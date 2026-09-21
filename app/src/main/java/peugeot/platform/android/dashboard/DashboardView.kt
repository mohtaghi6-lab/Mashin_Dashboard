package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.vehicle.VehicleData
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class DashboardView(
    context: Context
) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var data = VehicleData.demo()

    private var aiState = AIState.IDLE

    private var pulse = 0f

    var onAIOrbClick: (() -> Unit)? = null

    private val blue = Color.rgb(70, 190, 255)
    private val cyan = Color.rgb(110, 225, 255)
    private val dark = Color.rgb(2, 6, 12)
    private val panel = Color.argb(185, 9, 18, 29)
    private val muted = Color.rgb(145, 165, 180)

    init {
        isFocusable = true
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        postInvalidateOnAnimation()
    }

    fun setVehicleData(value: VehicleData) {
        data = value
        invalidate()
    }

    fun setAIState(value: AIState) {
        aiState = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        drawBackground(canvas, w, h)
        drawHeader(canvas, w)
        drawStatusBar(canvas, w)
        drawLeftGauge(canvas, w, h)
        drawRightGauge(canvas, w, h)
        drawAIOrb(canvas, w / 2f, h * 0.43f)
        drawCenterInfo(canvas, w, h)
        drawBottomPanels(canvas, w, h)

        pulse += 0.045f
        postInvalidateOnAnimation()
    }

    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.style = Paint.Style.FILL

        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            h,
            Color.rgb(3, 9, 17),
            Color.rgb(0, 2, 6),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            paint
        )

        paint.shader = null

        paint.color = Color.argb(20, 70, 190, 255)

        val gridSize = 80f

        var x = 0f

        while (x < w) {
            canvas.drawLine(
                x,
                0f,
                x,
                h,
                paint
            )
            x += gridSize
        }

        var y = 0f

        while (y < h) {
            canvas.drawLine(
                0f,
                y,
                w,
                y,
                paint
            )
            y += gridSize
        }
    }

    private fun drawHeader(
        canvas: Canvas,
        w: Float
    ) {
        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 24f
        paint.color = Color.WHITE

        canvas.drawText(
            "PEUGEOT",
            35f,
            42f,
            paint
        )

        paint.textSize = 14f
        paint.color = blue

        canvas.drawText(
            "VEHICLE OS",
            36f,
            64f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 18f
        paint.color = Color.WHITE

        canvas.drawText(
            "PARS 93",
            w - 35f,
            42f,
            paint
        )

        paint.textSize = 13f
        paint.color = muted

        canvas.drawText(
            "LUXURY DRIVE",
            w - 35f,
            63f,
            paint
        )
    }

    private fun drawStatusBar(
        canvas: Canvas,
        w: Float
    ) {
        val y = 92f

        paint.style = Paint.Style.FILL
        paint.color = Color.argb(130, 8, 18, 30)

        canvas.drawRoundRect(
            RectF(
                25f,
                y - 22f,
                w - 25f,
                y + 18f
            ),
            20f,
            20f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT_BOLD

        drawStatusItem(
            canvas,
            "CAN",
            data.canConnected,
            45f,
            y + 5f
        )

        drawStatusItem(
            canvas,
            "OBD",
            data.obdConnected,
            120f,
            y + 5f
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.color = muted

        canvas.drawText(
            "SYSTEM READY",
            w - 45f,
            y + 5f,
            paint
        )
    }

    private fun drawStatusItem(
        canvas: Canvas,
        label: String,
        connected: Boolean,
        x: Float,
        y: Float
    ) {
        paint.color = if (connected) {
            Color.rgb(80, 255, 170)
        } else {
            Color.rgb(110, 125, 140)
        }

        canvas.drawCircle(
            x,
            y - 4f,
            5f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 12f

        canvas.drawText(
            label,
            x + 12f,
            y,
            paint
        )
    }

    private fun drawLeftGauge(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val cx = w * 0.22f
        val cy = h * 0.48f
        val radius = min(w, h) * 0.19f

        drawGaugeRing(
            canvas,
            cx,
            cy,
            radius,
            data.speedKmh,
            300,
            "SPEED",
            "km/h"
        )
    }

    private fun drawRightGauge(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val cx = w * 0.78f
        val cy = h * 0.48f
        val radius = min(w, h) * 0.19f

        drawGaugeRing(
            canvas,
            cx,
            cy,
            radius,
            data.rpm,
            8000,
            "RPM",
            "x1000"
        )
    }

    private fun drawGaugeRing(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Int,
        maximum: Int,
        title: String,
        unit: String
    ) {
        paint.shader = null
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = Color.argb(55, 150, 180, 200)

        val rect = RectF(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius
        )

        canvas.drawArc(
            rect,
            135f,
            270f,
            false,
            paint
        )

        val safeValue = value.coerceIn(0, maximum)
        val sweep = 270f * safeValue / maximum.toFloat()

        paint.color = blue
        paint.strokeWidth = 10f
        paint.setShadowLayer(
            18f,
            0f,
            0f,
            Color.argb(170, 70, 190, 255)
        )

        canvas.drawArc(
            rect,
            135f,
            sweep,
            false,
            paint
        )

        paint.clearShadowLayer()

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = Color.WHITE
        paint.textSize = 36f

        val displayValue = if (title == "RPM") {
            "%.1f".format(value / 1000f)
        } else {
            value.toString()
        }

        canvas.drawText(
            displayValue,
            cx,
            cy + 12f,
            paint
        )

        paint.textSize = 12f
        paint.color = blue

        canvas.drawText(
            unit,
            cx,
            cy + 35f,
            paint
        )

        paint.textSize = 11f
        paint.color = muted

        canvas.drawText(
            title,
            cx,
            cy + radius + 25f,
            paint
        )

        drawGaugeTicks(
            canvas,
            cx,
            cy,
            radius
        )
    }

    private fun drawGaugeTicks(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.argb(100, 190, 215, 230)

        for (i in 0..12) {
            val angle = Math.toRadians(
                (135 + i * 22.5).toDouble()
            )

            val outer = radius - 2f
            val inner = radius - 13f

            val x1 =
                cx + cos(angle).toFloat() * outer

            val y1 =
                cy + sin(angle).toFloat() * outer

            val x2 =
                cx + cos(angle).toFloat() * inner

            val y2 =
                cy + sin(angle).toFloat() * inner

            canvas.drawLine(
                x1,
                y1,
                x2,
                y2,
                paint
            )
        }
    }

    private fun drawAIOrb(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {
        val wave =
            ((sin(pulse.toDouble()) + 1.0) / 2.0)
                .toFloat()

        val outerRadius =
            82f + wave * 12f

        paint.style = Paint.Style.FILL

        paint.shader = RadialGradient(
            cx,
            cy,
            outerRadius,
            intArrayOf(
                Color.argb(180, 70, 190, 255),
                Color.argb(80, 30, 120, 220),
                Color.argb(0, 0, 0, 0)
            ),
            floatArrayOf(
                0f,
                0.55f,
                1f
            ),
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            cx,
            cy,
            outerRadius,
            paint
        )

        paint.shader = null

        val orbColor = when (aiState) {
            AIState.LISTENING ->
                Color.rgb(0, 225, 255)

            AIState.THINKING ->
                Color.rgb(255, 180, 50)

            AIState.SPEAKING ->
                Color.rgb(80, 255, 160)

            AIState.ERROR ->
                Color.RED

            else ->
                blue
        }

        paint.color = orbColor
        paint.setShadowLayer(
            35f,
            0f,
            0f,
            orbColor
        )

        canvas.drawCircle(
            cx,
            cy,
            48f + wave * 4f,
            paint
        )

        paint.clearShadowLayer()

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.color = Color.WHITE

        canvas.drawCircle(
            cx,
            cy,
            57f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 24f
        paint.color = Color.WHITE

        canvas.drawText(
            "AI",
            cx,
            cy + 8f,
            paint
        )

        paint.textSize = 10f
        paint.color = Color.argb(210, 230, 245, 255)

        val stateText = when (aiState) {
            AIState.LISTENING -> "LISTENING"
            AIState.THINKING -> "THINKING"
            AIState.SPEAKING -> "SPEAKING"
            AIState.ERROR -> "ERROR"
            else -> "READY"
        }

        canvas.drawText(
            stateText,
            cx,
            cy + 78f,
            paint
        )
    }

    private fun drawCenterInfo(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.shader = null
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = Color.WHITE
        paint.textSize = 15f

        canvas.drawText(
            "PEUGEOT PARS",
            w / 2f,
            h * 0.60f,
            paint
        )

        paint.color = muted
        paint.textSize = 11f

        canvas.drawText(
            if (data.canConnected) {
                "VEHICLE DATA CONNECTED"
            } else {
                "WAITING FOR CAN / GPCU"
            },
            w / 2f,
            h * 0.635f,
            paint
        )
    }

    private fun drawBottomPanels(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val top = h * 0.73f
        val bottom = h - 25f

        drawInfoPanel(
            canvas,
            RectF(
                25f,
                top,
                w * 0.30f,
                bottom
            ),
            "ENGINE TEMP",
            "${data.engineTempC} °C"
        )

        drawInfoPanel(
            canvas,
            RectF(
                w * 0.35f,
                top,
                w * 0.65f,
                bottom
            ),
            "FUEL",
            "${data.fuelPercent}%"
        )

        drawInfoPanel(
            canvas,
            RectF(
                w * 0.70f,
                top,
                w - 25f,
                bottom
            ),
            "BATTERY",
            "%.1f V".format(data.batteryVoltage)
        )

        drawWarnings(
            canvas,
            w,
            top - 28f
        )
    }

    private fun drawInfoPanel(
        canvas: Canvas,
        rect: RectF,
        title: String,
        value: String
    ) {
        paint.shader = LinearGradient(
            rect.left,
            rect.top,
            rect.right,
            rect.bottom,
            Color.argb(190, 12, 26, 42),
            Color.argb(130, 5, 12, 21),
            Shader.TileMode.CLAMP
        )

        paint.style = Paint.Style.FILL

        canvas.drawRoundRect(
            rect,
            16f,
            16f,
            paint
        )

        paint.shader = null

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(80, 100, 200, 255)

        canvas.drawRoundRect(
            rect,
            16f,
            16f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.textSize = 10f
        paint.color = muted

        canvas.drawText(
            title,
            rect.centerX(),
            rect.top + 21f,
            paint
        )

        paint.textSize = 18f
        paint.color = Color.WHITE

        canvas.drawText(
            value,
            rect.centerX(),
            rect.top + 47f,
            paint
        )
    }

    private fun drawWarnings(
        canvas: Canvas,
        w: Float,
        y: Float
    ) {
        val warnings = mutableListOf<String>()

        if (data.checkEngine) {
            warnings.add("ENGINE")
        }

        if (data.absWarning) {
            warnings.add("ABS")
        }

        if (data.airbagWarning) {
            warnings.add("AIRBAG")
        }

        if (data.ecuErrorCount > 0) {
            warnings.add("DTC ${data.ecuErrorCount}")
        }

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 10f

        if (warnings.isEmpty()) {
            paint.color = Color.rgb(80, 255, 170)

            canvas.drawText(
                "NO ACTIVE WARNINGS",
                w / 2f,
                y,
                paint
            )

            return
        }

        paint.color = Color.rgb(255, 90, 90)

        canvas.drawText(
            warnings.joinToString("   •   "),
            w / 2f,
            y,
            paint
        )
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (event.action == MotionEvent.ACTION_UP) {

            val dx =
                event.x - width / 2f

            val dy =
                event.y - height * 0.43f

            val distance =
                kotlin.math.sqrt(
                    dx * dx + dy * dy
                )

            if (distance < 110f) {

                performClick()

                onAIOrbClick?.invoke()

                return true
            }
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
