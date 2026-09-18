package peugeot.platform.android

import peugeot.platform.android.core.VehicleKernel

class DisplayBootManager {

    fun onWindowReady() {
        VehicleKernel.boot()
    }

    fun destroy() {
        VehicleKernel.shutdown()
    }
}
