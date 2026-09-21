package peugeot.platform.android.ai

import android.content.Context
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import peugeot.platform.android.BuildConfig
import java.io.IOException


class AIEngine(
    private val context: Context
) {


    private val client =
        OkHttpClient()



    fun process(
        text: String,
        onResponse: (String) -> Unit,
        onError: (String) -> Unit
    ) {


        if (
            text.isBlank()
        ) {

            onError(
                "صدایی دریافت نشد"
            )

            return

        }



        val apiKey =
            BuildConfig.OPENAI_API_KEY



        if (
            apiKey.isBlank()
        ) {

            onError(
                "کلید هوش مصنوعی تنظیم نشده است"
            )

            return

        }



        val json =
            JSONObject().apply {


                put(
                    "model",
                    "gpt-4o-mini"
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
                                    تو MRT هستی.
                                    دستیار هوشمند خودرو پژو پارس.
                                    فقط فارسی جواب بده.
                                    کوتاه، طبیعی و دوستانه صحبت کن.
                                    مثل یک دستیار داخل خودرو پاسخ بده.
                                    """
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
                                    text
                                )

                            }

                        )


                    }

                )

            }



        val body =
            json.toString()
                .toRequestBody(
                    "application/json"
                        .toMediaType()
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

                .post(
                    body
                )

                .build()



        client.newCall(
            request
        )
            .enqueue(

                object : Callback {


                    override fun onFailure(
                        call: Call,
                        e: IOException
                    ) {

                        onError(
                            "خطای اینترنت"
                        )

                    }



                    override fun onResponse(
                        call: Call,
                        response: Response
                    ) {


                        response.use {


                            if (
                                !response.isSuccessful
                            ) {

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

                                    JSONObject(
                                        result
                                    )

                                        .getJSONArray(
                                            "choices"
                                        )

                                        .getJSONObject(
                                            0
                                        )

                                        .getJSONObject(
                                            "message"
                                        )

                                        .getString(
                                            "content"
                                        )



                                onResponse(
                                    answer.trim()
                                )


                            }
                            catch (
                                e: Exception
                            ) {


                                onError(
                                    "پاسخ نامعتبر دریافت شد"
                                )


                            }


                        }


                    }


                }

            )

    }

}
