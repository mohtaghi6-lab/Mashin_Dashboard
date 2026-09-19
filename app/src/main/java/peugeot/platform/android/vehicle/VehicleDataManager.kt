package peugeot.platform.android.vehicle

import peugeot.platform.android.can.CANDataParser
import peugeot.platform.android.can.CANFrame


object VehicleDataManager {


    private var currentData =
        VehicleData.demo()



    fun updateFromCAN(
        frame: CANFrame
    ) {


        val speed =
            CANDataParser.parseSpeed(frame)



        val rpm =
            CANDataParser.parseRpm(frame)



        val temperature =
            CANDataParser.parseTemperature(frame)



        currentData =
            currentData.copy(

                speedKmh =
                    speed ?: currentData.speedKmh,


                rpm =
                    rpm ?: currentData.rpm,


                engineTempC =
                    temperature ?: currentData.engineTempC,


                canConnected = true,


                lastUpdate =
                    System.currentTimeMillis()
            )
    }



    fun getData(): VehicleData {

        return currentData
    }




    fun setDemoMode() {


        currentData =
            VehicleData.demo().copy(

                canConnected = false,

                lastUpdate =
                    System.currentTimeMillis()
            )
    }




    fun setOBDConnected(
        connected: Boolean
    ) {


        currentData =
            currentData.copy(

                obdConnected = connected,

                lastUpdate =
                    System.currentTimeMillis()
            )
    }




    fun addECUError() {


        currentData =
            currentData.copy(

                ecuErrorCount =
                    currentData.ecuErrorCount + 1
            )
    }




    fun clearErrors() {


        currentData =
            currentData.copy(

                ecuErrorCount = 0,

                checkEngine = false,

                absWarning = false,

                airbagWarning = false
            )
    }




    fun reset() {


        currentData =
            VehicleData(
                lastUpdate =
                    System.currentTimeMillis()
            )
    }

}
