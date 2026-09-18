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
                speedKmh = speed
                    ?: currentData.speedKmh,

                rpm = rpm
                    ?: currentData.rpm,

                engineTempC = temperature
                    ?: currentData.engineTempC,

                canConnected = true
            )
    }

    fun getData(): VehicleData {
        return currentData
    }

    fun setDemoMode() {

        currentData =
            VehicleData.demo().copy(
                canConnected = false
            )
    }

    fun reset() {

        currentData =
            VehicleData()
    }
}
