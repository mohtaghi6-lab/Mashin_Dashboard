package peugeot.platform.android.plugins

object PermissionManager {

    private val permissions =
        mutableMapOf<String, MutableSet<String>>()

    fun grant(
        pluginId: String,
        permission: String
    ) {
        if (
            pluginId.isBlank() ||
            permission.isBlank()
        ) {
            return
        }

        val pluginPermissions =
            permissions.getOrPut(pluginId) {
                mutableSetOf()
            }

        pluginPermissions.add(permission)
    }

    fun revoke(
        pluginId: String,
        permission: String
    ) {
        permissions[pluginId]?.remove(permission)
    }

    fun hasPermission(
        pluginId: String,
        permission: String
    ): Boolean {
        return permissions[pluginId]
            ?.contains(permission) == true
    }

    fun getPermissions(
        pluginId: String
    ): List<String> {
        return permissions[pluginId]
            ?.toList()
            ?: emptyList()
    }

    fun clearPlugin(
        pluginId: String
    ) {
        permissions.remove(pluginId)
    }

    fun clear() {
        permissions.clear()
    }
}
