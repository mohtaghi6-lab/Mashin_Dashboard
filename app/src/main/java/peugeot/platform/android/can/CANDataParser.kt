package peugeot.platform.android.can

import peugeot.platform.android.vehicle.VehicleData


/**
 * Converts CAN frames into VehicleData.
 *
 * Flow:
 *
 * CANReceiver
 *       ↓
 *    CANFrame
 *       ↓
 * CANDataParser
 *       ↓
 * VehicleData
 *
 * NOTE:
 * Current mappings are placeholders.
 * Real Peugeot Pars CAN IDs must be filled after
 * capturing GPCU/CAN traffic.
 */
object CANDataParser {


    /**
     * Last decoded vehicle values.
     */
    private var currentData =
        VehicleData.demo()



    /**
     * Parses one CAN frame.
     */
    fun parse(
        frame: CANFrame
    ): VehicleData {


        val id =
            frame.id


        val bytes =
            frame.data



        when(id) {


            /*
             * Speed message
             *
             * Example placeholder:
             *
             * Byte 0-1 = speed
             */
            CAN_IDS.SPEED -> {

                if(bytes.size >= 2) {

                    val raw =
                        ((bytes[0].toInt() and 0xFF) shl 8) or
                        (bytes[1].toInt() and 0xFF)


                    currentData =
                        currentData.copy(
                            speedKmh = raw / 100
                        )

                }

            }



            /*
             * RPM message
             *
             * Example:
             * Byte 0-1 RPM value
             */
            CAN_IDS.RPM -> {

                if(bytes.size >= 2) {


                    val raw =
                        ((bytes[0].toInt() and 0xFF) shl 8) or
                        (bytes[1].toInt() and 0xFF)



                    currentData =
                        currentData.copy(
                            rpm = raw
                        )

                }

            }



            /*
             * Engine temperature
             */
            CAN_IDS.ENGINE_TEMP -> {


                if(bytes.isNotEmpty()) {


                    val temp =
                        bytes[0].toInt() and 0xFF



                    currentData =
                        currentData.copy(
                            engineTempC = temp
                        )

                }

            }



            /*
             * Fuel level
             */
            CAN_IDS.FUEL -> {


                if(bytes.isNotEmpty()) {


                    val fuel =
                        bytes[0].toInt() and 0xFF



                    currentData =
                        currentData.copy(
                            fuelPercent = fuel
                        )

                }

            }



            /*
             * Battery voltage
             */
            CAN_IDS.BATTERY -> {


                if(bytes.isNotEmpty()) {


                    val voltage =
                        bytes[0].toInt()
                            .and(0xFF)
                            .toFloat()
                            /
                            10f



                    currentData =
                        currentData.copy(
                            batteryVoltage = voltage
                        )

                }

            }



            /*
             * Error / diagnostic frame
             */
            CAN_IDS.ERROR -> {


                currentData =
                    currentData.copy(
                        canConnected = true
                    )

            }


        }



        return currentData.copy()

    }



    /**
     * Parses multiple frames.
     */
    fun parseFrames(
        frames: List<CANFrame>
    ): VehicleData {


        frames.forEach { frame ->

            parse(frame)

        }


        return currentData.copy()

    }



    /**
     * Returns current decoded data.
     */
    fun getCurrentData(): VehicleData {

        return currentData.copy()

    }



    /**
     * Reset parser.
     */
    fun reset() {

        currentData =
            VehicleData.demo()

    }



    /**
     * Placeholder CAN identifiers.
     *
     * These are NOT confirmed Peugeot Pars IDs.
     *
     * They must be replaced after real CAN capture.
     */
    object CAN_IDS {


        const val SPEED =
            0x100


        const val RPM =
            0x101


        const val ENGINE_TEMP =
            0x102


        const val FUEL =
            0x103


        const val BATTERY =
            0x104


        const val ERROR =
            0x700

    }

}
