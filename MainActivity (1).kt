package peugeot.platform.android.vehicle

import android.os.Handler
import android.os.Looper

import peugeot.platform.android.can.CANDataParser
import peugeot.platform.android.can.CANFrame
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

    private val handler =
        Handler(Looper.getMainLooper())

    private var demoSpeed =
        0

    private var demoRpm =
        850

    private var demoTemperature =
        82

    private var demoFuel =
        72

    private var demoVoltage =
        13.9f

    private var speedDirection =
        1

    init {

        CANReceiver.setFrameListener { frame ->

            processFrame(frame)

        }
    }


    /*
     * =========================================================
     * START CAN
     * =========================================================
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
        }
    }


    /*
     * =========================================================
     * STOP
     * =========================================================
     */

    fun stop() {

        started = false

        stopDemo()

        try {

            CANReceiver.stopReceiving()

            CANReceiver.disconnect()

        } catch (_: Exception) {
        }
    }


    /*
     * =========================================================
     * CAN FRAME
     * =========================================================
     */

    private fun processFrame(
        frame: CANFrame
    ) {

        if (!CANDataParser.isValidFrame(frame)) {
            return
        }

        /*
         * اگر CAN واقعی اطلاعات فرستاد،
         * Demo متوقف می‌شود.
         */
        if (demoMode) {

            demoMode = false

            stopDemo()
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
            ?.let { temperature ->

                updated =
                    updated.copy(
                        engineTempC = temperature
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


        currentData =
            updated.copy(
                canConnected = true,
                lastUpdate = System.currentTimeMillis()
            )


        notifyDataChanged()
    }


    /*
     * =========================================================
     * CURRENT DATA
     * =========================================================
     */

    fun getCurrentData(): VehicleData {

        return currentData.copy()
    }


    /*
     * =========================================================
     * LIVE DEMO MODE
     * =========================================================
     */

    fun useDemoMode() {

        demoMode = true

        started = true

        demoSpeed =
            0

        demoRpm =
            850

        demoTemperature =
            82

        demoFuel =
            72

        demoVoltage =
            13.9f

        speedDirection =
            1


        currentData =
            VehicleData.demo().copy(

                speedKmh =
                    demoSpeed,

                rpm =
                    demoRpm,

                engineTempC =
                    demoTemperature,

                batteryVoltage =
                    demoVoltage,

                fuelPercent =
                    demoFuel,

                canConnected =
                    false,

                lastUpdate =
                    System.currentTimeMillis()
            )


        notifyDataChanged()

        startDemo()
    }


    /*
     * =========================================================
     * DEMO LOOP
     * =========================================================
     */

    private val demoRunnable =
        object : Runnable {

            override fun run() {

                if (!demoMode) {
                    return
                }

                updateDemoData()

                handler.postDelayed(
                    this,
                    DEMO_INTERVAL
                )
            }
        }


    private fun startDemo() {

        handler.removeCallbacks(
            demoRunnable
        )

        handler.post(
            demoRunnable
        )
    }


    private fun stopDemo() {

        handler.removeCallbacks(
            demoRunnable
        )
    }


    /*
     * =========================================================
     * UPDATE DEMO DATA
     * =========================================================
     */

    private fun updateDemoData() {

        /*
         * SPEED
         *
         * 0 -> 120 -> 0
         */
        demoSpeed +=
            2 * speedDirection


        if (demoSpeed >= 120) {

            demoSpeed =
                120

            speedDirection =
                -1
        }


        if (demoSpeed <= 0) {

            demoSpeed =
                0

            speedDirection =
                1
        }


        /*
         * RPM
         *
         * تقریبی و متناسب با سرعت
         */
        val targetRpm =
            850 +
                    (demoSpeed * 30)


        if (demoRpm < targetRpm) {

            demoRpm +=
                180

        } else if (demoRpm > targetRpm) {

            demoRpm -=
                180
        }


        if (demoRpm < 850) {
            demoRpm =
                850
        }


        if (demoRpm > 4300) {
            demoRpm =
                4300
        }


        /*
         * ENGINE TEMPERATURE
         */
        demoTemperature =
            82 +
                    (demoSpeed / 15)


        if (demoTemperature > 96) {
            demoTemperature =
                96
        }


        /*
         * FUEL
         */
        demoFuel -=
            1


        if (demoFuel < 65) {

            demoFuel =
                72
        }


        /*
         * BATTERY
         */
        demoVoltage =
            if (demoRpm > 1500) {

                14.1f

            } else {

                13.7f
            }


        /*
         * UPDATE VEHICLE DATA
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
                    false,

                lastUpdate =
                    System.currentTimeMillis()
            )


        notifyDataChanged()
    }


    /*
     * =========================================================
     * NOTIFY UI
     * =========================================================
     */

    private fun notifyDataChanged() {

        onDataChanged(
            currentData.copy()
        )
    }


    /*
     * =========================================================
     * TEST CAN FRAME
     * =========================================================
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


    /*
     * =========================================================
     * DISABLE DEMO
     * =========================================================
     */

    fun disableDemoMode() {

        demoMode =
            false

        stopDemo()
    }


    /*
     * =========================================================
     * RESET
     * =========================================================
     */

    fun reset() {

        stopDemo()

        try {

            CANReceiver.reset()

        } catch (_: Exception) {
        }


        demoMode =
            true

        started =
            true

        demoSpeed =
            0

        demoRpm =
            850

        demoTemperature =
            82

        demoFuel =
            72

        demoVoltage =
            13.9f

        speedDirection =
            1


        currentData =
            VehicleData.demo().copy(

                speedKmh =
                    0,

                rpm =
                    850,

                engineTempC =
                    82,

                fuelPercent =
                    72,

                batteryVoltage =
                    13.9f,

                canConnected =
                    false,

                lastUpdate =
                    System.currentTimeMillis()
            )


        notifyDataChanged()

        startDemo()
    }


    companion object {

        private const val DEMO_INTERVAL =
            250L

    }
}
