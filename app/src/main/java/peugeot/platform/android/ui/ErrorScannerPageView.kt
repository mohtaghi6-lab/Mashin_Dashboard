package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.View

import peugeot.platform.android.can.CANFrame
import peugeot.platform.android.vehicle.ErrorScannerEngine
import peugeot.platform.android.vehicle.ErrorSeverity
import peugeot.platform.android.vehicle.VehicleData
import peugeot.platform.android.vehicle.VehicleError

class ErrorScannerPageView(
    context: Context
) : View(context) {

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private val errorScannerEngine =
        ErrorScannerEngine()

    private var vehicleData: VehicleData =
        VehicleData.demo()

    private var errors: List<VehicleError> =
        emptyList()

    init {
        errorScannerEngine.onErrorUpdated =
            { list ->
                errors = list
                invalidate()
            }
    }

    fun setVehicleData(
        data: VehicleData
    ) {
        vehicleData = data
        invalidate()
    }

    fun startScan() {
        errorScannerEngine.startScan()
        invalidate()
    }

    fun clearErrors() {
        errorScannerEngine.clearErrors()
        invalidate()
    }

    fun receiveFrame(
        frame: CANFrame
    ) {
        errorScannerEngine.processFrame(frame)
    }

    override fun onDraw(
        canvas: Canvas
    ) {
        super.onDraw(canvas)

        val w =
            width.toFloat()

        canvas.drawColor(
            Color.rgb(
                3,
                8,
                15
            )
        )

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.color =
            Color.WHITE

        paint.textSize =
            26f

        canvas.drawText(
            "ECU ERROR SCANNER",
            w / 2f,
            55f,
            paint
        )

        paint.textSize =
            16f

        paint.color =
            Color.rgb(
                100,
                220,
                255
            )

        val reportedErrors =
            if (vehicleData.ecuErrorCount > 0) {
                vehicleData.ecuErrorCount
            } else {
                errors.size
            }

        canvas.drawText(
            "ACTIVE ERRORS : $reportedErrors",
            w / 2f,
            90f,
            paint
        )

        paint.textAlign =
            Paint.Align.LEFT

        paint.textSize =
            13f

        paint.color =
            if (vehicleData.canConnected) {
                Color.GREEN
            } else {
                Color.CYAN
            }

        canvas.drawText(
            if (vehicleData.canConnected) {
                "CAN: ONLINE"
            } else {
                "CAN: READY / DEMO"
            },
            40f,
            125f,
            paint
        )

        paint.textAlign =
            Paint.Align.RIGHT

        paint.color =
            if (vehicleData.obdConnected) {
                Color.GREEN
            } else {
                Color.LTGRAY
            }

        canvas.drawText(
            if (vehicleData.obdConnected) {
                "OBD: CONNECTED"
            } else {
                "OBD: STANDBY"
            },
            w - 40f,
            125f,
            paint
        )

        var y =
            175f

        if (errors.isEmpty()) {
            paint.color =
                Color.GREEN

            paint.textSize =
                18f

            paint.textAlign =
                Paint.Align.CENTER

            canvas.drawText(
                "NO ECU ERRORS",
                w / 2f,
                y,
                paint
            )
        } else {
            errors.forEach { error ->
                drawError(
                    canvas,
                    error,
                    y
                )

                y += 75f
            }
        }
    }

    private fun drawError(
        canvas: Canvas,
        error: VehicleError,
        y: Float
    ) {
        paint.textAlign =
            Paint.Align.LEFT

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            18f

        paint.color =
            when (error.severity) {
                ErrorSeverity.INFO ->
                    Color.CYAN

                ErrorSeverity.WARNING ->
                    Color.YELLOW

                ErrorSeverity.CRITICAL ->
                    Color.RED
            }

        canvas.drawText(
            error.code,
            40f,
            y,
            paint
        )

        paint.typeface =
            Typeface.DEFAULT

        paint.textSize =
            14f

        paint.color =
            Color.LTGRAY

        canvas.drawText(
            error.description,
            40f,
            y + 25f,
            paint
        )
    }
}
