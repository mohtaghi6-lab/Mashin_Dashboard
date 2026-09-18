package peugeot.platform.android.bluetooth

enum class BluetoothState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED
}

object BluetoothManager {

    private var state =
        BluetoothState.DISCONNECTED

    private var deviceName: String? = null

    fun connect(
        name: String
    ) {

        if (name.isBlank()) {
            return
        }

        state =
            BluetoothState.CONNECTING

        deviceName = name

        state =
            BluetoothState.CONNECTED
    }

    fun disconnect() {

        state =
            BluetoothState.DISCONNECTED

        deviceName = null
    }

    fun getState(): BluetoothState {
        return state
    }

    fun isConnected(): Boolean {
        return state ==
            BluetoothState.CONNECTED
    }

    fun getDeviceName(): String? {
        return deviceName
    }

    fun reset() {

        state =
            BluetoothState.DISCONNECTED

        deviceName = null
    }
}
