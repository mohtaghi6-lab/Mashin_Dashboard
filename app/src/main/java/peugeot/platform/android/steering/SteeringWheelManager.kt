package peugeot.platform.android.steering

enum class SteeringAction {
    VOLUME_UP,
    VOLUME_DOWN,
    NEXT_TRACK,
    PREVIOUS_TRACK,
    ANSWER_CALL,
    END_CALL,
    VOICE_ASSISTANT,
    MUTE
}

object SteeringWheelManager {

    private val bindings =
        mutableMapOf<Int, SteeringAction>()

    private var connected = false

    fun connect() {
        connected = true
    }

    fun disconnect() {
        connected = false
    }

    fun isConnected(): Boolean {
        return connected
    }

    fun bind(
        buttonCode: Int,
        action: SteeringAction
    ) {
        bindings[buttonCode] = action
    }

    fun unbind(
        buttonCode: Int
    ) {
        bindings.remove(buttonCode)
    }

    fun getAction(
        buttonCode: Int
    ): SteeringAction? {
        return bindings[buttonCode]
    }

    fun getBindings(): Map<Int, SteeringAction> {
        return bindings.toMap()
    }

    fun clearBindings() {
        bindings.clear()
    }
}
