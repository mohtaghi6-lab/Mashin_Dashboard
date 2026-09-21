package peugeot.platform.android.vehicle

import peugeot.platform.android.can.CANFrame
import peugeot.platform.android.can.CANReceiver


/**
 * GPCU Bridge Layer
 *
 * وظیفه:
 * - ارتباط منطقی بین GPCU / USB CAN Adapter و سیستم Vehicle OS
 * - دریافت فریم‌های CAN از سخت‌افزار
 * - ارسال آنها به CANReceiver
 *
 * فعلاً Hardware مستقل است.
 * بعداً فقط Driver مربوط به USB یا Bluetooth CAN اضافه می‌شود.
 *
 *
 * Architecture:
 *
 * GPCU / CAN Hardware
 *          |
 *          ↓
 *     GPCUBridge
 *          |
 *          ↓
 *     CANReceiver
 *          |
 *          ↓
 *    CANDataParser
 *          |
 *          ↓
 * VehicleDataController
 */
class GPCUBridge {


    private var connected =
        false



    private var running =
        false



    private var deviceName =
        "Unknown GPCU"




    /**
     * Connect to GPCU device.
     *
     * Current:
     * Software connection.
     *
     * Future:
     * USB / Bluetooth / Serial driver.
     */
    fun connect(
        name: String = "Peugeot GPCU"
    ): Boolean {


        deviceName =
            name



        connected =
            CANReceiver.connect()



        return connected

    }





    /**
     * Start receiving CAN stream.
     */
    fun start() {


        if (!connected) {

            connect()

        }



        running =
            CANReceiver.startReceiving()

    }





    /**
     * Stop communication.
     */
    fun stop() {


        running =
            false



        CANReceiver.stopReceiving()

    }





    /**
     * Disconnect completely.
     */
    fun disconnect() {


        stop()



        CANReceiver.disconnect()



        connected =
            false

    }





    /**
     * Hardware status.
     */
    fun isConnected(): Boolean {

        return connected

    }





    /**
     * Stream status.
     */
    fun isRunning(): Boolean {

        return running

    }





    /**
     * Connected device name.
     */
    fun getDeviceName(): String {

        return deviceName

    }





    /**
     * Receive raw CAN packet from hardware.
     *
     * This function will later be called by:
     * USB Driver / Bluetooth Driver
     */
    fun receiveCAN(
        id: Int,
        data: ByteArray
    ) {


        if (!connected) {

            return

        }



        val frame =
            CANFrame(
                id = id,
                data = data.copyOf()
            )



        CANReceiver.receive(
            frame
        )

    }





    /**
     * Direct frame injection for testing.
     */
    fun injectFrame(
        frame: CANFrame
    ) {


        if (!connected) {

            connect()

        }



        CANReceiver.receive(
            frame
        )

    }





    /**
     * Reset bridge.
     */
    fun reset() {


        disconnect()



        deviceName =
            "Unknown GPCU"

    }

}
