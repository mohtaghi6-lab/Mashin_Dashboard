package peugeot.platform.android

import peugeot.platform.android.core.VehicleKernel

object DisplayBootManager {

    fun boot() {
        VehicleKernel.boot()
    }

    fun isSystemReady(): Boolean {
        return VehicleKernel.isReady()
    }
}
