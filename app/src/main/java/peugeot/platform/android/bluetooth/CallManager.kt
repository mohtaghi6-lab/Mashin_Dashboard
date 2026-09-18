package peugeot.platform.android.bluetooth

enum class CallState {
    IDLE,
    DIALING,
    RINGING,
    ACTIVE,
    ENDED
}

data class CallInfo(
    val number: String,
    val state: CallState
)

object CallManager {

    private var currentCall:
        CallInfo? = null

    fun dial(
        number: String
    ) {

        if (number.isBlank()) {
            return
        }

        currentCall =
            CallInfo(
                number = number,
                state = CallState.DIALING
            )
    }

    fun incoming(
        number: String
    ) {

        if (number.isBlank()) {
            return
        }

        currentCall =
            CallInfo(
                number = number,
                state = CallState.RINGING
            )
    }

    fun answer() {

        currentCall =
            currentCall?.copy(
                state = CallState.ACTIVE
            )
    }

    fun end() {

        currentCall =
            currentCall?.copy(
                state = CallState.ENDED
            )
    }

    fun getCurrentCall(): CallInfo? {
        return currentCall
    }

    fun isActive(): Boolean {

        return currentCall?.state ==
            CallState.ACTIVE
    }

    fun clear() {
        currentCall = null
    }
}
