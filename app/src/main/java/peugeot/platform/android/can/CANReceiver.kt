package peugeot.platform.android.can

import peugeot.platform.android.vehicle.ErrorScannerEngine


object CANReceiver {


    private var connected =
        false


    private var running =
        false



    private var frameListener:
            ((CANFrame) -> Unit)? = null



    private var errorScannerEngine:
            ErrorScannerEngine? = null





    fun attachErrorScanner(
        engine: ErrorScannerEngine
    ) {

        errorScannerEngine = engine

    }





    fun setFrameListener(
        listener: ((CANFrame) -> Unit)?
    ) {

        frameListener = listener

    }





    fun connect(): Boolean {

        connected = true

        return true

    }





    fun startReceiving(): Boolean {


        if (!connected) {

            connect()

        }


        running = true

        return true

    }





    fun stopReceiving() {

        running = false

    }





    fun disconnect() {

        running = false

        connected = false

    }





    fun receive(
        frame: CANFrame
    ) {


        if (!running || !connected) {

            return

        }



        frameListener?.invoke(
            frame
        )



        errorScannerEngine?.processFrame(
            frame
        )

    }





    fun simulateFrame(
        id: Int,
        data: ByteArray,
        timestamp: Long = System.currentTimeMillis()
    ) {


        if (!connected) {

            connect()

            startReceiving()

        }



        receive(

            CANFrame(

                id = id,

                data = data.copyOf(),

                timestamp = timestamp

            )

        )

    }





    fun reset() {


        running = false


        connected = false


        frameListener = null


        errorScannerEngine = null


    }





    fun isConnected(): Boolean {

        return connected

    }


}
