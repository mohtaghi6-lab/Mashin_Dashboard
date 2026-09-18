package peugeot.platform.android.vehicle

data class VehicleData(
    val speedKmh: Int = 0,
    val rpm: Int = 0,
    val engineTempC: Int = 90,
    val batteryVoltage: Float = 12.6f,
    val fuelPercent: Int = 72,
    val odometerKm: Int = 128540,
    val canConnected: Boolean = false,
    val obdConnected: Boolean = false,
    val ecuErrorCount: Int = 0
) {
    companion object {
        fun demo() = VehicleData(
            speedKmh = 86,
            rpm = 2350,
            engineTempC = 91,
            batteryVoltage = 13.9f,
            fuelPercent = 68
        )
    }
}
