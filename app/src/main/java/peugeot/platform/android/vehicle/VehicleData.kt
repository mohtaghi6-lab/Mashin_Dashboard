package peugeot.platform.android.vehicle

data class VehicleData(
    val speedKmh: Int = 0,
    val rpm: Int = 0,
    val engineTempC: Int = 0,
    val batteryVoltage: Float = 0f,
    val fuelPercent: Int = 0,
    val odometerKm: Int = 0,

    // Connection
    val canConnected: Boolean = false,
    val obdConnected: Boolean = false,

    // ECU
    val ecuErrorCount: Int = 0,

    // Warning lights
    val checkEngine: Boolean = false,
    val absWarning: Boolean = false,
    val airbagWarning: Boolean = false,

    // Runtime
    val lastUpdate: Long = 0L
) {

    companion object {

        fun demo(): VehicleData {
            return VehicleData(
                speedKmh = 86,
                rpm = 2350,
                engineTempC = 91,
                batteryVoltage = 13.9f,
                fuelPercent = 68,
                odometerKm = 128540,

                canConnected = false,
                obdConnected = false,

                ecuErrorCount = 0,

                checkEngine = false,
                absWarning = false,
                airbagWarning = false,

                lastUpdate = System.currentTimeMillis()
            )
        }
    }
}
