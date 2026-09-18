package peugeot.platform.android.vehicle

import peugeot.platform.android.can.CANFrame

class VehicleDataController(
    private val onDataChanged: (VehicleData) -> Unit
) {

    fun updateFromCAN(
        frame: CANFrame
    ) {
        VehicleDataManager.updateFromCAN(frame)

        onDataChanged(
            VehicleDataManager.getData()
        )
    }

    fun useDemoMode() {

        VehicleDataManager.setDemoMode()

        onDataChanged(
            VehicleDataManager.getData()
        )
    }

    fun getCurrentData(): VehicleData {
        return VehicleDataManager.getData()
    }

    fun reset() {

        VehicleDataManager.reset()

        onDataChanged(
            VehicleDataManager.getData()
        )
    }
}
