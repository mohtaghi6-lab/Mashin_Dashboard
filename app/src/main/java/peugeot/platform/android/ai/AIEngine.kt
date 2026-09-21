package peugeot.platform.android.ai

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import peugeot.platform.android.BuildConfig
import java.io.IOException
import java.util.concurrent.TimeUnit


class AIEngine(
    private val context: Context
) {

    private val client =
        OkHttpClient.Builder()
            .connectTimeout(
                20,
                TimeUnit.SECONDS
            )
            .readTimeout(
                30,
                TimeUnit.SECONDS
            )
            .writeTimeout(
                30,
                TimeUnit.SECONDS
            )
            .build()


    @Volatile
    private var busy = false


    fun process(
        text: String,
        onResponse: (String) -> Unit,
        onError: (String) -> Unit
    ) {


        if (busy) {

            onError(
                "در حال پردازش درخواست قبلی هستم"
            )

            return
        }


        if (text.isBlank()) {

            onError(
                "متنی دریافت نشد"
            )

            return
        }


        val apiKey =
            BuildConfig.OPENAI_API_KEY


        if (apiKey.isBlank()) {

            onError(
                "کلید OpenAI تنظیم نشده است"
            )

            return
        }


        busy = true


        val json =
            JSONObject().apply {


                put(
                    "model",
                    "gpt-4o-mini"
                )


                put(
                    "temperature",
                    0.7
                )


                put(
                    "messages",
                    JSONArray().apply {


                        put(
                            JSONObject().apply {

                                put(
                                    "role",
                                    "system"
                                )

                                put(
                                    "content",
                                    """
                                    تو دستیار هوشمند خودرو پژو پارس هستی.
                                    فقط فارسی صحبت کن.
                                    جواب‌ها کوتاه، طبیعی و مناسب رانندگی باشند.
                                    رسمی و کتابی صحبت نکن.
                                    """.trimIndent()
                                )
                            }
                        )


                        put(
                            JSONObject().apply {

                                put(
                                    "role",
                                    "user"
                                )

                                put(
                                    "content",
                                    text.trim()
                                )
                            }
                        )

                    }
                )

            }



        val body =
            json.toString()
                .toRequestBody(
                    "application/json".toMediaType()
                )


        val request =
            Request.Builder()
                .url(
                    "https://api.openai.com/v1/chat/completions"
                )
                .addHeader(
                    "Authorization",
                    "Bearer $apiKey"
                )
                .addHeader(
                    "Content-Type",
                    "application/json"
                )
                .post(body)
                .build()



        client.newCall(request)
            .enqueue(
                object : Callback {


                    override fun onFailure(
                        call: Call,
                        e: IOException
                    ) {

                        busy = false

                        onError(
                            "اتصال اینترنت برقرار نیست"
                        )
                    }



                    override fun onResponse(
                        call: Call,
                        response: Response
                    ) {


                        response.use {


                            busy = false


                            if (!response.isSuccessful) {

                                onError(
                                    "خطای سرور هوش مصنوعی ${response.code}"
                                )

                                return
                            }


                            try {


                                val result =
                                    response.body
                                        ?.string()
                                        ?: ""


                                val answer =
                                    JSONObject(result)
                                        .getJSONArray(
                                            "choices"
                                        )
                                        .getJSONObject(0)
                                        .getJSONObject(
                                            "message"
                                        )
                                        .getString(
                                            "content"
                                        )
                                        .trim()



                                if (answer.isEmpty()) {

                                    onError(
                                        "جوابی دریافت نشد"
                                    )

                                } else {

                                    onResponse(
                                        answer
                                    )

                                }


                            } catch (
                                e: Exception
                            ) {

                                onError(
                                    "خطا در پردازش پاسخ"
                                )

                            }
                        }
                    }
                }
            )
    }
}
