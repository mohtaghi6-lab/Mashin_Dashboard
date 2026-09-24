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
import kotlin.math.sqrt


class HomePageView(
    context: Context
) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var vehicleData = VehicleData.demo()
    private var aiState = AIState.IDLE
    private var pulse = 0f

    var onAIOrbClick: (() -> Unit)? = null
    var onCarClick: (() -> Unit)? = null
    var onMusicClick: (() -> Unit)? = null
    var onNavigationClick: (() -> Unit)? = null
    var onPhoneClick: (() -> Unit)? = null
    var onScannerClick: (() -> Unit)? = null

    private val blue = Color.rgb(70, 190, 255)
    private val cyan = Color.rgb(120, 230, 255)
    private val white = Color.WHITE
    private val muted = Color.rgb(145, 165, 180)

    private val glass = Color.argb(145, 8, 20, 34)
    private val glassStrong = Color.argb(190, 9, 24, 40)

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

        if (w <= 0f || h <= 0f) {
            return
        }

        drawBackground(canvas, w, h)
        drawHeader(canvas, w, h)

        drawGauge(
            canvas,
            w * 0.19f,
            h * 0.42f,
            min(w, h) * 0.145f,
            vehicleData.speedKmh.toFloat(),
            240f,
            "km/h",
            "SPEED"
        )

        drawGauge(
            canvas,
            w * 0.81f,
            h * 0.42f,
            min(w, h) * 0.145f,
            vehicleData.rpm.toFloat(),
            8000f,
            "RPM",
            "ENGINE"
        )

        drawAIOrb(
            canvas,
            w / 2f,
            h * 0.40f,
            min(w, h) * 0.105f
        )

        drawCenterInfo(canvas, w, h)
        drawStatusCards(canvas, w, h)
        drawBottomMenu(canvas, w, h)

        pulse += 0.035f

        if (pulse > 1000f) {
            pulse = 0f
        }

        postInvalidateDelayed(40L)
    }

    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            h,
            intArrayOf(
                Color.rgb(1, 5, 11),
                Color.rgb(3, 15, 27),
                Color.rgb(7, 24, 39)
            ),
            floatArrayOf(
                0f,
                0.52f,
                1f
            ),
            Shader.TileMode.CLAMP
        )

        paint.style = Paint.Style.FILL

        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            paint
        )

        paint.shader = null

        val glow = RadialGradient(
            w / 2f,
            h * 0.39f,
            min(w, h) * 0.58f,
            intArrayOf(
                Color.argb(55, 45, 170, 235),
                Color.argb(24, 25, 100, 165),
                Color.TRANSPARENT
            ),
            null,
            Shader.TileMode.CLAMP
        )

        paint.shader = glow

        canvas.drawCircle(
            w / 2f,
            h * 0.39f,
            min(w, h) * 0.58f,
            paint
        )

        paint.shader = null

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(18, 100, 205, 255)

        val grid = 85f

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

        paint.style = Paint.Style.FILL
    }

    private fun drawHeader(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val now = Date()

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.color = white
        paint.textSize = 28f

        canvas.drawText(
            timeFormat.format(now),
            34f,
            48f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 10f
        paint.color = muted

        canvas.drawText(
            dateFormat.format(now),
            36f,
            66f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 14f
        paint.color = white

        canvas.drawText(
            "PEUGEOT PARS",
            w - 34f,
            44f,
            paint
        )

        paint.textSize = 9f
        paint.color = cyan

        canvas.drawText(
            "BMW LUXURY • VEHICLE OS",
            w - 34f,
            61f,
            paint
        )

        paint.textSize = 8f
        paint.color =
            if (vehicleData.canConnected) {
                cyan
            } else {
                muted
            }

        canvas.drawText(
            if (vehicleData.canConnected) {
                "● CAN ONLINE"
            } else {
                "● DEMO MODE"
            },
            w - 34f,
            76f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        maxValue: Float,
        unit: String,
        label: String
    ) {
        val progress =
            (value / maxValue).coerceIn(0f, 1f)

        val rect = RectF(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius
        )

        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

        paint.strokeWidth = 15f
        paint.color = Color.argb(
            35,
            80,
            190,
            255
        )

        canvas.drawArc(
            rect,
            135f,
            270f,
            false,
            paint
        )

        paint.strokeWidth = 5f
        paint.color = cyan

        canvas.drawArc(
            rect,
            135f,
            270f * progress,
            false,
            paint
        )

        paint.strokeWidth = 2f
        paint.color = Color.argb(
            105,
            175,
            230,
            255
        )

        for (i in 0..24) {
            val angle = Math.toRadians(
                135.0 + i * 11.25
            )

            val outer = radius + 10f

            val inner =
                if (i % 3 == 0) {
                    radius - 5f
                } else {
                    radius + 2f
                }

            canvas.drawLine(
                cx + cos(angle).toFloat() * inner,
                cy + sin(angle).toFloat() * inner,
                cx + cos(angle).toFloat() * outer,
                cy + sin(angle).toFloat() * outer,
                paint
            )
        }

        val needle = Math.toRadians(
            135.0 + 270.0 * progress
        )

        paint.strokeWidth = 3.5f
        paint.color = white

        canvas.drawLine(
            cx,
            cy,
            cx + cos(needle).toFloat() *
                    (radius - 17f),
            cy + sin(needle).toFloat() *
                    (radius - 17f),
            paint
        )

        paint.style = Paint.Style.FILL
        paint.color = blue

        canvas.drawCircle(
            cx,
            cy,
            6f,
            paint
        )

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = radius * 0.27f
        paint.color = white

        canvas.drawText(
            value.toInt().toString(),
            cx,
            cy + radius * 0.10f,
            paint
        )

        paint.textSize = 10f
        paint.color = cyan

        canvas.drawText(
            unit,
            cx,
            cy + radius * 0.30f,
            paint
        )

        paint.textSize = 8f
        paint.color = muted

        canvas.drawText(
            label,
            cx,
            cy + radius * 0.47f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
        paint.strokeCap = Paint.Cap.BUTT
    }

    private fun drawAIOrb(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {
        val time = System.currentTimeMillis()

        val wave =
            ((sin(time / 250.0) + 1.0) / 2.0).toFloat()

        val statePower =
            when (aiState) {
                AIState.LISTENING -> 1.40f
                AIState.THINKING -> 1.55f
                AIState.SPEAKING -> 1.72f
                AIState.ERROR -> 1.25f
                else -> 1.18f
            }

        val glowRadius =
            radius * (statePower + wave * 0.22f)

        val glowPaint =
            Paint(Paint.ANTI_ALIAS_FLAG)

        glowPaint.shader = RadialGradient(
            cx,
            cy,
            glowRadius,
            intArrayOf(
                Color.argb(220, 140, 240, 255),
                Color.argb(95, 65, 190, 255),
                Color.argb(20, 30, 120, 220),
                Color.TRANSPARENT
            ),
            floatArrayOf(
                0f,
                0.42f,
                0.70f,
                1f
            ),
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            cx,
            cy,
            glowRadius,
            glowPaint
        )

        paint.shader = RadialGradient(
            cx - radius * 0.25f,
            cy - radius * 0.30f,
            radius * 1.15f,
            intArrayOf(
                Color.rgb(225, 250, 255),
                Color.rgb(75, 200, 255),
                Color.rgb(18, 80, 145),
                Color.rgb(2, 18, 36)
            ),
            floatArrayOf(
                0f,
                0.34f,
                0.72f,
                1f
            ),
            Shader.TileMode.CLAMP
        )

        paint.style = Paint.Style.FILL

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )

        paint.shader = null

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.5f
        paint.color = Color.argb(
            220,
            185,
            245,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )

        paint.strokeWidth = 1f
        paint.color = Color.argb(
            110,
            120,
            220,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            radius * 1.28f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = radius * 0.31f
        paint.color = white

        canvas.drawText(
            "AI",
            cx,
            cy + radius * 0.08f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = radius * 0.115f

        paint.color = Color.argb(
            235,
            225,
            248,
            255
        )

        val stateText =
            when (aiState) {
                AIState.IDLE -> "READY"
                AIState.LISTENING -> "LISTENING"
                AIState.THINKING -> "THINKING"
                AIState.SPEAKING -> "SPEAKING"
                AIState.ERROR -> "ERROR"
            }

        canvas.drawText(
            stateText,
            cx,
            cy + radius * 0.40f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawCenterInfo(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 16f
        paint.color = white

        canvas.drawText(
            "سلام، آماده‌ام",
            w / 2f,
            h * 0.585f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 10f
        paint.color = muted

        canvas.drawText(
            "برای فرمان صوتی روی هسته هوش مصنوعی لمس کنید",
            w / 2f,
            h * 0.615f,
            paint
        )

        paint.textSize = 8f
        paint.color = cyan

        canvas.drawText(
            "PERSIAN VOICE • AI ASSISTANT",
            w / 2f,
            h * 0.645f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawStatusCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val top = h * 0.69f
        val bottom = h * 0.775f

        val margin = 28f
        val gap = 10f

        val cardW =
            (w - margin * 2f - gap * 2f) / 3f

        drawGlassCard(
            canvas,
            margin,
            top,
            margin + cardW,
            bottom,
            "ENGINE",
            "${vehicleData.engineTempC}°C"
        )

        drawGlassCard(
            canvas,
            margin + cardW + gap,
            top,
            margin + cardW * 2f + gap,
            bottom,
            "FUEL",
            "${vehicleData.fuelPercent}%"
        )

        drawGlassCard(
            canvas,
            margin + cardW * 2f + gap * 2f,
            top,
            w - margin,
            bottom,
            "CAN",
            if (vehicleData.canConnected) {
                "ONLINE"
            } else {
                "DEMO"
            }
        )
    }

    private fun drawBottomMenu(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val top = h * 0.80f
        val bottom = h * 0.94f

        val margin = 28f
        val gap = 10f

        val cardW =
            (w - margin * 2f - gap * 2f) / 3f

        drawActionCard(
            canvas,
            margin,
            top,
            margin + cardW,
            bottom,
            "CAR",
            "VEHICLE",
            "🚗"
        )

        drawActionCard(
            canvas,
            margin + cardW + gap,
            top,
            margin + cardW * 2f + gap,
            bottom,
            "MUSIC",
            "MEDIA",
            "♪"
        )

        drawActionCard(
            canvas,
            margin + cardW * 2f + gap * 2f,
            top,
            w - margin,
            bottom,
            "NAV",
            "NAVIGATION",
            "⌖"
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
        val rect = RectF(
            left,
            top,
            right,
            bottom
        )

        paint.style = Paint.Style.FILL
        paint.color = glass

        canvas.drawRoundRect(
            rect,
            18f,
            18f,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(
            70,
            110,
            210,
            255
        )

        canvas.drawRoundRect(
            rect,
            18f,
            18f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 8f
        paint.color = muted

        canvas.drawText(
            title,
            left + 13f,
            top + 22f,
            paint
        )

        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 12f
        paint.color = white

        canvas.drawText(
            value,
            left + 13f,
            top + 43f,
            paint
        )

        paint.color = cyan

        canvas.drawCircle(
            right - 14f,
            top + 17f,
            3f,
            paint
        )
    }

    private fun drawActionCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        title: String,
        subtitle: String,
        icon: String
    ) {
        val rect = RectF(
            left,
            top,
            right,
            bottom
        )

        paint.style = Paint.Style.FILL
        paint.color = glassStrong

        canvas.drawRoundRect(
            rect,
            20f,
            20f,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(
            85,
            100,
            215,
            255
        )

        canvas.drawRoundRect(
            rect,
            20f,
            20f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER

        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 22f
        paint.color = cyan

        canvas.drawText(
            icon,
            (left + right) / 2f,
            top + 34f,
            paint
        )

        paint.textSize = 9f
        paint.color = white

        canvas.drawText(
            title,
            (left + right) / 2f,
            top + 57f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 7f
        paint.color = muted

        canvas.drawText(
            subtitle,
            (left + right) / 2f,
            top + 72f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {
        if (event.action != MotionEvent.ACTION_UP) {
            return true
        }

        val w = width.toFloat()
        val h = height.toFloat()

        val orbX = w / 2f
        val orbY = h * 0.40f
        val orbRadius = min(w, h) * 0.105f

        val dx = event.x - orbX
        val dy = event.y - orbY

        val distance =
            sqrt(dx * dx + dy * dy)

        if (distance <= orbRadius * 1.25f) {
            onAIOrbClick?.invoke()
            performClick()
            return true
        }

        val margin = 28f
        val gap = 10f
        val top = h * 0.80f
        val bottom = h * 0.94f

        if (event.y in top..bottom) {
            val cardW =
                (w - margin * 2f - gap * 2f) / 3f

            when {
                event.x >= margin &&
                        event.x <= margin + cardW -> {
                    onCarClick?.invoke()
                    performClick()
                    return true
                }

                event.x >= margin + cardW + gap &&
                        event.x <= margin +
                        cardW * 2f +
                        gap -> {
                    onMusicClick?.invoke()
                    performClick()
                    return true
                }

                event.x >= margin +
                        cardW * 2f +
                        gap * 2f &&
                        event.x <= w - margin -> {
                    onNavigationClick?.invoke()
                    performClick()
                    return true
                }
            }
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
