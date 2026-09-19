package peugeot.platform.android.vehicle

import peugeot.platform.android.can.CANFrame


class VehicleDataController(
    private val onDataChanged: (VehicleData) -> Unit
) {


    private var lastData: VehicleData? = null



    fun updateFromCAN(
        frame: CANFrame
    ) {


        VehicleDataManager.updateFromCAN(
            frame
        )


        publish()
    }



    fun useDemoMode() {


        VehicleDataManager.setDemoMode()


        publish()
    }



    fun getCurrentData(): VehicleData {

        return VehicleDataManager.getData()

    }



    fun reset() {


        VehicleDataManager.reset()


        publish()

    }



    private fun publish() {


        val data =
            VehicleDataManager.getData()



        if (data != lastData) {


            lastData = data


            onDataChanged(
                data
            )

        }

    }

}
