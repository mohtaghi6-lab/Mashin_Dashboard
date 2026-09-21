package peugeot.platform.android.can

/**
 * Central CAN frame receiver.
 *
 * Current role:
 * - Keeps the CAN connection state.
 * - Accepts CAN frames from a future hardware adapter.
 * - Stores a limited history of received frames.
 * - Notifies listeners when a new frame arrives.
 * - Can be used in demo/test mode without real CAN hardware.
 *
 * Future hardware flow:
 *
 * USB / Serial / CAN Adapter / GPCU
 *              ↓
 *        CANReceiver.receive()
 *              ↓
 *            CANFrame
 *              ↓
 *        CANDataParser
 *              ↓
 *      VehicleDataController
 *
 * IMPORTANT:
 * No Peugeot Pars CAN IDs are hard-coded here.
 * The real IDs and byte mappings belong in CANDataParser
 * after the actual GPCU/CAN protocol is known.
 */
object CANReceiver {

    private const val MAX_FRAME_HISTORY = 500

    private var connected = false

    private var receiving = false

    private val frames =
        mutableListOf<CANFrame>()

    private var frameListener:
            ((CANFrame) -> Unit)? = null

    /**
     * Connects the logical CAN receiver.
     *
     * This does NOT open physical CAN hardware yet.
     * It prepares the receiver for an external adapter.
     */
    @Synchronized
    fun connect(): Boolean {

        connected = true
        receiving = true

        return true
    }

    /**
     * Disconnects the receiver and clears the current
     * frame history.
     */
    @Synchronized
    fun disconnect() {

        receiving = false
        connected = false

        frames.clear()
    }

    /**
     * Returns whether the receiver is logically connected.
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
     * Registers a listener for incoming CAN frames.
     *
     * The listener is called whenever receive() accepts
     * a valid frame.
     */
    @Synchronized
    fun setFrameListener(
        listener: ((CANFrame) -> Unit)?
    ) {

        frameListener = listener
    }

    /**
     * Receives a CAN frame from an external adapter.
     *
     * This is the main entry point for future USB/GPCU
     * hardware integration.
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

        addFrame(frame)

        frameListener?.invoke(frame)
    }

    /**
     * Receives a raw CAN ID and payload and converts it
     * into a CANFrame.
     *
     * Useful for USB/Serial adapters that provide
     * decoded CAN values.
     */
    @Synchronized
    fun receive(
        id: Int,
        data: ByteArray,
        timestamp: Long = System.currentTimeMillis()
    ) {

        val frame = CANFrame(
            id = id,
            data = data.copyOf(),
            timestamp = timestamp
        )

        receive(frame)
    }

    /**
     * Adds a frame to the internal history.
     */
    private fun addFrame(
        frame: CANFrame
    ) {

        frames.add(
            frame.copy(
                data = frame.data.copyOf()
            )
        )

        while (frames.size > MAX_FRAME_HISTORY) {
            frames.removeAt(0)
        }
    }

    /**
     * Returns the latest received frame.
     */
    @Synchronized
    fun getLastFrame(): CANFrame? {

        return frames.lastOrNull()?.copy(
            data = frames.last().data.copyOf()
        )
    }

    /**
     * Returns a snapshot of all currently stored frames.
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
     * Clears only the stored frame history.
     *
     * The CAN connection remains active.
     */
    @Synchronized
    fun clearFrames() {

        frames.clear()
    }

    /**
     * Starts frame reception.
     *
     * Useful when a hardware adapter has been connected
     * but frame processing needs to be started separately.
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
     * Stops frame reception without disconnecting
     * the logical CAN connection.
     */
    @Synchronized
    fun stopReceiving() {

        receiving = false
    }

    /**
     * Checks whether a CAN frame is structurally valid.
     *
     * Standard CAN payload:
     * 0..8 bytes.
     *
     * Extended CAN IDs are also allowed.
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
     * Creates a readable debug representation
     * of a CAN frame.
     *
     * Example:
     *
     * ID=123 DATA=01 02 FF 00
     */
    fun frameToString(
        frame: CANFrame
    ): String {

        val hex =
            frame.data.joinToString(" ") {
                "%02X".format(
                    it.toInt() and 0xFF
                )
            }

        return "ID=${frame.id} DATA=$hex"
    }

    /**
     * Returns all currently stored frames as readable
     * diagnostic strings.
     */
    @Synchronized
    fun getDebugFrames(): List<String> {

        return frames.map {
            frameToString(it)
        }
    }

    /**
     * Simulates a CAN frame for development/testing.
     *
     * This does not represent confirmed Peugeot Pars
     * CAN data. It is only a transport test.
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
     * Resets the receiver completely.
     */
    @Synchronized
    fun reset() {

        receiving = false
        connected = false

        frames.clear()

        frameListener = null
    }
}
