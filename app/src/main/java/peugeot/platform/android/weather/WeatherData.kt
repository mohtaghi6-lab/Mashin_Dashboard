package peugeot.platform.android.weather

data class WeatherData(
    val city: String,
    val temperatureC: Int,
    val feelsLikeC: Int,
    val weatherCode: Int,
    val windKmh: Int,
    val updatedAt: Long
) {
    val description: String
        get() = when (weatherCode) {
            0 -> "صاف"
            1, 2 -> "کمی ابری"
            3 -> "ابری"
            45, 48 -> "مه‌آلود"
            51, 53, 55, 56, 57 -> "نم‌نم باران"
            61, 63, 65, 66, 67 -> "بارانی"
            71, 73, 75, 77 -> "برفی"
            80, 81, 82 -> "رگبار"
            85, 86 -> "رگبار برف"
            95 -> "رعدوبرق"
            96, 99 -> "رعدوبرق و تگرگ"
            else -> "آب‌وهوا"
        }

    val icon: String
        get() = when (weatherCode) {
            0 -> "☀"
            1, 2 -> "☁"
            3, 45, 48 -> "☁"
            51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> "☂"
            71, 73, 75, 77, 85, 86 -> "❄"
            95, 96, 99 -> "⚡"
            else -> "•"
        }

    companion object {
        fun demo(): WeatherData =
            WeatherData(
                city = "تهران",
                temperatureC = 24,
                feelsLikeC = 25,
                weatherCode = 1,
                windKmh = 9,
                updatedAt = System.currentTimeMillis()
            )
    }
}
