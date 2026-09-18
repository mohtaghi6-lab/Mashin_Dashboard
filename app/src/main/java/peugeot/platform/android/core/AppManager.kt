package peugeot.platform.android.core

object AppManager {

    private val apps = linkedSetOf<String>()

    fun initialize() {

        if (apps.isNotEmpty()) return

        apps += "AI Assistant"
        apps += "Dashboard"
        apps += "Bluetooth"
        apps += "Navigation"
        apps += "Vehicle"
    }

    fun getApps(): List<String> {
        return apps.toList()
    }
}
