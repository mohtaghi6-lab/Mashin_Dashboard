package peugeot.platform.android.core

object ResourceManager {

    private val resources =
        mutableMapOf<String, Any>()

    fun register(
        key: String,
        resource: Any
    ) {
        if (key.isBlank()) {
            return
        }

        resources[key] = resource
    }

    fun get(
        key: String
    ): Any? {
        return resources[key]
    }

    fun contains(
        key: String
    ): Boolean {
        return resources.containsKey(key)
    }

    fun remove(
        key: String
    ) {
        resources.remove(key)
    }

    fun clear() {
        resources.clear()
    }

    fun getKeys(): List<String> {
        return resources.keys.toList()
    }
}
