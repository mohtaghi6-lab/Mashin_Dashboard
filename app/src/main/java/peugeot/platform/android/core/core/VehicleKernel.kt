package peugeot.platform.android.core

object VehicleKernel {

    @Volatile
    var systemReady: Boolean = false
        private set

    fun boot() {

        if (systemReady) return

        ResourceManager.load()
        AppManager.initialize()

        systemReady = true
    }

    fun shutdown() {
        systemReady = false
    }
}
