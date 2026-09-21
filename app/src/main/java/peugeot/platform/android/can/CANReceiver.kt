package peugeot.platform.android.can

import peugeot.platform.android.vehicle.ErrorScannerEngine


object CANReceiver {


    private var connected =
        false


    private var running =
        false



    private var errorScannerEngine:
            ErrorScannerEngine? = null



    fun attachErrorScanner(
        engine: ErrorScannerEngine
    ) {

        errorScannerEngine = engine

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





    fun isConnected(): Boolean {

        return connected

    }





    /**
     * دریافت فریم واقعی CAN
     *
     * این تابع بعداً از USB/Bluetooth CAN Driver صدا زده می‌شود
     */
    fun receive(
        frame: CANFrame
    ) {


        if (!running) {

            return

        }



        errorScannerEngine?.processFrame(
            frame
        )

    }



}
