package peugeot.platform.android.vehicle

import android.os.Handler
import android.os.Looper

import peugeot.platform.android.can.CANFrame
import peugeot.platform.android.can.CANDataParser
import peugeot.platform.android.can.CANReceiver


class VehicleDataController(
    private val onDataChanged: (VehicleData) -> Unit
) {

    private var currentData =
        VehicleData.demo()

    private var demoMode =
        false

    private var started =
        false

    private val demoHandler =
        Handler(Looper.getMainLooper())

    private var demoSpeed =
        0f

    private var demoRpm =
        850f

    private var demoTemperature =
        82f

    private var demoFuel =
        72f

    private var demoVoltage =
        13.9f

    private var demoSpeedDirection =
        1f

    private var demoRpmDirection =
        1f

    private val demoRunnable =
        object : Runnable {

            override fun run() {

                if (!demoMode) {
                    return
                }

                updateDemoData()

                demoHandler.postDelayed(
                    this,
                    DEMO_UPDATE_INTERVAL
                )
            }
        }


    init {

        CANReceiver.setFrameListener { frame ->

            processFrame(frame)

        }

    }


    /**
     * Start listening to CAN frames.
     */
    fun start() {

        if (started) {
            return
        }

        started = true

        try {

            CANReceiver.connect()

            CANReceiver.startReceiving()

        } catch (_: Exception) {
            // در حالت Demo حتی در صورت نبود سخت‌افزار CAN
            // برنامه باید بدون مشکل ادامه پیدا کند.
        }

    }


    /**
     * Stop CAN receiving and Demo updates.
     */
    fun stop() {

        started = false

        stopDemoLoop()

        try {

            CANReceiver.stopReceiving()

            CANReceiver.disconnect()

        } catch (_: Exception) {
        }

    }


    /**
     * Receive one CAN frame.
     */
    private fun processFrame(
        frame: CANFrame
    ) {

        if (!CANDataParser.isValidFrame(frame)) {
            return
        }

        /*
         * وقتی CAN واقعی فریم می‌فرستد،
         * Demo باید متوقف شود.
         */
        if (demoMode) {

            demoMode = false

            stopDemoLoop()
        }


        var updated =
            currentData


        CANDataParser.parseSpeed(frame)
            ?.let { speed ->

                updated =
                    updated.copy(
                        speedKmh = speed
                    )

            }


        CANDataParser.parseRpm(frame)
            ?.let { rpm ->

                updated =
                    updated.copy(
                        rpm = rpm
                    )

            }


        CANDataParser.parseTemperature(frame)
            ?.let { temp ->

                updated =
                    updated.copy(
                        engineTempC = temp
                    )

            }


        CANDataParser.parseFuel(frame)
            ?.let { fuel ->

                updated =
                    updated.copy(
                        fuelPercent = fuel
                    )

            }


        CANDataParser.parseBatteryVoltage(frame)
            ?.let { voltage ->

                updated =
                    updated.copy(
                        batteryVoltage = voltage
                    )

            }


        updated =
            updated.copy(
                canConnected = true
            )


        currentData =
            updated


        notifyDataChanged()

    }


    /**
     * Current vehicle data.
     */
    fun getCurrentData(): VehicleData {

        return currentData.copy()

    }


    /**
     * Enable live Demo Mode.
     *
     * This mode is used until real CAN/GPCU hardware
     * is connected.
     *
     * Data is continuously updated so the dashboard
     * can be tested without vehicle hardware.
     */
    fun useDemoMode() {

        demoMode = true

        started = true

        demoSpeed =
            0f

        demoRpm =
            850f

        demoTemperature =
            82f

        demoFuel =
            72f

        demoVoltage =
            13.9f

        demoSpeedDirection =
            1f

        demoRpmDirection =
            1f


        currentData =
            VehicleData.demo().copy(
                speedKmh = demoSpeed,
                rpm = demoRpm,
                engineTempC = demoTemperature,
                fuelPercent = demoFuel,
                batteryVoltage = demoVoltage,
                canConnected = false
            )


        notifyDataChanged()

        startDemoLoop()

    }


    /**
     * Disable Demo Mode.
     */
    fun disableDemoMode() {

        demoMode = false

        stopDemoLoop()

    }


    /**
     * Start live Demo updates.
     */
    private fun startDemoLoop() {

        demoHandler.removeCallbacks(
            demoRunnable
        )

        demoHandler.post(
            demoRunnable
        )

    }


    /**
     * Stop live Demo updates.
     */
    private fun stopDemoLoop() {

        demoHandler.removeCallbacks(
            demoRunnable
        )

    }


    /**
     * Generate realistic demo vehicle data.
     *
     * Speed and RPM continuously move up and down
     * instead of remaining static.
     */
    private fun updateDemoData() {

        /*
         * -----------------------------------------
         * SPEED
         * -----------------------------------------
         *
         * 0 -> 120 km/h -> 0
         */
        demoSpeed +=
            1.2f * demoSpeedDirection


        if (demoSpeed >= 120f) {

            demoSpeed =
                120f

            demoSpeedDirection =
                -1f

        }


        if (demoSpeed <= 0f) {

            demoSpeed =
                0f

            demoSpeedDirection =
                1f

        }


        /*
         * -----------------------------------------
         * RPM
         * -----------------------------------------
         *
         * RPM roughly follows vehicle speed.
         */
        val targetRpm =
            850f +
                    (demoSpeed * 32f)


        if (demoRpm < targetRpm) {

            demoRpm +=
                180f

        } else {

            demoRpm -=
                180f

        }


        /*
         * Keep RPM inside realistic demo limits.
         */
        if (demoRpm < 850f) {
            demoRpm = 850f
        }

        if (demoRpm > 4200f) {
            demoRpm = 4200f
        }


        /*
         * -----------------------------------------
         * ENGINE TEMPERATURE
         * -----------------------------------------
         */
        val targetTemperature =
            82f +
                    (demoSpeed * 0.045f)


        demoTemperature +=
            (targetTemperature - demoTemperature) * 0.08f


        if (demoTemperature < 80f) {
            demoTemperature = 80f
        }

        if (demoTemperature > 96f) {
            demoTemperature = 96f
        }


        /*
         * -----------------------------------------
         * FUEL
         * -----------------------------------------
         *
         * Slowly decreases during demo driving.
         */
        demoFuel -=
            0.002f


        if (demoFuel < 68f) {
            demoFuel = 72f
        }


        /*
         * -----------------------------------------
         * BATTERY VOLTAGE
         * -----------------------------------------
         */
        demoVoltage =
            13.7f +
                    ((demoRpm - 850f) / 3350f) * 0.5f


        if (demoVoltage < 13.5f) {
            demoVoltage = 13.5f
        }

        if (demoVoltage > 14.4f) {
            demoVoltage = 14.4f
        }


        /*
         * -----------------------------------------
         * CREATE NEW VEHICLE DATA
         * -----------------------------------------
         */
        currentData =
            currentData.copy(

                speedKmh =
                    demoSpeed,

                rpm =
                    demoRpm,

                engineTempC =
                    demoTemperature,

                fuelPercent =
                    demoFuel,

                batteryVoltage =
                    demoVoltage,

                canConnected =
                    false
            )


        /*
         * Send data to Dashboard.
         */
        notifyDataChanged()

    }


    /**
     * Send current data to all UI consumers.
     */
    private fun notifyDataChanged() {

        onDataChanged(
            currentData.copy()
        )

    }


    /**
     * Inject fake CAN frame.
     *
     * Useful for testing dashboard and parser.
     */
    fun testFrame(
        id: Int,
        data: ByteArray
    ) {

        CANReceiver.simulateFrame(
            id,
            data
        )

    }


    /**
     * Reset everything and return to live Demo Mode.
     */
    fun reset() {

        stopDemoLoop()

        try {

            CANReceiver.reset()

        } catch (_: Exception) {
        }


        demoMode = true

        started = true

        demoSpeed =
            0f

        demoRpm =
            850f

        demoTemperature =
            82f

        demoFuel =
            72f

        demoVoltage =
            13.9f

        demoSpeedDirection =
            1f

        demoRpmDirection =
            1f


        currentData =
            VehicleData.demo().copy(

                speedKmh =
                    demoSpeed,

                rpm =
                    demoRpm,

                engineTempC =
                    demoTemperature,

                fuelPercent =
                    demoFuel,

                batteryVoltage =
                    demoVoltage,

                canConnected =
                    false
            )


        notifyDataChanged()

        startDemoLoop()

    }


    companion object {

        private const val DEMO_UPDATE_INTERVAL =
            250L

    }

}
