package peugeot.platform.android.display

object DisplayManager {

    private var initialized = false

    private var width = 0
    private var height = 0

    fun initialize(
        displayWidth: Int,
        displayHeight: Int
    ) {
        width = displayWidth
        height = displayHeight
        initialized = true
    }

    fun isInitialized(): Boolean {
        return initialized
    }

    fun getWidth(): Int {
        return width
    }

    fun getHeight(): Int {
        return height
    }

    fun getAspectRatio(): Float {

        if (height <= 0) {
            return 0f
        }

        return width.toFloat() / height.toFloat()
    }

    fun reset() {
        initialized = false
        width = 0
        height = 0
    }
}
