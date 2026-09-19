package peugeot.platform.android.ui

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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class HomePageView(
    context: Context
) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var vehicleData = VehicleData.demo()
    private var aiState = AIState.IDLE
    private var pulse = 0f

    var onAIOrbClick: (() -> Unit)? = null

    private val blue = Color.rgb(74, 196, 255)
    private val cyan = Color.rgb(116, 231, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(142, 164, 180)
    private val panel = Color.argb(150, 10, 22, 36)

    private val timeFormat =
        SimpleDateFormat("HH:mm", Locale.getDefault())

    private val dateFormat =
        SimpleDateFormat("EEE, dd MMM", Locale.ENGLISH)

    fun setVehicleData(data: VehicleData) {
        vehicleData = data
        invalidate()
    }

    fun setAIState(state: AIState) {
        aiState = state
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        drawBackground(canvas, w, h)
        drawHeader(canvas, w)

        val radius = min(w, h) * 0.17f
        val gaugeY = h * 0.49f

        drawGauge(
            canvas,
            w * 0.22f,
            gaugeY,
            radius,
            vehicleData.speedKmh.toFloat(),
            240f,
            "km/h",
            "SPEED"
        )

        drawGauge(
            canvas,
            w * 0.78f,
            gaugeY,
            radius,
            vehicleData.rpm / 1000f,
            8f,
            "x1000 RPM",
            "ENGINE"
        )

        drawAIOrb(
            canvas,
            w / 2f,
            h * 0.42f,
            min(w, h) * 0.095f
        )

        drawCenterInfo(canvas, w, h)
        drawStatusCards(canvas, w, h)

        pulse += 0.035f
        postInvalidateOnAnimation()
    }

    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        canvas.drawColor(Color.rgb(2, 6, 12))

        val centerGlow = Paint(Paint.ANTI_ALIAS_FLAG)
        centerGlow.shader = RadialGradient(
            w * 0.50f,
            h * 0.40f,
            min(w, h) * 0.62f,
            intArrayOf(
                Color.rgb(8, 39, 58),
                Color.rgb(4, 17, 27),
                Color.rgb(2, 6, 12)
            ),
            floatArrayOf(0f, 0.52f, 1f),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(0f, 0f, w, h, centerGlow)

        val horizonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        horizonPaint.shader = LinearGradient(
            0f,
            h * 0.63f,
            0f,
            h,
            Color.argb(0, 50, 180, 255),
            Color.argb(65, 20, 100, 150),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(0f, h * 0.55f, w, h, horizonPaint)
    }

    private fun drawHeader(
        canvas: Canvas,
        w: Float
    ) {
        paint.style = Paint.Style.FILL

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        paint.textSize = 19f
        paint.color = white

        canvas.drawText(
            "PEUGEOT",
            34f,
            38f,
            paint
        )

        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        paint.color = muted

        canvas.drawText(
            "VEHICLE OS • MRT",
            35f,
            55f,
            paint
        )

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 32f
        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        paint.color = white

        canvas.drawText(
            timeFormat.format(Date()),
            w / 2f,
            42f,
            paint
        )

        paint.textSize = 10f
        paint.typeface = Typeface.DEFAULT
        paint.color = muted

        canvas.drawText(
            dateFormat.format(Date()).uppercase(Locale.ENGLISH),
            w / 2f,
            58f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 11f
        paint.color = cyan

        val canText =
            if (vehicleData.canConnected) "CAN ONLINE" else "CAN STANDBY"

        canvas.drawText(
            canText,
            w - 34f,
            38f,
            paint
        )

        paint.color = muted

        canvas.drawText(
            vehicleData.batteryVoltage.toString() + "V",
            w - 34f,
            55f,
            paint
        )
    }

    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        rawValue: Float,
        maximum: Float,
        unit: String,
        label: String
    ) {
        val progress =
            rawValue.coerceIn(0f, maximum) / maximum

        val rect = RectF(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius
        )

        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = radius * 0.055f
        paint.color = Color.rgb(19, 45, 61)

        canvas.drawArc(
            rect,
            140f,
            260f,
            false,
            paint
        )

        paint.color = blue

        canvas.drawArc(
            rect,
            140f,
            260f * progress,
            false,
            paint
        )

        paint.strokeCap = Paint.Cap.BUTT
        paint.strokeWidth = 1.2f
        paint.color = Color.rgb(72, 101, 118)

        for (i in 0..20) {
            val angle = Math.toRadians(
                140.0 + i * 13.0
            )

            val outer = radius * 0.90f
            val inner =
                if (i % 4 == 0) radius * 0.78f
                else radius * 0.84f

            canvas.drawLine(
                cx + cos(angle).toFloat() * inner,
                cy + sin(angle).toFloat() * inner,
                cx + cos(angle).toFloat() * outer,
                cy + sin(angle).toFloat() * outer,
                paint
            )
        }

        val needleAngle = Math.toRadians(
            140.0 + 260.0 * progress
        )

        paint.strokeWidth = radius * 0.018f
        paint.color = white

        canvas.drawLine(
            cx,
            cy,
            cx + cos(needleAngle).toFloat() * radius * 0.68f,
            cy + sin(needleAngle).toFloat() * radius * 0.68f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.color = cyan

        canvas.drawCircle(
            cx,
            cy,
            radius * 0.04f,
            paint
        )

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        paint.color = white
        paint.textSize = radius * 0.23f

        val valueText =
            if (maximum > 10f) {
                rawValue.toInt().toString()
            } else {
                String.format(
                    Locale.US,
                    "%.1f",
                    rawValue
                )
            }

        canvas.drawText(
            valueText,
            cx,
            cy + radius * 0.07f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = radius * 0.075f
        paint.color = cyan

        canvas.drawText(
            unit,
            cx,
            cy + radius * 0.23f,
            paint
        )

        paint.textSize = 10f
        paint.color = muted

        canvas.drawText(
            label,
            cx,
            cy + radius + 24f,
            paint
        )
    }

    private fun drawAIOrb(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {
        val wave =
            ((sin(pulse.toDouble()) + 1.0) / 2.0).toFloat()

        val stateColor = when (aiState) {
            AIState.LISTENING -> Color.rgb(0, 220, 255)
            AIState.THINKING -> Color.rgb(255, 185, 60)
            AIState.SPEAKING -> Color.rgb(90, 255, 170)
            AIState.ERROR -> Color.rgb(255, 80, 90)
            else -> blue
        }

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.2f + wave * 3.5f
        paint.color = Color.argb(
            90 + (wave * 70).toInt(),
            Color.red(stateColor),
            Color.green(stateColor),
            Color.blue(stateColor)
        )

        canvas.drawCircle(
            cx,
            cy,
            radius * (1.65f + wave * 0.16f),
            paint
        )

        paint.strokeWidth = 1.4f
        paint.color = Color.argb(
            145,
            120,
            230,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            radius * 1.35f,
            paint
        )

        paint.style = Paint.Style.FILL

        val orbPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        orbPaint.shader = RadialGradient(
            cx - radius * 0.28f,
            cy - radius * 0.34f,
            radius * 1.28f,
            intArrayOf(
                Color.rgb(150, 245, 255),
                stateColor,
                Color.rgb(0, 48, 110)
            ),
            floatArrayOf(0f, 0.48f, 1f),
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            cx,
            cy,
            radius,
            orbPaint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.color = Color.argb(190, 190, 250, 255)

        canvas.drawCircle(
            cx,
            cy,
            radius * 0.72f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        paint.textSize = radius * 0.42f
        paint.color = white

        canvas.drawText(
            "MRT",
            cx,
            cy + radius * 0.12f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = radius * 0.16f
        paint.color = Color.argb(
            220,
            220,
            250,
            255
        )

        val modeText = when (aiState) {
            AIState.LISTENING -> "LISTENING"
            AIState.THINKING -> "THINKING"
            AIState.SPEAKING -> "SPEAKING"
            AIState.ERROR -> "ERROR"
            else -> "VOICE AI"
        }

        canvas.drawText(
            modeText,
            cx,
            cy + radius * 0.42f,
            paint
        )
    }

    private fun drawCenterInfo(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER

        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        paint.textSize = 13f
        paint.color = Color.rgb(190, 220, 236)

        canvas.drawText(
            "سلام MRT",
            w / 2f,
            h * 0.60f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 10f
        paint.color = muted

        canvas.drawText(
            "آماده دریافت فرمان صوتی",
            w / 2f,
            h * 0.625f,
            paint
        )
    }

    private fun drawStatusCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val top = h * 0.70f
        val bottom = h * 0.80f
        val gap = 12f
        val margin = 30f
        val cardW = (w - margin * 2f - gap * 2f) / 3f

        drawGlassCard(
            canvas,
            margin,
            top,
            margin + cardW,
            bottom,
            "ENGINE",
            vehicleData.engineTempC.toString() + " °C"
        )

        drawGlassCard(
            canvas,
            margin + cardW + gap,
            top,
            margin + cardW * 2f + gap,
            bottom,
            "FUEL",
            vehicleData.fuelPercent.toString() + "%"
        )

        drawGlassCard(
            canvas,
            margin + cardW * 2f + gap * 2f,
            top,
            w - margin,
            bottom,
            "CAN",
            if (vehicleData.canConnected) "ONLINE" else "STANDBY"
        )
    }

    private fun drawGlassCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        title: String,
        value: String
    ) {
        paint.style = Paint.Style.FILL
        paint.color = panel

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            18f,
            18f,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.2f
        paint.color = Color.argb(
            110,
            90,
            190,
            235
        )

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            18f,
            18f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 9f
        paint.color = muted

        canvas.drawText(
            title,
            (left + right) / 2f,
            top + 25f,
            paint
        )

        paint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        paint.textSize = 17f
        paint.color = white

        canvas.drawText(
            value,
            (left + right) / 2f,
            top + 52f,
            paint
        )
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val dx = event.x - width / 2f
            val dy = event.y - height * 0.42f
            val distance = kotlin.math.sqrt(
                dx * dx + dy * dy
            )

            if (distance < min(width, height) * 0.14f) {
                onAIOrbClick?.invoke()
                return true
            }
        }

        return true
    }
}
