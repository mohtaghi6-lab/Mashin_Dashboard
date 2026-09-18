package peugeot.platform.android.core

object ResourceManager {

    @Volatile
    var loaded: Boolean = false
        private set

    fun load() {
        loaded = true
    }
}
