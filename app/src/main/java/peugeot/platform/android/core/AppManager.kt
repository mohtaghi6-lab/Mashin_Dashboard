package peugeot.platform.android.core

object AppManager {

    private val activeApps =
        mutableSetOf<String>()

    fun registerApp(
        appId: String
    ) {
        if (appId.isBlank()) {
            return
        }

        activeApps.add(appId)
    }

    fun unregisterApp(
        appId: String
    ) {
        activeApps.remove(appId)
    }

    fun isRegistered(
        appId: String
    ): Boolean {
        return activeApps.contains(appId)
    }

    fun getActiveApps(): List<String> {
        return activeApps.toList()
    }

    fun clear() {
        activeApps.clear()
    }
}
