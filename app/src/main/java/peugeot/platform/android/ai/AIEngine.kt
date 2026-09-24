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

    private val client = OkHttpClient.Builder()
        .connectTimeout(15L, TimeUnit.SECONDS)
        .readTimeout(30L, TimeUnit.SECONDS)
        .writeTimeout(15L, TimeUnit.SECONDS)
        .build()

    fun process(
        text: String,
        onResponse: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val userText = text.trim()

        if (userText.isBlank()) {
            onError("صدایی دریافت نشد")
            return
        }

        val apiKey = BuildConfig.OPENAI_API_KEY.trim()

        if (apiKey.isBlank()) {
            onError("کلید هوش مصنوعی تنظیم نشده است")
            return
        }

        val systemPrompt = """
            تو MRT هستی؛ دستیار هوشمند داخل خودرو پژو پارس.

            قوانین پاسخ:
            1. فقط و فقط به زبان فارسی پاسخ بده.
            2. پاسخ‌ها طبیعی، دوستانه و مناسب شنیدن با صدای بلند باشند.
            3. کوتاه و مستقیم صحبت کن؛ معمولاً یک تا سه جمله کافی است.
            4. از لحن رسمی و کتابی، عبارت‌های طولانی و توضیحات غیرضروری استفاده نکن.
            5. اگر کاربر سؤال عمومی پرسید، مثل یک دستیار هوشمند پاسخ بده.
            6. اگر اطلاعات خودرو در اختیار تو نیست، مقدار ساختگی برای خودرو اعلام نکن.
            7. از Markdown، فهرست‌های طولانی، ایموجی و نشانه‌های غیرضروری استفاده نکن.
            8. پاسخ را طوری بنویس که Text-to-Speech فارسی آن را روان بخواند.
        """.trimIndent()

        val json = JSONObject().apply {
            put("model", "gpt-4o-mini")

            put(
                "messages",
                JSONArray().apply {
                    put(
                        JSONObject().apply {
                            put("role", "system")
                            put("content", systemPrompt)
                        }
                    )

                    put(
                        JSONObject().apply {
                            put("role", "user")
                            put("content", userText)
                        }
                    )
                }
            )

            put("temperature", 0.6)
            put("max_tokens", 220)
        }

        val body = json.toString()
            .toRequestBody(
                "application/json".toMediaType()
            )

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
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

        client.newCall(request).enqueue(
            object : Callback {

                override fun onFailure(
                    call: Call,
                    e: IOException
                ) {
                    onError("خطای اینترنت")
                }

                override fun onResponse(
                    call: Call,
                    response: Response
                ) {
                    response.use {
                        val responseBody = response.body?.string().orEmpty()

                        if (!response.isSuccessful) {
                            val message = try {
                                JSONObject(responseBody)
                                    .optJSONObject("error")
                                    ?.optString("message")
                                    .orEmpty()
                            } catch (_: Exception) {
                                ""
                            }

                            if (message.isNotBlank()) {
                                onError("خطای هوش مصنوعی")
                            } else {
                                onError("خطای سرور هوش مصنوعی ${response.code}")
                            }
                            return
                        }

                        try {
                            val root = JSONObject(responseBody)
                            val choices = root.optJSONArray("choices")

                            if (choices == null || choices.length() == 0) {
                                onError("پاسخ هوش مصنوعی خالی است")
                                return
                            }

                            val message = choices
                                .getJSONObject(0)
                                .optJSONObject("message")

                            val answer = message
                                ?.optString("content")
                                ?.trim()
                                .orEmpty()

                            if (answer.isBlank()) {
                                onError("پاسخ هوش مصنوعی خالی است")
                                return
                            }

                            onResponse(answer)

                        } catch (_: Exception) {
                            onError("پاسخ نامعتبر دریافت شد")
                        }
                    }
                }
            }
        )
    }
}
