package peugeot.platform.android.can


object CANDataParser {


    fun isValidFrame(
        frame: CANFrame
    ): Boolean {

        return frame.id >= 0 &&
                frame.data.size <= 8

    }



    fun parseSpeed(
        frame: CANFrame
    ): Int? {


        if (!isValidFrame(frame)) {
            return null
        }


        if (frame.data.size < 2) {
            return null
        }


        val raw =
            ((frame.data[0].toInt() and 0xFF) shl 8) or
                    (frame.data[1].toInt() and 0xFF)



        val speed =
            raw / 100



        return speed.takeIf {

            it in 0..300

        }

    }





    fun parseRpm(
        frame: CANFrame
    ): Int? {


        if (!isValidFrame(frame)) {
            return null
        }


        if (frame.data.size < 2) {
            return null
        }



        val raw =
            ((frame.data[0].toInt() and 0xFF) shl 8) or
                    (frame.data[1].toInt() and 0xFF)



        val rpm =
            raw / 4



        return rpm.takeIf {

            it in 0..8000

        }

    }





    fun parseTemperature(
        frame: CANFrame
    ): Int? {


        if (!isValidFrame(frame)) {
            return null
        }


        if (frame.data.isEmpty()) {
            return null
        }



        val temperature =
            (frame.data[0].toInt() and 0xFF) - 40



        return temperature.takeIf {

            it in -40..150

        }

    }





    fun parseFuel(
        frame: CANFrame
    ): Int? {


        if (!isValidFrame(frame)) {
            return null
        }


        if (frame.data.isEmpty()) {
            return null
        }


        val fuel =
            frame.data[0].toInt() and 0xFF



        return fuel.takeIf {

            it in 0..100

        }

    }





    fun parseBatteryVoltage(
        frame: CANFrame
    ): Float? {


        if (!isValidFrame(frame)) {
            return null
        }


        if (frame.data.isEmpty()) {
            return null
        }



        return (frame.data[0].toInt() and 0xFF)
            .toFloat() / 10f

    }





    fun getFrameDescription(
        frame: CANFrame
    ): String {


        val bytes =
            frame.data.joinToString(" ") {

                "%02X".format(
                    it.toInt() and 0xFF
                )

            }



        return "ID=${frame.id} DATA=$bytes"

    }


}
