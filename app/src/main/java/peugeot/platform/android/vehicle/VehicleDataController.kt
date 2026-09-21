package peugeot.platform.android.vehicle

import peugeot.platform.android.can.CANDataParser
import peugeot.platform.android.can.CANFrame

/**
 * Central controller for vehicle data.
 *
 * Data flow:
 *
 * CAN / GPCU
 *      ↓
 * CANFrame
 *      ↓
 * CANDataParser
 *      ↓
 * VehicleDataController
 *      ↓
 * VehicleData
 *      ↓
 * Dashboard / UI
 *
 * IMPORTANT:
 * The CAN decoder currently uses generic parsing rules.
 * Peugeot Pars / GPCU-specific CAN IDs will be added
 * when the real CAN/GPCU interface is available.
 */
class VehicleDataController(
    private val onDataChanged: (VehicleData) -> Unit
) {

    private var currentData = VehicleData()
    private var demoMode = false

    /**
     * Receives a CAN frame and tries to extract vehicle values.
     */
    fun updateFromCAN(frame: CANFrame) {

        if (!CANDataParser.isValidFrame(frame)) {
            return
        }

        demoMode = false

        var updatedData = currentData.copy(
            canConnected = true,
            lastUpdate = System.currentTimeMillis()
        )

        CANDataParser.parseSpeed(frame)?.let { speed ->
            updatedData = updatedData.copy(
                speedKmh = speed
            )
        }

        CANDataParser.parseRpm(frame)?.let { rpm ->
            updatedData = updatedData.copy(
                rpm = rpm
            )
        }

        CANDataParser.parseTemperature(frame)?.let { temperature ->
            updatedData = updatedData.copy(
                engineTempC = temperature
            )
        }

        currentData = updatedData

        publish()
    }

    /**
     * Enables demo mode.
     *
     * This is only for UI testing until real CAN/GPCU
     * hardware is connected.
     */
    fun useDemoMode() {

        demoMode = true

        currentData = VehicleData.demo()

        publish()
    }

    /**
     * Disables demo mode.
     */
    fun disableDemoMode() {

        demoMode = false

        currentData = currentData.copy(
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Returns whether demo mode is active.
     */
    fun isDemoMode(): Boolean {
        return demoMode
    }

    /**
     * Returns the latest vehicle data.
     */
    fun getCurrentData(): VehicleData {
        return currentData
    }

    /**
     * Updates CAN connection state.
     */
    fun setCANConnected(
        connected: Boolean
    ) {

        currentData = currentData.copy(
            canConnected = connected,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates OBD connection state.
     */
    fun setOBDConnected(
        connected: Boolean
    ) {

        currentData = currentData.copy(
            obdConnected = connected,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates vehicle speed manually.
     *
     * Useful for future GPCU/OBD adapters.
     */
    fun setSpeed(
        speedKmh: Int
    ) {

        currentData = currentData.copy(
            speedKmh = speedKmh.coerceIn(0, 300),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates engine RPM manually.
     */
    fun setRpm(
        rpm: Int
    ) {

        currentData = currentData.copy(
            rpm = rpm.coerceIn(0, 8000),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates engine temperature.
     */
    fun setEngineTemperature(
        temperatureC: Int
    ) {

        currentData = currentData.copy(
            engineTempC = temperatureC.coerceIn(-40, 150),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates fuel percentage.
     */
    fun setFuelPercent(
        percent: Int
    ) {

        currentData = currentData.copy(
            fuelPercent = percent.coerceIn(0, 100),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates battery voltage.
     */
    fun setBatteryVoltage(
        voltage: Float
    ) {

        currentData = currentData.copy(
            batteryVoltage = voltage.coerceIn(0f, 32f),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates odometer.
     */
    fun setOdometer(
        kilometers: Int
    ) {

        currentData = currentData.copy(
            odometerKm = kilometers.coerceAtLeast(0),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates ECU error count.
     */
    fun setECUErrorCount(
        count: Int
    ) {

        currentData = currentData.copy(
            ecuErrorCount = count.coerceAtLeast(0),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates Check Engine warning.
     */
    fun setCheckEngine(
        enabled: Boolean
    ) {

        currentData = currentData.copy(
            checkEngine = enabled,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates ABS warning.
     */
    fun setABSWarning(
        enabled: Boolean
    ) {

        currentData = currentData.copy(
            absWarning = enabled,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Updates Airbag warning.
     */
    fun setAirbagWarning(
        enabled: Boolean
    ) {

        currentData = currentData.copy(
            airbagWarning = enabled,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Resets vehicle data to a clean state.
     */
    fun reset() {

        demoMode = false

        currentData = VehicleData(
            speedKmh = 0,
            rpm = 0,
            engineTempC = 0,
            batteryVoltage = 0f,
            fuelPercent = 0,
            odometerKm = 0,
            canConnected = false,
            obdConnected = false,
            ecuErrorCount = 0,
            checkEngine = false,
            absWarning = false,
            airbagWarning = false,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * Sends the latest data to the UI.
     */
    private fun publish() {

        onDataChanged(currentData)
    }
}
