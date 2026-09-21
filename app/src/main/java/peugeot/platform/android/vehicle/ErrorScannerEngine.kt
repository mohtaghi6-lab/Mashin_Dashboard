package peugeot.platform.android.vehicle

import peugeot.platform.android.can.CANFrame
import java.util.concurrent.CopyOnWriteArrayList


/**
 * ErrorScannerEngine
 *
 * وظیفه:
 * - دریافت خطاهای ECU از CAN
 * - نگهداری DTC ها
 * - ارسال وضعیت به UI
 *
 * Flow:
 *
 * CANFrame
 *     |
 *     ↓
 * ErrorScannerEngine
 *     |
 *     ↓
 * ErrorScannerPageView
 */
class ErrorScannerEngine {


    private val errors =
        CopyOnWriteArrayList<VehicleError>()



    private var scanning =
        false



    var onErrorUpdated:
            ((List<VehicleError>) -> Unit)? = null





    /**
     * Start ECU scanning
     */
    fun startScan() {

        scanning = true

        notifyUpdate()

    }





    /**
     * Stop scanning
     */
    fun stopScan() {

        scanning = false

    }





    /**
     * Receive CAN frame.
     *
     * Future:
     * Replace IDs with Peugeot ECU mappings.
     */
    fun processFrame(
        frame: CANFrame
    ) {


        if (!scanning) {
            return
        }



        if (isDiagnosticFrame(frame)) {


            val error =
                decodeError(frame)



            if (error != null) {


                addError(error)


            }

        }

    }





    /**
     * Detect diagnostic CAN message.
     */
    private fun isDiagnosticFrame(
        frame: CANFrame
    ): Boolean {


        return frame.id in 0x700..0x7FF

    }





    /**
     * Convert CAN data to DTC.
     *
     * Placeholder decoder.
     */
    private fun decodeError(
        frame: CANFrame
    ): VehicleError? {


        if (frame.data.isEmpty()) {

            return null

        }



        val code =
            frame.data.joinToString("") {

                "%02X".format(
                    it.toInt() and 0xFF
                )

            }



        return VehicleError(

            code = "DTC-$code",

            description =
                "ECU diagnostic message",

            severity =
                ErrorSeverity.WARNING

        )

    }





    /**
     * Add new error.
     */
    private fun addError(
        error: VehicleError
    ) {


        if (
            errors.none {
                it.code == error.code
            }
        ) {

            errors.add(error)

            notifyUpdate()

        }

    }





    /**
     * Current errors.
     */
    fun getErrors(): List<VehicleError> {

        return errors.toList()

    }





    /**
     * Clear errors.
     */
    fun clearErrors() {

        errors.clear()

        notifyUpdate()

    }





    private fun notifyUpdate() {

        onErrorUpdated?.invoke(
            getErrors()
        )

    }


}




/**
 * Vehicle error model.
 */
data class VehicleError(

    val code: String,

    val description: String,

    val severity: ErrorSeverity

)





enum class ErrorSeverity {

    INFO,

    WARNING,

    CRITICAL

}
