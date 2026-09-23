package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CarPageView(
    context: Context
) : View(context) {

    var onBackClick: (() -> Unit)? = null

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var widthPx = 0f
    private var heightPx = 0f

    private var speed = 0f
    private var rpm = 0f
    private var temperature = 90f
    private var fuel = 72f
    private var voltage = 13.8f

    init {
        isClickable = true
        isFocusable = true

        backgroundPaint.shader = LinearGradient(
            0f,
            0f,
            0f,
            1200f,
            Color.rgb(3, 7, 14),
            Color.rgb(9, 18, 31),
            Shader.TileMode.CLAMP
        )

        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeCap = Paint.Cap.ROUND

        textPaint.typeface = Typeface.create(
            Typeface.SANS_SERIF,
            Typeface.NORMAL
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        widthPx = width.toFloat()
        heightPx = height.toFloat()

        canvas.drawRect(
            0f,
            0f,
            widthPx,
            heightPx,
            backgroundPaint
        )

        drawAmbientGlow(canvas)
        drawHeader(canvas)
        drawMainGauges(canvas)
        drawVehicleCenter(canvas)
        drawStatusPanels(canvas)
        drawBottomBar(canvas)
    }

    private fun drawAmbientGlow(canvas: Canvas) {
        val glow = Paint(Paint.ANTI_ALIAS_FLAG)

        glow.shader = RadialGradient(
            widthPx * 0.50f,
            heightPx * 0.42f,
            widthPx * 0.60f,
            intArrayOf(
                Color.argb(65, 35, 130, 255),
                Color.argb(18, 35, 100, 180),
                Color.TRANSPARENT
            ),
            null,
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            widthPx * 0.50f,
            heightPx * 0.42f,
            widthPx * 0.60f,
            glow
        )
    }

    private fun drawHeader(canvas: Canvas) {
        val left = 28f
        val top = 24f
        val right = widthPx - 28f
        val bottom = 108f

        panelPaint.shader = LinearGradient(
            left,
            top,
            right,
            bottom,
            Color.argb(80, 255, 255, 255),
            Color.argb(25, 255, 255, 255),
            Shader.TileMode.CLAMP
        )

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            28f,
            28f,
            panelPaint
        )

        panelPaint.shader = null
        panelPaint.style = Paint.Style.STROKE
        panelPaint.strokeWidth = 1.5f
        panelPaint.color = Color.argb(90, 255, 255, 255)

        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            28f,
            28f,
            panelPaint
        )

        panelPaint.style = Paint.Style.FILL

        textPaint.color = Color.WHITE
        textPaint.textSize = 25f
        textPaint.typeface = Typeface.create(
            Typeface.SANS_SERIF,
            Typeface.BOLD
        )

        canvas.drawText(
            "VEHICLE",
            left + 28f,
            top + 38f,
            textPaint
        )

        textPaint.color = Color.rgb(105, 180, 255)
        textPaint.textSize = 16f
        textPaint.typeface = Typeface.DEFAULT

        canvas.drawText(
            "BMW LUXURY CONTROL",
            left + 29f,
            top + 63f,
            textPaint
        )

        drawStatusDot(
            canvas,
            right - 125f,
            top + 42f
        )

        textPaint.color = Color.WHITE
        textPaint.textSize = 14f

        canvas.drawText(
            "SYSTEM ONLINE",
            right - 105f,
            top + 47f,
            textPaint
        )
    }

    private fun drawStatusDot(
        canvas: Canvas,
        x: Float,
        y: Float
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.rgb(60, 220, 140)
        canvas.drawCircle(x, y, 6f, paint)

        paint.color = Color.argb(70, 60, 220, 140)
        canvas.drawCircle(x, y, 13f, paint)
    }

    private fun drawMainGauges(canvas: Canvas) {
        val gaugeRadius = min(widthPx * 0.27f, heightPx * 0.27f)

        val leftX = widthPx * 0.27f
        val rightX = widthPx * 0.73f
        val centerY = heightPx * 0.43f

        drawGauge(
            canvas,
            leftX,
            centerY,
            gaugeRadius,
            0f,
            240f,
            speed,
            "km/h",
            "SPEED"
        )

        drawGauge(
            canvas,
            rightX,
            centerY,
            gaugeRadius,
            0f,
            8000f,
            rpm,
            "RPM",
            "ENGINE"
        )
    }

    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        minValue: Float,
        maxValue: Float,
        value: Float,
        unit: String,
        title: String
    ) {
        val outer = Paint(Paint.ANTI_ALIAS_FLAG)
        outer.style = Paint.Style.STROKE
        outer.strokeWidth = 10f
        outer.strokeCap = Paint.Cap.ROUND
        outer.color = Color.argb(55, 150, 190, 230)

        canvas.drawArc(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius,
            135f,
            270f,
            false,
            outer
        )

        val progress = ((value - minValue) / (maxValue - minValue))
            .coerceIn(0f, 1f)

        val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = 10f
        progressPaint.strokeCap = Paint.Cap.ROUND

        progressPaint.shader = SweepGradient(
            cx,
            cy,
            intArrayOf(
                Color.rgb(45, 110, 255),
                Color.rgb(70, 190, 255),
                Color.rgb(100, 225, 255)
            ),
            floatArrayOf(0f, 0.7f, 1f)
        )

        canvas.drawArc(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius,
            135f,
            270f * progress,
            false,
            progressPaint
        )

        drawGaugeTicks(
            canvas,
            cx,
            cy,
            radius
        )

        textPaint.color = Color.argb(170, 210, 225, 245)
        textPaint.textSize = 13f
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.DEFAULT

        canvas.drawText(
            title,
            cx,
            cy - 45f,
            textPaint
        )

        textPaint.color = Color.WHITE
        textPaint.textSize = 43f
        textPaint.typeface = Typeface.create(
            Typeface.SANS_SERIF,
            Typeface.BOLD
        )

        val valueText =
            if (maxValue > 1000f) {
                value.toInt().toString()
            } else {
                value.toInt().toString()
            }

        canvas.drawText(
            valueText,
            cx,
            cy + 20f,
            textPaint
        )

        textPaint.color = Color.rgb(100, 185, 255)
        textPaint.textSize = 14f
        textPaint.typeface = Typeface.DEFAULT

        canvas.drawText(
            unit,
            cx,
            cy + 45f,
            textPaint
        )

        textPaint.textAlign = Paint.Align.LEFT
    }

    private fun drawGaugeTicks(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {
        val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        tickPaint.style = Paint.Style.STROKE
        tickPaint.strokeCap = Paint.Cap.ROUND

        for (i in 0..36) {
            val angle = Math.toRadians(
                (135.0 + i * (270.0 / 36.0))
            )

            val outerRadius = radius - 16f
            val innerRadius =
                if (i % 3 == 0) radius - 29f
                else radius - 23f

            val x1 =
                cx + cos(angle).toFloat() * innerRadius
            val y1 =
                cy + sin(angle).toFloat() * innerRadius

            val x2 =
                cx + cos(angle).toFloat() * outerRadius
            val y2 =
                cy + sin(angle).toFloat() * outerRadius

            tickPaint.strokeWidth =
                if (i % 3 == 0) 2.5f else 1.2f

            tickPaint.color =
                if (i % 3 == 0)
                    Color.argb(170, 210, 225, 245)
                else
                    Color.argb(75, 210, 225, 245)

            canvas.drawLine(
                x1,
                y1,
                x2,
                y2,
                tickPaint
            )
        }
    }

    private fun drawVehicleCenter(canvas: Canvas) {
        val cx = widthPx / 2f
        val cy = heightPx * 0.43f

        val glow = Paint(Paint.ANTI_ALIAS_FLAG)

        glow.shader = RadialGradient(
            cx,
            cy,
            100f,
            intArrayOf(
                Color.argb(80, 40, 150, 255),
                Color.argb(20, 40, 100, 200),
                Color.TRANSPARENT
            ),
            null,
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            cx,
            cy,
            100f,
            glow
        )

        val circle = Paint(Paint.ANTI_ALIAS_FLAG)
        circle.style = Paint.Style.STROKE
        circle.strokeWidth = 2f
        circle.color = Color.argb(100, 100, 190, 255)

        canvas.drawCircle(
            cx,
            cy,
            63f,
            circle
        )

        textPaint.textAlign = Paint.Align.CENTER

        textPaint.color = Color.WHITE
        textPaint.textSize = 16f
        textPaint.typeface = Typeface.create(
            Typeface.SANS_SERIF,
            Typeface.BOLD
        )

        canvas.drawText(
            "PEUGEOT PARS",
            cx,
            cy - 8f,
            textPaint
        )

        textPaint.color = Color.rgb(90, 180, 255)
        textPaint.textSize = 12f
        textPaint.typeface = Typeface.DEFAULT

        canvas.drawText(
            "XU7 • DEMO MODE",
            cx,
            cy + 15f,
            textPaint
        )

        textPaint.textAlign = Paint.Align.LEFT
    }

    private fun drawStatusPanels(canvas: Canvas) {
        val gap = 16f
        val margin = 28f
        val panelWidth = (widthPx - margin * 2f - gap * 2f) / 3f

        val y = heightPx * 0.68f
        val h = 110f

        drawInfoPanel(
            canvas,
            margin,
            y,
            panelWidth,
            h,
            "ENGINE",
            "90°C",
            "TEMPERATURE"
        )

        drawInfoPanel(
            canvas,
            margin + panelWidth + gap,
            y,
            panelWidth,
            h,
            "FUEL",
            "${fuel.toInt()}%",
            "LEVEL"
        )

        drawInfoPanel(
            canvas,
            margin + (panelWidth + gap) * 2f,
            y,
            panelWidth,
            h,
            "VOLTAGE",
            String.format("%.1f V", voltage),
            "BATTERY"
        )
    }

    private fun drawInfoPanel(
        canvas: Canvas,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        title: String,
        value: String,
        subtitle: String
    ) {
        panelPaint.shader = LinearGradient(
            x,
            y,
            x + w,
            y + h,
            Color.argb(75, 255, 255, 255),
            Color.argb(18, 255, 255, 255),
            Shader.TileMode.CLAMP
        )

        canvas.drawRoundRect(
            x,
            y,
            x + w,
            y + h,
            22f,
            22f,
            panelPaint
        )

        panelPaint.shader = null
        panelPaint.style = Paint.Style.STROKE
        panelPaint.strokeWidth = 1f
        panelPaint.color = Color.argb(60, 180, 210, 240)

        canvas.drawRoundRect(
            x,
            y,
            x + w,
            y + h,
            22f,
            22f,
            panelPaint
        )

        panelPaint.style = Paint.Style.FILL

        textPaint.color = Color.argb(170, 200, 220, 245)
        textPaint.textSize = 12f

        canvas.drawText(
            title,
            x + 18f,
            y + 27f,
            textPaint
        )

        textPaint.color = Color.WHITE
        textPaint.textSize = 23f
        textPaint.typeface = Typeface.create(
            Typeface.SANS_SERIF,
            Typeface.BOLD
        )

        canvas.drawText(
            value,
            x + 18f,
            y + 61f,
            textPaint
        )

        textPaint.color = Color.rgb(90, 175, 245)
        textPaint.textSize = 10f
        textPaint.typeface = Typeface.DEFAULT

        canvas.drawText(
            subtitle,
            x + 18f,
            y + 83f,
            textPaint
        )
    }

    private fun drawBottomBar(canvas: Canvas) {
        val y = heightPx - 65f

        strokePaint.color = Color.argb(
            55,
            180,
            210,
            240
        )

        strokePaint.strokeWidth = 1f

        canvas.drawLine(
            28f,
            y - 18f,
            widthPx - 28f,
            y - 18f,
            strokePaint
        )

        textPaint.color = Color.argb(
            160,
            190,
            215,
            240
        )

        textPaint.textSize = 13f

        canvas.drawText(
            "← BACK",
            32f,
            y + 10f,
            textPaint
        )

        textPaint.textAlign = Paint.Align.CENTER

        textPaint.color = Color.argb(
            130,
            190,
            215,
            240
        )

        canvas.drawText(
            "VEHICLE CONTROL • BMW LUXURY UI",
            widthPx / 2f,
            y + 10f,
            textPaint
        )

        textPaint.textAlign = Paint.Align.LEFT
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {

            val bottomZone = heightPx - 95f

            if (event.y >= bottomZone &&
                event.x < widthPx * 0.25f
            ) {
                onBackClick?.invoke()
                performClick()
                return true
            }

            performClick()
        }

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}

