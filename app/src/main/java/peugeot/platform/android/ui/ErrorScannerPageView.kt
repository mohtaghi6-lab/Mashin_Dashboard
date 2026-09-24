package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View

import peugeot.platform.android.can.CANFrame
import peugeot.platform.android.vehicle.ErrorScannerEngine
import peugeot.platform.android.vehicle.ErrorSeverity
import peugeot.platform.android.vehicle.VehicleData
import peugeot.platform.android.vehicle.VehicleError

class ErrorScannerPageView(
    context: Context
) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()
    private val errorScannerEngine = ErrorScannerEngine()

    private var vehicleData: VehicleData = VehicleData.demo()
    private var errors: List<VehicleError> = emptyList()
    private var scanning = false

    var onBackClick: (() -> Unit)? = null
    var onRescanClick: (() -> Unit)? = null

    init {
        isClickable = true

        errorScannerEngine.onErrorUpdated = { list ->
            errors = list
            invalidate()
        }
    }

    fun setVehicleData(data: VehicleData) {
        vehicleData = data
        invalidate()
    }

    fun startScan() {
        scanning = true
        errorScannerEngine.startScan()
        invalidate()
    }

    fun stopScan() {
        scanning = false
        errorScannerEngine.stopScan()
        invalidate()
    }

    fun clearErrors() {
        errorScannerEngine.clearErrors()
        invalidate()
    }

    fun receiveFrame(frame: CANFrame) {
        errorScannerEngine.processFrame(frame)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) return true

        val x = event.x
        val y = event.y
        val w = width.toFloat()
        val h = height.toFloat()

        if (y in 28f..90f && x < w * 0.25f) {
            onBackClick?.invoke()
            return true
        }

        if (y in h - 125f..h - 45f && x > w * 0.52f) {
            onRescanClick?.invoke()
            return true
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        canvas.drawColor(Color.rgb(3, 8, 15))

        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(8, 18, 30)
        rect.set(22f, 20f, w - 22f, 98f)
        canvas.drawRoundRect(rect, 22f, 22f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 14f
        paint.color = Color.rgb(130, 210, 255)
        canvas.drawText("‹", 42f, 68f, paint)

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 23f
        paint.color = Color.WHITE
        canvas.drawText("ECU DIAGNOSTIC", w / 2f, 58f, paint)

        paint.textSize = 11f
        paint.color = Color.rgb(130, 150, 170)
        canvas.drawText("BMW LUXURY VEHICLE OS", w / 2f, 82f, paint)

        val reportedErrors =
            if (vehicleData.ecuErrorCount > 0) vehicleData.ecuErrorCount else errors.size

        drawStatusCard(canvas, 22f, 118f, w * 0.48f - 30f, 112f,
            "ECU STATUS",
            if (reportedErrors == 0) "SAFE" else "WARNING",
            if (reportedErrors == 0) Color.rgb(70, 230, 150) else Color.rgb(255, 190, 70))

        drawStatusCard(canvas, w * 0.52f + 8f, 118f, w - 22f, 230f,
            "ACTIVE ERRORS",
            reportedErrors.toString(),
            if (reportedErrors == 0) Color.rgb(70, 230, 150) else Color.rgb(255, 190, 70))

        drawConnectionCard(canvas, 22f, 248f, w * 0.48f - 30f, 112f)
        drawWarningCard(canvas, w * 0.52f + 8f, 248f, w - 22f, 360f)

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 12f
        paint.color = Color.rgb(130, 210, 255)
        canvas.drawText("DIAGNOSTIC LOG", 22f, 402f, paint)

        var y = 430f
        if (errors.isEmpty()) {
            paint.textAlign = Paint.Align.LEFT
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 15f
            paint.color = Color.rgb(70, 230, 150)
            canvas.drawText(
                if (scanning) "SCAN COMPLETE — NO ECU ERRORS" else "NO ECU ERRORS",
                22f, y, paint
            )
        } else {
            errors.take(4).forEach { error ->
                drawError(canvas, error, y)
                y += 62f
            }
        }

        paint.color = Color.rgb(8, 18, 30)
        rect.set(22f, h - 118f, w - 22f, h - 22f)
        canvas.drawRoundRect(rect, 20f, 20f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f
        paint.color = Color.rgb(150, 165, 180)
        canvas.drawText("ECU • CAN • OBD", 42f, h - 78f, paint)

        paint.textSize = 10f
        canvas.drawText(
            if (scanning) "SYSTEM READY" else "SYSTEM STANDBY",
            42f, h - 54f, paint
        )

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 14f
        paint.color = Color.rgb(100, 220, 255)
        canvas.drawText("RESCAN", w * 0.78f, h - 70f, paint)
    }

    private fun drawStatusCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        title: String,
        value: String,
        accent: Int
    ) {
        paint.color = Color.rgb(8, 18, 30)
        rect.set(left, top, right, bottom)
        canvas.drawRoundRect(rect, 18f, 18f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 10f
        paint.color = Color.rgb(130, 150, 170)
        canvas.drawText(title, left + 16f, top + 25f, paint)

        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 23f
        paint.color = accent
        canvas.drawText(value, left + 16f, top + 62f, paint)
    }

    private fun drawConnectionCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        paint.color = Color.rgb(8, 18, 30)
        rect.set(left, top, right, bottom)
        canvas.drawRoundRect(rect, 18f, 18f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 11f
        paint.color = Color.rgb(130, 210, 255)
        canvas.drawText("CONNECTION", left + 16f, top + 24f, paint)

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f
        paint.color = if (vehicleData.canConnected) Color.GREEN else Color.CYAN
        canvas.drawText(
            if (vehicleData.canConnected) "CAN  ONLINE" else "CAN  DEMO",
            left + 16f, top + 53f, paint
        )

        paint.color = if (vehicleData.obdConnected) Color.GREEN else Color.LTGRAY
        canvas.drawText(
            if (vehicleData.obdConnected) "OBD  CONNECTED" else "OBD  STANDBY",
            left + 16f, top + 79f, paint
        )
    }

    private fun drawWarningCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float
    ) {
        paint.color = Color.rgb(8, 18, 30)
        rect.set(left, top, right, bottom)
        canvas.drawRoundRect(rect, 18f, 18f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 11f
        paint.color = Color.rgb(130, 210, 255)
        canvas.drawText("WARNING LIGHTS", left + 16f, top + 24f, paint)

        drawIndicator(canvas, left + 16f, top + 48f, "CHECK ENGINE", vehicleData.checkEngine)
        drawIndicator(canvas, left + 16f, top + 82f, "ABS", vehicleData.absWarning)
        drawIndicator(canvas, left + 16f, top + 116f, "AIRBAG", vehicleData.airbagWarning)
    }

    private fun drawIndicator(
        canvas: Canvas,
        x: Float,
        y: Float,
        label: String,
        active: Boolean
    ) {
        paint.style = Paint.Style.FILL
        paint.color = if (active) Color.rgb(255, 80, 70) else Color.rgb(60, 200, 130)
        canvas.drawCircle(x + 5f, y - 4f, 5f, paint)

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f
        paint.color = if (active) Color.rgb(255, 130, 120) else Color.LTGRAY
        canvas.drawText(
            if (active) "$label  ACTIVE" else "$label  OK",
            x + 18f, y, paint
        )
    }

    private fun drawError(
        canvas: Canvas,
        error: VehicleError,
        y: Float
    ) {
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 15f
        paint.color = when (error.severity) {
            ErrorSeverity.INFO -> Color.CYAN
            ErrorSeverity.WARNING -> Color.YELLOW
            ErrorSeverity.CRITICAL -> Color.RED
        }

        canvas.drawText(error.code, 22f, y, paint)

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f
        paint.color = Color.LTGRAY
        canvas.drawText(error.description, 22f, y + 20f, paint)
    }
}
