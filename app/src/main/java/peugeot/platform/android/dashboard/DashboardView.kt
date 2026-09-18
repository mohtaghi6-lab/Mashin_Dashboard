package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.vehicle.VehicleData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class DashboardView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var data = VehicleData.demo()

    private var pulse = 0f
    private var selectedCard = -1

    private val blue = Color.rgb(70, 190, 255)
    private val lightBlue = Color.rgb(130, 220, 255)
    private val dark = Color.rgb(3, 7, 13)
    private val panel = Color.rgb(9, 17, 27)

    init {
        isFocusable = true
        postInvalidateOnAnimation()
    }

    fun setVehicleData(value: VehicleData) {
        data = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        drawBackground(canvas, w, h)
        drawTopBar(canvas, w, h)

        val gaugeY = h * 0.48f
        val radius = min(w * 0.245f, h * 0.31f)

        drawGauge(
            canvas,
            w * 0.25f,
            gaugeY,
            radius,
            data.speedKmh.toFloat(),
            240f,
            "km/h"
        )

        drawGauge(
            canvas,
            w * 0.75f,
            gaugeY,
            radius,
            data.rpm / 1000f,
            8f,
            "x1000 RPM"
        )

        drawCenterAI(canvas, w * 0.5f, gaugeY)

        drawQuickCards(canvas, w, h)
        drawBottomBar(canvas, w, h)

        pulse += 0.035f
        if (pulse > Math.PI * 2) {
            pulse = 0f
        }

        postInvalidateOnAnimation()
    }

    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        canvas.drawColor(dark)

        val glow = Paint(Paint.ANTI_ALIAS_FLAG)
        glow.shader = RadialGradient(
            w * 0.5f,
            h * 0.48f,
            w * 0.55f,
            intArrayOf(
                Color.rgb(8, 35, 55),
                Color.rgb(4, 15, 25),
                dark
            ),
            floatArrayOf(0f, 0.48f, 1f),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(0f, 0f, w, h, glow)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.rgb(18, 42, 58)

        canvas.drawLine(
            0f,
            h * 0.17f,
            w,
            h * 0.17f,
            paint
        )

        canvas.drawLine(
            0f,
            h * 0.84f,
            w,
            h * 0.84f,
            paint
        )
    }

    private fun drawTopBar(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 25f

        canvas.drawText(
            "PEUGEOT PARS",
            38f,
            48f,
            paint
        )

        paint.textSize = 12f
        paint.color = Color.rgb(100, 175, 210)

        canvas.drawText(
            "VEHICLE OS",
            40f,
            67f,
            paint
        )

        val time = SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(Date())

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 32f
        paint.color = Color.WHITE

        canvas.drawText(
            time,
            w * 0.5f,
            51f,
            paint
        )

        paint.textSize = 12f
        paint.color = Color.rgb(120, 185, 215)

        canvas.drawText(
            "PEUGEOT INTELLIGENT COCKPIT",
            w * 0.5f,
            69f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 13f

        paint.color =
            if (data.canConnected)
                lightBlue
            else
                Color.rgb(150, 165, 175)

        canvas.drawText(
            if (data.canConnected) "● CAN ONLINE" else "● DEMO MODE",
            w - 38f,
            48f,
            paint
        )
    }

    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        max: Float,
        unit: String
    ) {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

        // Outer ring
        paint.strokeWidth = radius * 0.035f
        paint.color = Color.rgb(20, 42, 57)

        canvas.drawArc(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius,
            135f,
            270f,
            false,
            paint
        )

        // Active arc
        paint.color = blue

        canvas.drawArc(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius,
            135f,
            270f * (value.coerceIn(0f, max) / max),
            false,
            paint
        )

        // Inner ring
        paint.strokeWidth = 1.5f
        paint.color = Color.rgb(30, 60, 78)

        canvas.drawCircle(
            cx,
            cy,
            radius * 0.82f,
            paint
        )

        // Tick marks
        for (i in 0..24) {

            val angle = Math.toRadians(
                135.0 + i * 11.25
            )

            val outer = radius * 0.93f
            val inner =
                if (i % 3 == 0)
                    radius * 0.84f
                else
                    radius * 0.88f

            paint.strokeWidth =
                if (i % 3 == 0)
                    3f
                else
                    1.2f

            paint.color =
                if (i % 3 == 0)
                    Color.WHITE
                else
                    Color.rgb(90, 125, 145)

            canvas.drawLine(
                cx + cos(angle).toFloat() * inner,
                cy + sin(angle).toFloat() * inner,
                cx + cos(angle).toFloat() * outer,
                cy + sin(angle).toFloat() * outer,
                paint
            )
        }

        // Needle
        val needleAngle = Math.toRadians(
            135.0 +
                270.0 *
                (value.coerceIn(0f, max) / max)
        )

        paint.strokeWidth = 3.5f
        paint.color = Color.WHITE

        canvas.drawLine(
            cx,
            cy,
            cx + cos(needleAngle).toFloat() * radius * 0.70f,
            cy + sin(needleAngle).toFloat() * radius * 0.70f,
            paint
        )

        // Center hub
        paint.style = Paint.Style.FILL
        paint.color = blue

        canvas.drawCircle(
            cx,
            cy,
            radius * 0.045f,
            paint
        )

        // Number
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.color = Color.WHITE
        paint.textSize = radius * 0.30f

        canvas.drawText(
            if (max > 10)
                value.toInt().toString()
            else
                String.format(Locale.US, "%.1f", value),
            cx,
            cy + radius * 0.10f,
            paint
        )

        // Unit
        paint.textSize = radius * 0.075f
        paint.color = lightBlue

        canvas.drawText(
            unit,
            cx,
            cy + radius * 0.27f,
            paint
        )
    }

    private fun drawCenterAI(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {
        val wave =
            ((sin(pulse.toDouble()) + 1.0) / 2.0).toFloat()

        // Outer glow
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f + wave * 2f
        paint.color = Color.argb(
            (80 + wave * 100).toInt(),
            70,
            190,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            65f + wave * 8f,
            paint
        )

        // Main ring
        paint.strokeWidth = 4f
        paint.color = blue

        canvas.drawCircle(
            cx,
            cy,
            48f,
            paint
        )

        // Inner disc
        paint.style = Paint.Style.FILL

        val shader = RadialGradient(
            cx,
            cy,
            48f,
            intArrayOf(
                Color.rgb(30, 90, 125),
                Color.rgb(5, 25, 38),
                Color.rgb(2, 10, 18)
            ),
            null,
            Shader.TileMode.CLAMP
        )

        paint.shader = shader

        canvas.drawCircle(
            cx,
            cy,
            46f,
            paint
        )

        paint.shader = null

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.color = Color.WHITE
        paint.textSize = 25f

        canvas.drawText(
            "AI",
            cx,
            cy + 8f,
            paint
        )

        paint.textSize = 11f
        paint.color = lightBlue

        canvas.drawText(
            "آماده‌ام",
            cx,
            cy + 27f,
            paint
        )

        paint.textSize = 9f
        paint.color = Color.rgb(100, 150, 175)

        canvas.drawText(
            "VOICE",
            cx,
            cy + 84f,
            paint
        )
    }

    private fun drawQuickCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val labels = arrayOf(
            "CAR",
            "MUSIC",
            "NAV",
            "CALL",
            "SCAN"
        )

        val icons = arrayOf(
            "▣",
            "♫",
            "⌖",
            "☎",
            "⌕"
        )

        val gap = 14f
        val totalWidth = w - 70f
        val cardWidth =
            (totalWidth - gap * 4f) / 5f

        val top = h * 0.72f
        val bottom = top + 78f

        for (i in labels.indices) {

            val left =
                35f + i * (cardWidth + gap)

            val right =
                left + cardWidth

            paint.style = Paint.Style.FILL

            paint.color =
                if (selectedCard == i)
                    Color.rgb(13, 48, 67)
                else
                    panel

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
            paint.strokeWidth = 1.5f

            paint.color =
                if (selectedCard == i)
                    blue
                else
                    Color.rgb(30, 65, 82)

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

            paint.color = blue
            paint.textSize = 24f

            canvas.drawText(
                icons[i],
                (left + right) / 2f,
                top + 32f,
                paint
            )

            paint.color = Color.WHITE
            paint.textSize = 11f

            canvas.drawText(
                labels[i],
                (left + right) / 2f,
                top + 56f,
                paint
            )
        }
    }

    private fun drawBottomBar(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.LEFT

        paint.textSize = 11f
        paint.color = Color.rgb(80, 125, 150)

        canvas.drawText(
            "PEUGEOT VEHICLE OS • v31",
            35f,
            h - 24f,
            paint
        )

        paint.textAlign = Paint.Align.CENTER
        paint.color = Color.rgb(130, 165, 180)

        canvas.drawText(
            "ODO  ${data.odometerKm} km",
            w * 0.50f,
            h - 24f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT

        canvas.drawText(
            String.format(
                Locale.US,
                "%.1fV  •  %d°C",
                data.batteryVoltage,
                data.engineTempC
            ),
            w - 35f,
            h - 24f,
            paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_UP) {

            val w = width.toFloat()
            val h = height.toFloat()

            val gap = 14f
            val totalWidth = w - 70f
            val cardWidth =
                (totalWidth - gap * 4f) / 5f

            val top = h * 0.72f
            val bottom = top + 78f

            for (i in 0..4) {

                val left =
                    35f + i * (cardWidth + gap)

                val right =
                    left + cardWidth

                if (
                    event.x >= left &&
                    event.x <= right &&
                    event.y >= top &&
                    event.y <= bottom
                ) {
                    selectedCard = i
                    invalidate()
                    return true
                }
            }
        }

        return true
    }
}
