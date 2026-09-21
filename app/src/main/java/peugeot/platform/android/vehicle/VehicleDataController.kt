package peugeot.platform.android.vehicle

import peugeot.platform.android.can.CANDataParser
import peugeot.platform.android.can.CANFrame

class VehicleDataController(
    private val onDataChanged: (VehicleData) -> Unit
) {

    private var currentData = VehicleData()
    private var demoMode = false

    /**
     * دریافت یک فریم CAN
     *
     * فعلاً Parser موجود پروژه استفاده می‌شود.
     * وقتی پروتکل واقعی پژو/GPCU مشخص شود،
     * IDهای واقعی CAN در همین لایه اضافه خواهند شد.
     */
    fun updateFromCAN(frame: CANFrame) {

        demoMode = false

        var data = currentData.copy(
            canConnected = true,
            lastUpdate = frame.timestamp
        )

        val speed = CANDataParser.parseSpeed(frame)
        if (speed != null) {
            data = data.copy(
                speedKmh = speed
            )
        }

        val rpm = CANDataParser.parseRpm(frame)
        if (rpm != null) {
            data = data.copy(
                rpm = rpm
            )
        }

        val temperature = CANDataParser.parseTemperature(frame)
        if (temperature != null) {
            data = data.copy(
                engineTempC = temperature
            )
        }

        currentData = data

        publish()
    }

    /**
     * فعال کردن حالت Demo
     *
     * تا زمانی که GPCU/CAN واقعی متصل نشده،
     * داشبورد می‌تواند با داده آزمایشی اجرا شود.
     */
    fun useDemoMode() {

        demoMode = true

        currentData = VehicleData.demo()

        publish()
    }

    /**
     * خروج از حالت Demo
     */
    fun disableDemoMode() {
        demoMode = false

        currentData = currentData.copy(
            canConnected = false,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * وضعیت فعلی خودرو
     */
    fun getCurrentData(): VehicleData {
        return currentData
    }

    /**
     * آیا در حالت Demo هستیم؟
     */
    fun isDemoMode(): Boolean {
        return demoMode
    }

    /**
     * اعلام اتصال CAN
     */
    fun setCANConnected(connected: Boolean) {

        currentData = currentData.copy(
            canConnected = connected,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * اعلام اتصال OBD
     */
    fun setOBDConnected(connected: Boolean) {

        currentData = currentData.copy(
            obdConnected = connected,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * ثبت خطای ECU
     */
    fun setECUErrorCount(count: Int) {

        currentData = currentData.copy(
            ecuErrorCount = count.coerceAtLeast(0),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * وضعیت چراغ Check Engine
     */
    fun setCheckEngine(active: Boolean) {

        currentData = currentData.copy(
            checkEngine = active,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * وضعیت ABS
     */
    fun setABSWarning(active: Boolean) {

        currentData = currentData.copy(
            absWarning = active,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * وضعیت Airbag
     */
    fun setAirbagWarning(active: Boolean) {

        currentData = currentData.copy(
            airbagWarning = active,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * تنظیم سرعت
     *
     * برای زمانی که یک منبع دیگر مثل GPCU
     * داده سرعت را مستقیماً ارسال کند.
     */
    fun setSpeed(speedKmh: Int) {

        currentData = currentData.copy(
            speedKmh = speedKmh.coerceAtLeast(0),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * تنظیم RPM
     */
    fun setRPM(rpm: Int) {

        currentData = currentData.copy(
            rpm = rpm.coerceAtLeast(0),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * تنظیم دمای موتور
     */
    fun setEngineTemperature(tempC: Int) {

        currentData = currentData.copy(
            engineTempC = tempC,
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * تنظیم سطح سوخت
     */
    fun setFuelPercent(percent: Int) {

        currentData = currentData.copy(
            fuelPercent = percent.coerceIn(0, 100),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * تنظیم ولتاژ باتری
     */
    fun setBatteryVoltage(voltage: Float) {

        currentData = currentData.copy(
            batteryVoltage = voltage.coerceAtLeast(0f),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * تنظیم کیلومتر خودرو
     */
    fun setOdometer(km: Int) {

        currentData = currentData.copy(
            odometerKm = km.coerceAtLeast(0),
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * بازنشانی اطلاعات
     */
    fun reset() {

        demoMode = false

        currentData = VehicleData(
            lastUpdate = System.currentTimeMillis()
        )

        publish()
    }

    /**
     * ارسال اطلاعات جدید به Dashboard
     */
    private fun publish() {

        onDataChanged(currentData)
    }
}
