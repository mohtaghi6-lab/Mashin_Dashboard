package peugeot.platform.android.vehicle

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



    init {

        CANReceiver.setFrameListener { frame ->

            processFrame(frame)

        }

    }




    /**
     * Start listening to CAN frames
     */
    fun start() {

        CANReceiver.connect()

        CANReceiver.startReceiving()

    }




    /**
     * Stop CAN receiving
     */
    fun stop() {

        CANReceiver.stopReceiving()

        CANReceiver.disconnect()

    }





    /**
     * Receive one CAN frame
     */
    private fun processFrame(
        frame: CANFrame
    ) {


        if (!CANDataParser.isValidFrame(frame)) {
            return
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



        onDataChanged(
            currentData.copy()
        )

    }





    /**
     * Current vehicle data
     */
    fun getCurrentData(): VehicleData {

        return currentData.copy()

    }





    /**
     * Enable demo mode.
     *
     * Used until real GPCU/CAN hardware exists.
     */
    fun useDemoMode() {


        demoMode = true



        currentData =
            VehicleData.demo()



        onDataChanged(
            currentData.copy()
        )

    }





    /**
     * Disable demo mode
     */
    fun disableDemoMode() {

        demoMode = false

    }





    /**
     * Inject fake CAN frame.
     *
     * Useful for testing dashboard.
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
     * Reset everything.
     */
    fun reset() {


        CANReceiver.reset()



        currentData =
            VehicleData.demo()



        onDataChanged(
            currentData.copy()
        )

    }

}
