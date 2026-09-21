package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
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
import kotlin.math.sqrt

class DashboardView(
    context: Context
) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val needlePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var data = VehicleData.demo()
    private var aiState = AIState.IDLE

    private var pulse = 0f

    private var displaySpeed = data.speedKmh.toFloat()
    private var displayRpm = data.rpm.toFloat()
    private var displayTemp = data.engineTempC.toFloat()
    private var displayFuel = data.fuelPercent.toFloat()
    private var displayBattery = data.batteryVoltage

    var onAIOrbClick: (() -> Unit)? = null

    private val blue = Color.rgb(70, 190, 255)
    private val cyan = Color.rgb(100, 225, 255)
    private val white = Color.rgb(245, 250, 255)
    private val muted = Color.rgb(135, 158, 178)
    private val green = Color.rgb(75, 245, 165)
    private val orange = Color.rgb(255, 180, 65)
    private val red = Color.rgb(255, 75, 85)

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

        smoothValues()

        drawBackground(canvas, w, h)
        drawTopBar(canvas, w)
        drawStatusBar(canvas, w)

        drawGauge(
            canvas,
            w * 0.235f,
            h * 0.465f,
            min(w, h) * 0.205f,
            displaySpeed,
            300f,
            false,
            "km/h",
            "SPEED"
        )

        drawGauge(
            canvas,
            w * 0.765f,
            h * 0.465f,
            min(w, h) * 0.205f,
            displayRpm,
            8000f,
            true,
            "x1000",
            "RPM"
        )

        drawCenterVehiclePanel(canvas, w, h)
        drawAIOrb(canvas, w / 2f, h * 0.425f)

        drawBottomInfo(canvas, w, h)
        drawWarnings(canvas, w, h)

        pulse += 0.045f
        postInvalidateOnAnimation()
    }

    private fun smoothValues() {
        displaySpeed = approach(
            displaySpeed,
            data.speedKmh.toFloat(),
            0.14f
        )

        displayRpm = approach(
            displayRpm,
            data.rpm.toFloat(),
            0.14f
        )

        displayTemp = approach(
            displayTemp,
            data.engineTempC.toFloat(),
            0.12f
        )

        displayFuel = approach(
            displayFuel,
            data.fuelPercent.toFloat(),
            0.10f
        )

        displayBattery = approach(
            displayBattery,
            data.batteryVoltage,
            0.10f
        )
    }

    private fun approach(
        current: Float,
        target: Float,
        factor: Float
    ): Float {
        return current + (target - current) * factor
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
            Color.rgb(5, 12, 21),
            Color.rgb(0, 2, 7),
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

        val centerGlow = RadialGradient(
            w / 2f,
            h * 0.45f,
            min(w, h) * 0.48f,
            intArrayOf(
                Color.argb(42, 40, 145, 220),
                Color.argb(12, 20, 80, 130),
                Color.TRANSPARENT
            ),
            floatArrayOf(
                0f,
                0.5f,
                1f
            ),
            Shader.TileMode.CLAMP
        )

        paint.shader = centerGlow

        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            paint
        )

        paint.shader = null

        paint.color = Color.argb(13, 100, 190, 255)
        paint.strokeWidth = 1f

        val grid = 90f

        var x = 0f

        while (x < w) {
            canvas.drawLine(
                x,
                0f,
                x,
                h,
                paint
            )
            x += grid
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
            y += grid
        }
    }

    private fun drawTopBar(
        canvas: Canvas,
        w: Float
    ) {
        paint.shader = null
        paint.style = Paint.Style.FILL

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = white
        paint.textSize = 23f

        canvas.drawText(
            "PEUGEOT",
            38f,
            38f,
            paint
        )

        paint.color = blue
        paint.textSize = 11f

        canvas.drawText(
            "VEHICLE OS",
            40f,
            56f,
            paint
        )

        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.argb(210, 220, 235, 245)
        paint.textSize = 13f

        canvas.drawText(
            "LUXURY DRIVE",
            w / 2f,
            38f,
            paint
        )

        paint.color = muted
        paint.textSize = 10f

        canvas.drawText(
            "PARS 93  •  DIGITAL COCKPIT",
            w / 2f,
            55f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.color = white
        paint.textSize = 17f

        val time = java.text.SimpleDateFormat(
            "HH:mm",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        canvas.drawText(
            time,
            w - 38f,
            38f,
            paint
        )

        paint.color = muted
        paint.textSize = 10f

        val date = java.text.SimpleDateFormat(
            "dd/MM/yyyy",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        canvas.drawText(
            date,
            w - 38f,
            55f,
            paint
        )
    }

    private fun drawStatusBar(
        canvas: Canvas,
        w: Float
    ) {
        val rect = RectF(
            28f,
            70f,
            w - 28f,
            105f
        )

        paint.style = Paint.Style.FILL

        paint.shader = LinearGradient(
            rect.left,
            rect.top,
            rect.right,
            rect.bottom,
            Color.argb(150, 12, 28, 45),
            Color.argb(95, 4, 10, 18),
            Shader.TileMode.CLAMP
        )

        canvas.drawRoundRect(
            rect,
            18f,
            18f,
            paint
        )

        paint.shader = null

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(65, 90, 190, 255)

        canvas.drawRoundRect(
            rect,
            18f,
            18f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 10f

        drawConnection(
            canvas,
            "CAN",
            data.canConnected,
            50f,
            92f
        )

        drawConnection(
            canvas,
            "OBD",
            data.obdConnected,
            125f,
            92f
        )

        paint.textAlign = Paint.Align.CENTER
        paint.color = if (
            data.canConnected || data.obdConnected
        ) {
            green
        } else {
            muted
        }

        canvas.drawText(
            if (
                data.canConnected || data.obdConnected
            ) {
                "LIVE VEHICLE DATA"
            } else {
                "DEMO • WAITING FOR CAN / GPCU"
            },
            w / 2f,
            92f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.color = green

        canvas.drawText(
            "SYSTEM READY",
            w - 50f,
            92f,
            paint
        )
    }

    private fun drawConnection(
        canvas: Canvas,
        label: String,
        connected: Boolean,
        x: Float,
        y: Float
    ) {
        paint.color = if (connected) {
            green
        } else {
            Color.rgb(90, 105, 120)
        }

        canvas.drawCircle(
            x,
            y - 3f,
            4f,
            paint
        )

        paint.color = if (connected) {
            white
        } else {
            muted
        }

        canvas.drawText(
            label,
            x + 10f,
            y,
            paint
        )
    }

    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        maximum: Float,
        rpmGauge: Boolean,
        unit: String,
        title: String
    ) {
        drawGaugeOuter(
            canvas,
            cx,
            cy,
            radius
        )

        drawGaugeTicks(
            canvas,
            cx,
            cy,
            radius,
            maximum,
            rpmGauge
        )

        if (rpmGauge) {
            drawRedZone(
                canvas,
                cx,
                cy,
                radius,
                maximum
            )
        }

        drawGaugeProgress(
            canvas,
            cx,
            cy,
            radius,
            value,
            maximum
        )

        drawNeedle(
            canvas,
            cx,
            cy,
            radius,
            value,
            maximum
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = white
        paint.textSize = 31f

        val number = if (rpmGauge) {
            "%.1f".format(
                value.coerceIn(0f, maximum) / 1000f
            )
        } else {
            value.toInt().toString()
        }

        canvas.drawText(
            number,
            cx,
            cy + 15f,
            paint
        )

        paint.color = blue
        paint.textSize = 10f

        canvas.drawText(
            unit,
            cx,
            cy + 34f,
            paint
        )

        paint.color = muted
        paint.textSize = 10f

        canvas.drawText(
            title,
            cx,
            cy + radius + 25f,
            paint
        )
    }

    private fun drawGaugeOuter(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

        paint.strokeWidth = 8f
        paint.color = Color.argb(55, 160, 190, 215)

        canvas.drawArc(
            RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
            ),
            135f,
            270f,
            false,
            paint
        )

        paint.strokeWidth = 2f
        paint.color = Color.argb(80, 90, 190, 255)

        canvas.drawCircle(
            cx,
            cy,
            radius + 10f,
            paint
        )

        paint.strokeWidth = 1f
        paint.color = Color.argb(30, 150, 210, 255)

        canvas.drawCircle(
            cx,
            cy,
            radius - 20f,
            paint
        )
    }

    private fun drawGaugeTicks(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        maximum: Float,
        rpmGauge: Boolean
    ) {
        val majorCount = 12

        for (i in 0..60) {
            val fraction = i / 60f
            val angleDegrees = 135f + 270f * fraction
            val angle = Math.toRadians(
                angleDegrees.toDouble()
            )

            val major = i % 5 == 0

            val outer =
                radius - 5f

            val inner =
                if (major) {
                    radius - 20f
                } else {
                    radius - 13f
                }

            val x1 =
                cx + cos(angle).toFloat() * outer

            val y1 =
                cy + sin(angle).toFloat() * outer

            val x2 =
                cx + cos(angle).toFloat() * inner

            val y2 =
                cy + sin(angle).toFloat() * inner

            paint.style = Paint.Style.STROKE
            paint.strokeWidth =
                if (major) 2.2f else 1f

            paint.color =
                if (major) {
                    Color.argb(210, 215, 235, 245)
                } else {
                    Color.argb(80, 150, 180, 200)
                }

            canvas.drawLine(
                x1,
                y1,
                x2,
                y2,
                paint
            )
        }

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 9f
        paint.color = muted

        for (i in 0..majorCount) {
            val fraction = i / majorCount.toFloat()

            val angleDegrees =
                135f + 270f * fraction

            val angle = Math.toRadians(
                angleDegrees.toDouble()
            )

            val labelRadius =
                radius - 32f

            val x =
                cx + cos(angle).toFloat() * labelRadius

            val y =
                cy + sin(angle).toFloat() * labelRadius + 3f

            val labelValue =
                if (rpmGauge) {
                    i.toString()
                } else {
                    (i * 25).toString()
                }

            canvas.drawText(
                labelValue,
                x,
                y,
                paint
            )
        }
    }

    private fun drawRedZone(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        maximum: Float
    ) {
        val redStart = 6500f

        val startFraction =
            redStart / maximum

        val sweep =
            270f * (1f - startFraction)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 7f
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = Color.argb(210, 255, 65, 75)

        canvas.drawArc(
            RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
            ),
            135f + 270f * startFraction,
            sweep,
            false,
            paint
        )
    }

    private fun drawGaugeProgress(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        maximum: Float
    ) {
        val safe =
            value.coerceIn(0f, maximum)

        val sweep =
            270f * safe / maximum

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5f
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = blue

        paint.setShadowLayer(
            12f,
            0f,
            0f,
            Color.argb(180, 70, 190, 255)
        )

        canvas.drawArc(
            RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
            ),
            135f,
            sweep,
            false,
            paint
        )

        paint.clearShadowLayer()
    }

    private fun drawNeedle(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        maximum: Float
    ) {
        val fraction =
            value.coerceIn(0f, maximum) / maximum

        val angleDegrees =
            135f + 270f * fraction

        val angle =
            Math.toRadians(angleDegrees.toDouble())

        val length =
            radius - 30f

        val endX =
            cx + cos(angle).toFloat() * length

        val endY =
            cy + sin(angle).toFloat() * length

        needlePaint.style = Paint.Style.STROKE
        needlePaint.strokeWidth = 4f
        needlePaint.strokeCap = Paint.Cap.ROUND
        needlePaint.color = white

        needlePaint.setShadowLayer(
            12f,
            0f,
            0f,
            Color.argb(210, 80, 200, 255)
        )

        canvas.drawLine(
            cx,
            cy,
            endX,
            endY,
            needlePaint
        )

        needlePaint.clearShadowLayer()

        paint.style = Paint.Style.FILL
        paint.color = white

        canvas.drawCircle(
            cx,
            cy,
            8f,
            paint
        )

        paint.color = blue

        canvas.drawCircle(
            cx,
            cy,
            4f,
            paint
        )
    }

    private fun drawCenterVehiclePanel(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val rect = RectF(
            w * 0.365f,
            h * 0.555f,
            w * 0.635f,
            h * 0.695f
        )

        paint.style = Paint.Style.FILL

        paint.shader = LinearGradient(
            rect.left,
            rect.top,
            rect.right,
            rect.bottom,
            Color.argb(155, 14, 32, 51),
            Color.argb(80, 4, 10, 18),
            Shader.TileMode.CLAMP
        )

        canvas.drawRoundRect(
            rect,
            22f,
            22f,
            paint
        )

        paint.shader = null

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(80, 90, 200, 255)

        canvas.drawRoundRect(
            rect,
            22f,
            22f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = muted
        paint.textSize = 9f

        canvas.drawText(
            "PEUGEOT PARS",
            w / 2f,
            rect.top + 21f,
            paint
        )

        paint.color = white
        paint.textSize = 25f

        canvas.drawText(
            displaySpeed.toInt().toString(),
            w / 2f,
            rect.top + 56f,
            paint
        )

        paint.color = blue
        paint.textSize = 9f

        canvas.drawText(
            "CURRENT SPEED  km/h",
            w / 2f,
            rect.top + 74f,
            paint
        )

        paint.color = if (
            data.canConnected
        ) green else muted

        paint.textSize = 8f

        canvas.drawText(
            if (data.canConnected) {
                "VEHICLE DATA CONNECTED"
            } else {
                "WAITING FOR CAN / GPCU"
            },
            w / 2f,
            rect.bottom - 12f,
            paint
        )
    }

    private fun drawAIOrb(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {
        val wave =
            ((sin(pulse.toDouble()) + 1.0) / 2.0)
                .toFloat()

        val stateColor = when (aiState) {
            AIState.LISTENING -> cyan
            AIState.THINKING -> orange
            AIState.SPEAKING -> green
            AIState.ERROR -> red
            AIState.IDLE -> blue
        }

        val glowRadius =
            68f + wave * 14f

        paint.style = Paint.Style.FILL

        paint.shader = RadialGradient(
            cx,
            cy,
            glowRadius,
            intArrayOf(
                Color.argb(190, stateColor shr 16 and 255,
                    stateColor shr 8 and 255,
                    stateColor and 255),
                Color.argb(65, 40, 150, 255),
                Color.TRANSPARENT
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
            glowRadius,
            paint
        )

        paint.shader = null

        paint.color = Color.argb(
            225,
            Color.red(stateColor),
            Color.green(stateColor),
            Color.blue(stateColor)
        )

        paint.setShadowLayer(
            30f,
            0f,
            0f,
            stateColor
        )

        canvas.drawCircle(
            cx,
            cy,
            43f + wave * 4f,
            paint
        )

        paint.clearShadowLayer()

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.argb(220, 235, 250, 255)

        canvas.drawCircle(
            cx,
            cy,
            53f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = white
        paint.textSize = 20f

        canvas.drawText(
            "AI",
            cx,
            cy + 7f,
            paint
        )

        paint.color = stateColor
        paint.textSize = 9f

        canvas.drawText(
            when (aiState) {
                AIState.LISTENING -> "LISTENING"
                AIState.THINKING -> "THINKING"
                AIState.SPEAKING -> "SPEAKING"
                AIState.ERROR -> "ERROR"
                AIState.IDLE -> "READY"
            },
            cx,
            cy + 72f,
            paint
        )
    }

    private fun drawBottomInfo(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val top = h * 0.775f
        val bottom = h - 22f

        drawInfoPanel(
            canvas,
            RectF(
                28f,
                top,
                w * 0.245f,
                bottom
            ),
            "ENGINE TEMP",
            "${displayTemp.toInt()} °C",
            if (displayTemp >= 110f) red else green
        )

        drawInfoPanel(
            canvas,
            RectF(
                w * 0.265f,
                top,
                w * 0.475f,
                bottom
            ),
            "FUEL",
            "${displayFuel.toInt()} %",
            blue
        )

        drawInfoPanel(
            canvas,
            RectF(
                w * 0.495f,
                top,
                w * 0.705f,
                bottom
            ),
            "BATTERY",
            "%.1f V".format(displayBattery),
            if (
                displayBattery in 12f..15f
            ) green else orange
        )

        drawInfoPanel(
            canvas,
            RectF(
                w * 0.725f,
                top,
                w - 28f,
                bottom
            ),
            "ODOMETER",
            "${data.odometerKm} km",
            white
        )
    }

    private fun drawInfoPanel(
        canvas: Canvas,
        rect: RectF,
        title: String,
        value: String,
        accent: Int
    ) {
        paint.style = Paint.Style.FILL

        paint.shader = LinearGradient(
            rect.left,
            rect.top,
            rect.right,
            rect.bottom,
            Color.argb(165, 12, 28, 45),
            Color.argb(85, 4, 10, 18),
            Shader.TileMode.CLAMP
        )

        canvas.drawRoundRect(
            rect,
            15f,
            15f,
            paint
        )

        paint.shader = null

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(65, 100, 190, 255)

        canvas.drawRoundRect(
            rect,
            15f,
            15f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = muted
        paint.textSize = 8f

        canvas.drawText(
            title,
            rect.centerX(),
            rect.top + 18f,
            paint
        )

        paint.color = accent
        paint.textSize = 16f

        canvas.drawText(
            value,
            rect.centerX(),
            rect.top + 42f,
            paint
        )
    }

    private fun drawWarnings(
        canvas: Canvas,
        w: Float,
        h: Float
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
            warnings.add(
                "DTC ${data.ecuErrorCount}"
            )
        }

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 9f

        if (warnings.isEmpty()) {
            paint.color = green

            canvas.drawText(
                "●  ALL VEHICLE SYSTEMS NORMAL",
                w / 2f,
                h * 0.735f,
                paint
            )
        } else {
            val blink =
                ((sin(pulse * 2.0) + 1.0) / 2.0)

            paint.color = Color.rgb(
                255,
                (80 + blink * 100).toInt(),
                85
            )

            canvas.drawText(
                warnings.joinToString(
                    "   •   "
                ),
                w / 2f,
                h * 0.735f,
                paint
            )
        }
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {

            val dx =
                event.x - width / 2f

            val dy =
                event.y - height * 0.425f

            val distance =
                sqrt(
                    dx * dx + dy * dy
                )

            if (distance < 115f) {
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
