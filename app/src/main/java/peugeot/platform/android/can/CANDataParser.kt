package peugeot.platform.android.can

/**
 * Converts raw CAN frames into safe, normalized vehicle values.
 *
 * IMPORTANT:
 * The Peugeot Pars / GPCU CAN IDs and byte layouts are intentionally
 * not hard-coded here until the real interface/protocol is available.
 *
 * The real vehicle mapping can later be added without changing
 * VehicleDataController or DashboardView.
 */
object CANDataParser {

    /**
     * Generic unsigned 16-bit big-endian value.
     */
    fun readUInt16BE(
        data: ByteArray,
        offset: Int = 0
    ): Int? {

        if (offset < 0 || offset + 1 >= data.size) {
            return null
        }

        return ((data[offset].toInt() and 0xFF) shl 8) or
            (data[offset + 1].toInt() and 0xFF)
    }

    /**
     * Generic unsigned 16-bit little-endian value.
     */
    fun readUInt16LE(
        data: ByteArray,
        offset: Int = 0
    ): Int? {

        if (offset < 0 || offset + 1 >= data.size) {
            return null
        }

        return (data[offset].toInt() and 0xFF) or
            ((data[offset + 1].toInt() and 0xFF) shl 8)
    }

    /**
     * Generic unsigned 8-bit value.
     */
    fun readUInt8(
        data: ByteArray,
        offset: Int = 0
    ): Int? {

        if (offset < 0 || offset >= data.size) {
            return null
        }

        return data[offset].toInt() and 0xFF
    }

    /**
     * Generic signed 8-bit value.
     */
    fun readInt8(
        data: ByteArray,
        offset: Int = 0
    ): Int? {

        if (offset < 0 || offset >= data.size) {
            return null
        }

        return data[offset].toInt()
    }

    /**
     * Generic unsigned 32-bit big-endian value.
     */
    fun readUInt32BE(
        data: ByteArray,
        offset: Int = 0
    ): Long? {

        if (offset < 0 || offset + 3 >= data.size) {
            return null
        }

        return ((data[offset].toLong() and 0xFFL) shl 24) or
            ((data[offset + 1].toLong() and 0xFFL) shl 16) or
            ((data[offset + 2].toLong() and 0xFFL) shl 8) or
            (data[offset + 3].toLong() and 0xFFL)
    }

    /**
     * Generic unsigned 32-bit little-endian value.
     */
    fun readUInt32LE(
        data: ByteArray,
        offset: Int = 0
    ): Long? {

        if (offset < 0 || offset + 3 >= data.size) {
            return null
        }

        return (data[offset].toLong() and 0xFFL) or
            ((data[offset + 1].toLong() and 0xFFL) shl 8) or
            ((data[offset + 2].toLong() and 0xFFL) shl 16) or
            ((data[offset + 3].toLong() and 0xFFL) shl 24)
    }

    /**
     * Parses speed in km/h.
     *
     * This is currently a generic decoder.
     * It is NOT claimed to be the confirmed Peugeot Pars/GPCU format.
     */
    fun parseSpeed(
        frame: CANFrame
    ): Int? {

        val raw = readUInt16BE(frame.data) ?: return null

        val speed = raw / 100

        return if (speed in 0..300) {
            speed
        } else {
            null
        }
    }

    /**
     * Parses engine RPM.
     *
     * This is currently a generic decoder.
     * It is NOT claimed to be the confirmed Peugeot Pars/GPCU format.
     */
    fun parseRpm(
        frame: CANFrame
    ): Int? {

        val raw = readUInt16BE(frame.data) ?: return null

        val rpm = raw / 4

        return if (rpm in 0..8000) {
            rpm
        } else {
            null
        }
    }

    /**
     * Parses engine temperature.
     *
     * Generic formula:
     * raw - 40
     *
     * The actual Peugeot Pars/GPCU formula must be confirmed
     * from real vehicle frames.
     */
    fun parseTemperature(
        frame: CANFrame
    ): Int? {

        val raw = readUInt8(frame.data) ?: return null

        val temperature = raw - 40

        return if (temperature in -40..150) {
            temperature
        } else {
            null
        }
    }

    /**
     * Parses a generic 0..255 value as percentage.
     */
    fun parsePercentage(
        frame: CANFrame,
        offset: Int = 0
    ): Int? {

        val raw = readUInt8(frame.data, offset) ?: return null

        return ((raw * 100) / 255).coerceIn(0, 100)
    }

    /**
     * Parses a generic voltage value.
     *
     * scale = volts per raw unit.
     */
    fun parseVoltage(
        frame: CANFrame,
        offset: Int = 0,
        scale: Float = 0.1f
    ): Float? {

        if (scale <= 0f) {
            return null
        }

        val raw = readUInt16BE(
            frame.data,
            offset
        ) ?: return null

        val voltage = raw * scale

        return if (voltage in 0f..32f) {
            voltage
        } else {
            null
        }
    }

    /**
     * Checks whether a CAN frame is structurally usable.
     */
    fun isValidFrame(
        frame: CANFrame
    ): Boolean {

        return frame.id >= 0 &&
            frame.data.isNotEmpty() &&
            frame.data.size <= 8
    }

    /**
     * Converts CAN payload to hexadecimal text.
     *
     * Example:
     * 01 2A FF 00
     */
    fun toHex(
        data: ByteArray
    ): String {

        return data.joinToString(" ") {
            "%02X".format(it.toInt() and 0xFF)
        }
    }
}
