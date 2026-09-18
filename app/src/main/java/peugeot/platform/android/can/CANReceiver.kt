package peugeot.platform.android.can

data class CANFrame(
    val id: Int,
    val data: ByteArray,
    val timestamp: Long = System.currentTimeMillis()
)

object CANReceiver {

    private var connected = false

    private val frames =
        mutableListOf<CANFrame>()

    fun connect(): Boolean {
        connected = true
        return true
    }

    fun disconnect() {
        connected = false
        frames.clear()
    }

    fun isConnected(): Boolean {
        return connected
    }

    fun receive(
        frame: CANFrame
    ) {
        if (!connected) {
            return
        }

        frames.add(frame)
    }

    fun getLastFrame(): CANFrame? {
        return frames.lastOrNull()
    }

    fun getFrames(): List<CANFrame> {
        return frames.toList()
    }

    fun clearFrames() {
        frames.clear()
    }
}
