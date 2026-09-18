package peugeot.platform.android.navigation

data class NavigationDestination(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

enum class NavigationState {
    IDLE,
    READY,
    NAVIGATING,
    ARRIVED
}

object NavigationManager {

    private var state =
        NavigationState.IDLE

    private var destination:
        NavigationDestination? = null

    fun setDestination(
        value: NavigationDestination
    ) {
        destination = value
        state = NavigationState.READY
    }

    fun startNavigation() {

        if (destination != null) {
            state =
                NavigationState.NAVIGATING
        }
    }

    fun markArrived() {

        if (destination != null) {
            state =
                NavigationState.ARRIVED
        }
    }

    fun stopNavigation() {

        state =
            NavigationState.IDLE

        destination = null
    }

    fun getState(): NavigationState {
        return state
    }

    fun getDestination():
        NavigationDestination? {
        return destination
    }

    fun isNavigating(): Boolean {
        return state ==
            NavigationState.NAVIGATING
    }

    fun reset() {
        state = NavigationState.IDLE
        destination = null
    }
}
