package peugeot.platform.android.plugins

object PluginEngine {

    private val plugins =
        mutableMapOf<String, Boolean>()

    fun register(
        pluginId: String
    ) {
        if (pluginId.isBlank()) {
            return
        }

        plugins[pluginId] = true
    }

    fun unregister(
        pluginId: String
    ) {
        plugins.remove(pluginId)
    }

    fun isRegistered(
        pluginId: String
    ): Boolean {
        return plugins.containsKey(pluginId)
    }

    fun isEnabled(
        pluginId: String
    ): Boolean {
        return plugins[pluginId] == true
    }

    fun enable(
        pluginId: String
    ) {
        if (plugins.containsKey(pluginId)) {
            plugins[pluginId] = true
        }
    }

    fun disable(
        pluginId: String
    ) {
        if (plugins.containsKey(pluginId)) {
            plugins[pluginId] = false
        }
    }

    fun getPlugins(): List<String> {
        return plugins.keys.toList()
    }

    fun clear() {
        plugins.clear()
    }
}
