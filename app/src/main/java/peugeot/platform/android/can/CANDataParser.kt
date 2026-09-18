package peugeot.platform.android.can

object CANDataParser {

    fun parseSpeed(
        frame: CANFrame
    ): Int? {

        if (frame.data.size < 2) {
            return null
        }

        val raw =
            ((frame.data[0].toInt() and 0xFF) shl 8) or
            (frame.data[1].toInt() and 0xFF)

        return raw / 100
    }

    fun parseRpm(
        frame: CANFrame
    ): Int? {

        if (frame.data.size < 2) {
            return null
        }

        val raw =
            ((frame.data[0].toInt() and 0xFF) shl 8) or
            (frame.data[1].toInt() and 0xFF)

        return raw / 4
    }

    fun parseTemperature(
        frame: CANFrame
    ): Int? {

        if (frame.data.isEmpty()) {
            return null
        }

        return frame.data[0].toInt() - 40
    }
}
