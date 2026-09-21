package peugeot.platform.android.can

/**
 * Represents one CAN bus frame.
 *
 * This is a generic CAN frame model and does not contain
 * Peugeot Pars specific CAN mappings.
 */
data class CANFrame(
    val id: Int,
    val data: ByteArray,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Central CAN frame receiver.
 *
 * Current role:
 * - Maintains logical CAN connection state.
 * - Receives CAN frames from future hardware.
 * - Stores recent frames.
 * - Notifies listeners about received frames.
 * - Provides test/simulation support.
 *
 * Future architecture:
 *
 * CAN / GPCU / USB Adapter
 *          ↓
 *    CANReceiver
 *          ↓
 *       CANFrame
 *          ↓
 *    CANDataParser
 *          ↓
 * VehicleDataController
 *          ↓
 *     VehicleData
 */
object CANReceiver {

    private const val MAX_FRAME_HISTORY = 500

    private var connected = false
    private var receiving = false

    private val frames = mutableListOf<CANFrame>()

    private var frameListener: ((CANFrame) -> Unit)? = null

    /**
     * Starts the logical CAN receiver.
     *
     * This does not open real CAN hardware yet.
     */
    @Synchronized
    fun connect(): Boolean {
        connected = true
        receiving = true
        return true
    }

    /**
     * Disconnects the receiver.
     */
    @Synchronized
    fun disconnect() {
        receiving = false
        connected = false
        frames.clear()
    }

    /**
     * Returns whether the receiver is connected.
     */
    @Synchronized
    fun isConnected(): Boolean {
        return connected
    }

    /**
     * Returns whether frame reception is active.
     */
    @Synchronized
    fun isReceiving(): Boolean {
        return receiving
    }

    /**
     * Registers a callback for incoming frames.
     */
    @Synchronized
    fun setFrameListener(
        listener: ((CANFrame) -> Unit)?
    ) {
        frameListener = listener
    }

    /**
     * Receives a complete CAN frame.
     */
    @Synchronized
    fun receive(
        frame: CANFrame
    ) {
        if (!connected || !receiving) {
            return
        }

        if (!isValidFrame(frame)) {
            return
        }

        val safeFrame = frame.copy(
            data = frame.data.copyOf()
        )

        frames.add(safeFrame)

        while (frames.size > MAX_FRAME_HISTORY) {
            frames.removeAt(0)
        }

        frameListener?.invoke(safeFrame)
    }

    /**
     * Receives a CAN frame from raw ID + data.
     */
    @Synchronized
    fun receive(
        id: Int,
        data: ByteArray,
        timestamp: Long = System.currentTimeMillis()
    ) {
        receive(
            CANFrame(
                id = id,
                data = data.copyOf(),
                timestamp = timestamp
            )
        )
    }

    /**
     * Returns the latest received frame.
     */
    @Synchronized
    fun getLastFrame(): CANFrame? {
        val frame = frames.lastOrNull() ?: return null

        return frame.copy(
            data = frame.data.copyOf()
        )
    }

    /**
     * Returns a safe copy of all stored frames.
     */
    @Synchronized
    fun getFrames(): List<CANFrame> {
        return frames.map {
            it.copy(
                data = it.data.copyOf()
            )
        }
    }

    /**
     * Returns the number of stored frames.
     */
    @Synchronized
    fun getFrameCount(): Int {
        return frames.size
    }

    /**
     * Clears received frame history.
     */
    @Synchronized
    fun clearFrames() {
        frames.clear()
    }

    /**
     * Starts receiving frames without reconnecting.
     */
    @Synchronized
    fun startReceiving(): Boolean {
        if (!connected) {
            return false
        }

        receiving = true
        return true
    }

    /**
     * Stops receiving frames without disconnecting.
     */
    @Synchronized
    fun stopReceiving() {
        receiving = false
    }

    /**
     * Validates basic CAN frame structure.
     *
     * Standard CAN payload is 0..8 bytes.
     */
    private fun isValidFrame(
        frame: CANFrame
    ): Boolean {
        if (frame.id < 0) {
            return false
        }

        if (frame.data.size > 8) {
            return false
        }

        return true
    }

    /**
     * Converts a frame to readable hexadecimal text.
     */
    fun frameToString(
        frame: CANFrame
    ): String {
        val hex = frame.data.joinToString(" ") {
            "%02X".format(it.toInt() and 0xFF)
        }

        return "ID=${frame.id} DATA=$hex"
    }

    /**
     * Returns stored frames as readable debug strings.
     */
    @Synchronized
    fun getDebugFrames(): List<String> {
        return frames.map {
            frameToString(it)
        }
    }

    /**
     * Simulates a CAN frame for software testing.
     *
     * This is NOT real Peugeot Pars CAN data.
     */
    @Synchronized
    fun simulateFrame(
        id: Int,
        data: ByteArray
    ) {
        if (!connected) {
            connect()
        }

        receive(
            CANFrame(
                id = id,
                data = data.copyOf(),
                timestamp = System.currentTimeMillis()
            )
        )
    }

    /**
     * Completely resets the receiver.
     */
    @Synchronized
    fun reset() {
        receiving = false
        connected = false
        frames.clear()
        frameListener = null
    }
}
