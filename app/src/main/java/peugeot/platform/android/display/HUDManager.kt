package peugeot.platform.android.display

object HUDManager {

    private var enabled = false

    private var speed = 0
    private var rpm = 0

    fun enable() {
        enabled = true
    }

    fun disable() {
        enabled = false
    }

    fun isEnabled(): Boolean {
        return enabled
    }

    fun updateSpeed(
        value: Int
    ) {
        speed = value.coerceAtLeast(0)
    }

    fun updateRpm(
        value: Int
    ) {
        rpm = value.coerceAtLeast(0)
    }

    fun getSpeed(): Int {
        return speed
    }

    fun getRpm(): Int {
        return rpm
    }

    fun reset() {
        enabled = false
        speed = 0
        rpm = 0
    }
}
