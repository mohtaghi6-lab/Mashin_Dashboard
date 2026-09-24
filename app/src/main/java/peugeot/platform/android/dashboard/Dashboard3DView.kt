package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.vehicle.VehicleData
import peugeot.platform.android.weather.WeatherData
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Dashboard3DView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var vehicleData = VehicleData.demo()
    private var aiState = AIState.IDLE
    private var weatherData = WeatherData.demo()

    var onAIOrbClick: (() -> Unit)? = null
    var onCarClick: (() -> Unit)? = null
    var onMusicClick: (() -> Unit)? = null
    var onNavigationClick: (() -> Unit)? = null
    var onPhoneClick: (() -> Unit)? = null
    var onScanClick: (() -> Unit)? = null

    private val blue = Color.rgb(0, 170, 255)
    private val lightBlue = Color.rgb(90, 210, 255)
    private val darkBlue = Color.rgb(5, 35, 65)

    private var animatedTime = 0L

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0f || h <= 0f) return

        animatedTime = System.currentTimeMillis()

        drawBackground(canvas, w, h)
        drawTopStatus(canvas, w, h)
        drawWeatherCard(canvas, w, h)

        drawLeftGauge(
            canvas,
            w * 0.235f,
            h * 0.45f,
            minOf(w, h) * 0.205f
        )

        drawRightGauge(
            canvas,
            w * 0.765f,
            h * 0.45f,
            minOf(w, h) * 0.205f
        )

        drawCenterVehicle(canvas, w, h)

        drawDriveStatus(canvas, w, h)

        drawAIOrb(
            canvas,
            w / 2f,
            h * 0.685f
        )

        drawQuickActions(canvas, w, h)

        postInvalidateDelayed(40L)
    }

    fun setVehicleData(data: VehicleData) {
        vehicleData = data
        invalidate()
    }

    fun setAIState(state: AIState) {
        aiState = state
        invalidate()
    }

    fun setWeatherData(data: WeatherData) {
        weatherData = data
        invalidate()
    }

    // ---------------------------------------------------------
    // BACKGROUND
    // ---------------------------------------------------------

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
    intArrayOf(
        Color.rgb(1, 4, 10),
        Color.rgb(2, 13, 24),
        Color.rgb(5, 25, 42)
    ),
    floatArrayOf(
        0f,
        0.5f,
        1f
    ),
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

        // مرکز نور آبی
        val centerGlow = RadialGradient(
            w / 2f,
            h * 0.47f,
            minOf(w, h) * 0.55f,
            intArrayOf(
                Color.argb(75, 0, 170, 255),
                Color.argb(25, 0, 110, 190),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.45f, 1f),
            Shader.TileMode.CLAMP
        )

        glowPaint.shader = centerGlow

        canvas.drawCircle(
            w / 2f,
            h * 0.47f,
            minOf(w, h) * 0.55f,
            glowPaint
        )

        glowPaint.shader = null

        // نور بالای صفحه
        paint.color = Color.argb(
            30,
            0,
            190,
            255
        )

        canvas.drawCircle(
            w / 2f,
            -20f,
            w * 0.42f,
            paint
        )

        // خطوط بسیار ظریف پس‌زمینه
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(
            22,
            100,
            210,
            255
        )

        for (i in 1..7) {
            val y = h * 0.12f + i * 58f

            canvas.drawLine(
                0f,
                y,
                w,
                y,
                paint
            )
        }
    }

    // ---------------------------------------------------------
    // TOP STATUS
    // ---------------------------------------------------------

    private fun drawTopStatus(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val rect = RectF(
            32f,
            18f,
            w - 32f,
            78f
        )

        drawGlass(
            canvas,
            rect,
            28f,
            Color.argb(70, 255, 255, 255),
            Color.argb(90, 80, 200, 255)
        )

        textPaint.textAlign = Paint.Align.LEFT
        textPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        textPaint.textSize = 17f
        textPaint.color = Color.WHITE

        canvas.drawText(
            "PEUGEOT PARS",
            58f,
            46f,
            textPaint
        )

        textPaint.textSize = 10f
        textPaint.color = lightBlue

        canvas.drawText(
            "VEHICLE OS  •  BMW LUXURY",
            58f,
            64f,
            textPaint
        )

        // وضعیت سیستم
        val rightX = w - 58f

        textPaint.textAlign = Paint.Align.RIGHT
        textPaint.textSize = 11f
        textPaint.color = Color.rgb(110, 255, 170)

        canvas.drawText(
            "● SYSTEM ONLINE",
            rightX,
            44f,
            textPaint
        )

        textPaint.textSize = 10f
        textPaint.color = Color.LTGRAY

        canvas.drawText(
            "DEMO / CAN READY",
            rightX,
            62f,
            textPaint
        )
    }

    // ---------------------------------------------------------
    // WEATHER
    // ---------------------------------------------------------

    private fun drawWeatherCard(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val rect = RectF(
            w / 2f - 150f,
            92f,
            w / 2f + 150f,
            150f
        )

        drawGlass(
            canvas, rect, 24f,
            Color.argb(45, 255, 255, 255),
            Color.argb(75, 70, 190, 255)
        )

        textPaint.textAlign = Paint.Align.LEFT
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        textPaint.textSize = 20f
        textPaint.color = Color.WHITE
        canvas.drawText(weatherData.icon, rect.left + 18f, rect.top + 37f, textPaint)

        textPaint.textSize = 18f
        canvas.drawText(weatherData.temperatureC.toString() + "°", rect.left + 58f, rect.top + 27f, textPaint)

        textPaint.textSize = 9f
        textPaint.color = lightBlue
        canvas.drawText(weatherData.description, rect.left + 59f, rect.top + 43f, textPaint)

        textPaint.textAlign = Paint.Align.RIGHT
        textPaint.textSize = 9f
        textPaint.color = Color.LTGRAY
        canvas.drawText(weatherData.city, rect.right - 18f, rect.top + 22f, textPaint)
        canvas.drawText(
            "حس‌شده " + weatherData.feelsLikeC + "°  •  باد " + weatherData.windKmh + " km/h",
            rect.right - 18f, rect.top + 39f, textPaint
        )

        textPaint.textSize = 8f
        textPaint.color = Color.argb(150, 210, 230, 245)
        canvas.drawText("ONLINE WEATHER", rect.right - 18f, rect.top + 52f, textPaint)
    }

    // ---------------------------------------------------------
    // LEFT SPEED GAUGE
    // ---------------------------------------------------------

    private fun drawLeftGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {
        drawGaugeBase(
            canvas,
            cx,
            cy,
            radius,
            "km/h"
        )

        val value = vehicleData.speedKmh
            .toFloat()
            .coerceIn(0f, 240f)

        val progress = value / 240f

        drawGaugeArc(
            canvas,
            cx,
            cy,
            radius,
            progress
        )

        drawNeedle(
            canvas,
            cx,
            cy,
            radius,
            progress
        )

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        textPaint.textSize = radius * 0.34f
        textPaint.color = Color.WHITE

        canvas.drawText(
            value.toInt().toString(),
            cx,
            cy + 14f,
            textPaint
        )

        textPaint.textSize = 12f
        textPaint.color = lightBlue

        canvas.drawText(
            "km/h",
            cx,
            cy + 35f,
            textPaint
        )

        drawGaugeLabel(
            canvas,
            cx,
            cy + radius + 32f,
            "SPEED"
        )
    }

    // ---------------------------------------------------------
    // RIGHT RPM GAUGE
    // ---------------------------------------------------------

    private fun drawRightGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {
        drawGaugeBase(
            canvas,
            cx,
            cy,
            radius,
            "RPM"
        )

        val value = vehicleData.rpm
            .toFloat()
            .coerceIn(0f, 8000f)

        val progress = value / 8000f

        drawGaugeArc(
            canvas,
            cx,
            cy,
            radius,
            progress
        )

        drawNeedle(
            canvas,
            cx,
            cy,
            radius,
            progress
        )

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        textPaint.textSize = radius * 0.31f
        textPaint.color = Color.WHITE

        canvas.drawText(
            value.toInt().toString(),
            cx,
            cy + 14f,
            textPaint
        )

        textPaint.textSize = 12f
        textPaint.color = lightBlue

        canvas.drawText(
            "RPM",
            cx,
            cy + 35f,
            textPaint
        )

        drawGaugeLabel(
            canvas,
            cx,
            cy + radius + 32f,
            "ENGINE"
        )
    }

    // ---------------------------------------------------------
    // GAUGE BASE
    // ---------------------------------------------------------

    private fun drawGaugeBase(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        unit: String
    ) {
        val rect = RectF(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius
        )

        // Outer glow
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 24f
        paint.color = Color.argb(
            22,
            0,
            180,
            255
        )

        canvas.drawArc(
            rect,
            135f,
            270f,
            false,
            paint
        )

        // Outer ring
        paint.strokeWidth = 2f
        paint.color = Color.argb(
            160,
            90,
            200,
            255
        )

        canvas.drawArc(
            rect,
            135f,
            270f,
            false,
            paint
        )

        // Inner ring
        val inner = RectF(
            cx - radius + 18f,
            cy - radius + 18f,
            cx + radius - 18f,
            cy + radius - 18f
        )

        paint.strokeWidth = 1f
        paint.color = Color.argb(
            70,
            255,
            255,
            255
        )

        canvas.drawArc(
            inner,
            135f,
            270f,
            false,
            paint
        )

        // Tick marks
        for (i in 0..36) {

            val angle = Math.toRadians(
                135.0 + i * 7.5
            )

            val major = i % 3 == 0

            val innerRadius =
                if (major) radius - 26f
                else radius - 20f

            val outerRadius =
                radius - 7f

            paint.strokeWidth =
                if (major) 2.5f else 1f

            paint.color =
                if (major) Color.WHITE
                else Color.argb(
                    130,
                    180,
                    220,
                    255
                )

            canvas.drawLine(
                cx +
                    cos(angle).toFloat() *
                    innerRadius,
                cy +
                    sin(angle).toFloat() *
                    innerRadius,
                cx +
                    cos(angle).toFloat() *
                    outerRadius,
                cy +
                    sin(angle).toFloat() *
                    outerRadius,
                paint
            )
        }

        // مرکز
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(
            80,
            0,
            170,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            16f,
            paint
        )
    }

    // ---------------------------------------------------------
    // GAUGE ARC
    // ---------------------------------------------------------

    private fun drawGaugeArc(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        progress: Float
    ) {
        val rect = RectF(
            cx - radius + 4f,
            cy - radius + 4f,
            cx + radius - 4f,
            cy + radius - 4f
        )

        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND

        // Background
        paint.strokeWidth = 9f
        paint.color = Color.argb(
            55,
            0,
            160,
            255
        )

        canvas.drawArc(
            rect,
            135f,
            270f,
            false,
            paint
        )

        // Active
        paint.strokeWidth = 5f
        paint.color = blue

        canvas.drawArc(
            rect,
            135f,
            270f * progress,
            false,
            paint
        )

        // نقطه انتهایی
        val angle = Math.toRadians(
            135.0 + 270.0 * progress
        )

        val px =
            cx +
                cos(angle).toFloat() *
                (radius - 2f)

        val py =
            cy +
                sin(angle).toFloat() *
                (radius - 2f)

        paint.style = Paint.Style.FILL
        paint.color = lightBlue

        canvas.drawCircle(
            px,
            py,
            5f,
            paint
        )
    }

    // ---------------------------------------------------------
    // NEEDLE
    // ---------------------------------------------------------

    private fun drawNeedle(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        progress: Float
    ) {
        val angle = Math.toRadians(
            135.0 + 270.0 * progress
        )

        val length = radius - 40f

        val endX =
            cx +
                cos(angle).toFloat() *
                length

        val endY =
            cy +
                sin(angle).toFloat() *
                length

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = Color.WHITE

        canvas.drawLine(
            cx,
            cy,
            endX,
            endY,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.color = blue

        canvas.drawCircle(
            cx,
            cy,
            8f,
            paint
        )

        paint.color = Color.WHITE

        canvas.drawCircle(
            cx,
            cy,
            3f,
            paint
        )
    }

    private fun drawGaugeLabel(
        canvas: Canvas,
        x: Float,
        y: Float,
        label: String
    ) {
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        textPaint.textSize = 10f
        textPaint.color = Color.argb(
            190,
            180,
            220,
            255
        )

        canvas.drawText(
            label,
            x,
            y,
            textPaint
        )
    }

    // ---------------------------------------------------------
    // CENTER VEHICLE / SPEED
    // ---------------------------------------------------------

    private fun drawCenterVehicle(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val cx = w / 2f
        val cy = h * 0.42f

        // مرکز شیشه‌ای
        val panel = RectF(
            cx - 125f,
            cy - 58f,
            cx + 125f,
            cy + 55f
        )

        drawGlass(
            canvas,
            panel,
            30f,
            Color.argb(38, 255, 255, 255),
            Color.argb(55, 100, 210, 255)
        )

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        textPaint.textSize = 64f
        textPaint.color = Color.WHITE

        canvas.drawText(
            vehicleData.speedKmh.toString(),
            cx,
            cy + 18f,
            textPaint
        )

        textPaint.textSize = 11f
        textPaint.color = lightBlue

        canvas.drawText(
            "CURRENT SPEED",
            cx,
            cy + 40f,
            textPaint
        )

        // خط نوری
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(
            100,
            0,
            180,
            255
        )

        canvas.drawLine(
            cx - 75f,
            cy + 57f,
            cx + 75f,
            cy + 57f,
            paint
        )
    }

    // ---------------------------------------------------------
    // DRIVE STATUS
    // ---------------------------------------------------------

    private fun drawDriveStatus(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val cx = w / 2f
        val y = h * 0.555f

        val panel = RectF(
            cx - 155f,
            y,
            cx + 155f,
            y + 40f
        )

        drawGlass(
            canvas,
            panel,
            20f,
            Color.argb(35, 255, 255, 255),
            Color.argb(50, 90, 190, 255)
        )

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.textSize = 11f
        textPaint.color = Color.WHITE

        canvas.drawText(
            "P",
            cx - 115f,
            y + 25f,
            textPaint
        )

        textPaint.color = Color.argb(
            90,
            255,
            255,
            255
        )

        canvas.drawText(
            "R",
            cx - 58f,
            y + 25f,
            textPaint
        )

        canvas.drawText(
            "N",
            cx,
            y + 25f,
            textPaint
        )

        canvas.drawText(
            "D",
            cx + 58f,
            y + 25f,
            textPaint
        )

        textPaint.color = lightBlue

        canvas.drawText(
            "DEMO",
            cx + 115f,
            y + 25f,
            textPaint
        )
    }

    // ---------------------------------------------------------
    // AI ORB
    // ---------------------------------------------------------

    private fun drawAIOrb(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {
        val t =
            animatedTime / 1000.0

        val pulse =
            (sin(t * 2.5) * 0.5 + 0.5)
                .toFloat()

        val stateColor =
            when (aiState) {
                AIState.LISTENING -> Color.GREEN
                AIState.THINKING -> Color.YELLOW
                AIState.SPEAKING -> Color.CYAN
                AIState.ERROR -> Color.RED
                else -> blue
            }

        // Outer pulse
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.argb(
            (80 + pulse * 100).toInt(),
            Color.red(stateColor),
            Color.green(stateColor),
            Color.blue(stateColor)
        )

        canvas.drawCircle(
            cx,
            cy,
            62f + pulse * 18f,
            paint
        )

        // Glow
        val gradient = RadialGradient(
            cx,
            cy,
            70f,
            intArrayOf(
                Color.argb(180, 0, 170, 255),
                Color.argb(80, 0, 110, 220),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP
        )

        glowPaint.shader = gradient

        canvas.drawCircle(
            cx,
            cy,
            70f,
            glowPaint
        )

        glowPaint.shader = null

        // Orb
        paint.style = Paint.Style.FILL

        paint.shader = RadialGradient(
            cx - 12f,
            cy - 15f,
            65f,
            intArrayOf(
                Color.rgb(130, 230, 255),
                Color.rgb(0, 130, 245),
                Color.rgb(0, 40, 100)
            ),
            floatArrayOf(0f, 0.45f, 1f),
            Shader.TileMode.CLAMP
        )

        canvas.drawCircle(
            cx,
            cy,
            50f + pulse * 3f,
            paint
        )

        paint.shader = null

        // Reflection
        paint.color = Color.argb(
            130,
            220,
            250,
            255
        )

        canvas.drawCircle(
            cx - 17f,
            cy - 19f,
            14f,
            paint
        )

        // AI
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        textPaint.textSize = 22f
        textPaint.color = Color.WHITE

        canvas.drawText(
            "AI",
            cx,
            cy + 8f,
            textPaint
        )

        textPaint.textSize = 10f
        textPaint.color = stateColor

        canvas.drawText(
            aiText(),
            cx,
            cy + 27f,
            textPaint
        )

        // موج صوتی
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.argb(
            170,
            Color.red(stateColor),
            Color.green(stateColor),
            Color.blue(stateColor)
        )

        val waveRadius =
            75f + pulse * 25f

        canvas.drawCircle(
            cx,
            cy,
            waveRadius,
            paint
        )
    }

    private fun aiText(): String {
        return when (aiState) {
            AIState.IDLE -> "آماده‌ام"
            AIState.LISTENING -> "گوش می‌کنم"
            AIState.THINKING -> "در حال پردازش"
            AIState.SPEAKING -> "در حال صحبت"
            AIState.ERROR -> "خطا"
        }
    }

    // ---------------------------------------------------------
    // QUICK ACTIONS
    // ---------------------------------------------------------

    private fun drawQuickActions(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val labels = arrayOf(
            "CAR",
            "MUSIC",
            "NAVI",
            "PHONE",
            "SCAN"
        )

        val width = 112f
        val height = 54f
        val gap = 10f

        val total =
            labels.size * width +
                (labels.size - 1) * gap

        val start =
            (w - total) / 2f

        val top = h - 76f

        for (i in labels.indices) {

            val left =
                start + i * (width + gap)

            val rect = RectF(
                left,
                top,
                left + width,
                top + height
            )

            drawGlass(
                canvas,
                rect,
                20f,
                Color.argb(42, 255, 255, 255),
                Color.argb(80, 70, 190, 255)
            )

            // نقطه آبی
            paint.style = Paint.Style.FILL
            paint.color = blue

            canvas.drawCircle(
                left + 18f,
                top + height / 2f,
                3f,
                paint
            )

            textPaint.textAlign = Paint.Align.CENTER
            textPaint.typeface = Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )
            textPaint.textSize = 11f
            textPaint.color = Color.WHITE

            canvas.drawText(
                labels[i],
                left + width / 2f + 7f,
                top + 32f,
                textPaint
            )
        }
    }

    // ---------------------------------------------------------
    // GLASS PANEL
    // ---------------------------------------------------------

    private fun drawGlass(
        canvas: Canvas,
        rect: RectF,
        radius: Float,
        fillColor: Int,
        strokeColor: Int
    ) {
        paint.style = Paint.Style.FILL
        paint.color = fillColor

        canvas.drawRoundRect(
            rect,
            radius,
            radius,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.color = strokeColor

        canvas.drawRoundRect(
            rect,
            radius,
            radius,
            paint
        )

        // خط انعکاس بالایی
        paint.strokeWidth = 1f
        paint.color = Color.argb(
            55,
            255,
            255,
            255
        )

        canvas.drawLine(
            rect.left + radius,
            rect.top + 1f,
            rect.right - radius,
            rect.top + 1f,
            paint
        )
    }

    // ---------------------------------------------------------
    // TOUCH
    // ---------------------------------------------------------

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (event.actionMasked != MotionEvent.ACTION_UP) {
            return true
        }

        val w = width.toFloat()
        val h = height.toFloat()

        // AI Orb
        val orbX = w / 2f
        val orbY = h * 0.685f

        val dx = event.x - orbX
        val dy = event.y - orbY

        val distance =
            sqrt(dx * dx + dy * dy)

        if (distance < 100f) {
            onAIOrbClick?.invoke()
            performClick()
            return true
        }

        // Bottom buttons
        val labels = arrayOf(
            "CAR",
            "MUSIC",
            "NAVI",
            "PHONE",
            "SCAN"
        )

        val cardWidth = 112f
        val gap = 10f
        val cardTop = h - 76f
        val cardBottom = h - 22f

        val total =
            labels.size * cardWidth +
                (labels.size - 1) * gap

        val start =
            (w - total) / 2f

        if (
            event.y >= cardTop &&
            event.y <= cardBottom
        ) {

            for (i in labels.indices) {

                val left =
                    start +
                        i * (cardWidth + gap)

                val right =
                    left + cardWidth

                if (
                    event.x >= left &&
                    event.x <= right
                ) {

                    when (labels[i]) {

                        "CAR" ->
                            onCarClick?.invoke()

                        "MUSIC" ->
                            onMusicClick?.invoke()

                        "NAVI" ->
                            onNavigationClick?.invoke()

                        "PHONE" ->
                            onPhoneClick?.invoke()

                        "SCAN" ->
                            onScanClick?.invoke()
                    }

                    performClick()
                    return true
                }
            }
        }

        performClick()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
