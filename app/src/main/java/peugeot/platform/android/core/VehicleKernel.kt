package peugeot.platform.android.core

object VehicleKernel {

    @Volatile
    var systemReady: Boolean = false
        private set

    fun boot() {
        systemReady = true
    }

    fun isReady(): Boolean {
        return systemReady
    }
}
