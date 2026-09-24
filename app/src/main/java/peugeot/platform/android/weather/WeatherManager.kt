package peugeot.platform.android.weather

import android.os.Handler
import android.os.Looper
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WeatherManager(
    private val onWeatherChanged: (WeatherData) -> Unit
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    private val handler = Handler(Looper.getMainLooper())
    private var stopped = false

    private val refreshRunnable = object : Runnable {
        override fun run() {
            if (stopped) return
            fetch()
            handler.postDelayed(this, 15 * 60 * 1000L)
        }
    }

    fun start() {
        stopped = false
        onWeatherChanged(WeatherData.demo())
        handler.removeCallbacks(refreshRunnable)
        handler.post(refreshRunnable)
    }

    fun stop() {
        stopped = true
        handler.removeCallbacks(refreshRunnable)
        client.dispatcher.cancelAll()
        client.connectionPool.evictAll()
    }

    private fun fetch() {
        // تهران به عنوان موقعیت پیش‌فرض تا زمانی که GPS خودرو به WeatherManager متصل شود.
        val latitude = 35.6892
        val longitude = 51.3890

        val url =
            "https://api.open-meteo.com/v1/forecast" +
                "?latitude=$latitude" +
                "&longitude=$longitude" +
                "&current=temperature_2m,apparent_temperature,weather_code,wind_speed_10m" +
                "&timezone=auto"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                // آخرین وضعیت معتبر روی داشبورد باقی می‌ماند.
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use {
                    if (!response.isSuccessful) return

                    try {
                        val current = JSONObject(response.body?.string().orEmpty())
                            .getJSONObject("current")

                        val data = WeatherData(
                            city = "تهران",
                            temperatureC = current.optDouble("temperature_2m", 24.0).toInt(),
                            feelsLikeC = current.optDouble("apparent_temperature", 24.0).toInt(),
                            weatherCode = current.optInt("weather_code", 0),
                            windKmh = current.optDouble("wind_speed_10m", 0.0).toInt(),
                            updatedAt = System.currentTimeMillis()
                        )

                        handler.post {
                            if (!stopped) onWeatherChanged(data)
                        }
                    } catch (_: Exception) {
                        // پاسخ نامعتبر؛ وضعیت قبلی حفظ می‌شود.
                    }
                }
            }
        })
    }
}
