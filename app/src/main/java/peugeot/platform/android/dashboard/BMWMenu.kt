package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class BMWMenu(context: Context) : View(context) {

    var onMenuClick: ((String) -> Unit)? = null

    var onSwipeRight: (() -> Unit)? = null
    var onSwipeLeft: (() -> Unit)? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var downX = 0f
    private var downY = 0f

    private val swipeLimit = 120f

    private val items = arrayOf(
        "CAR",
        "MUSIC",
        "NAVI",
        "PHONE",
        "SCAN",
        "AI"
    )

    private var selectedIndex = 0

    private var animationStart = System.currentTimeMillis()

    private val blue = Color.rgb(0, 175, 255)
    private val lightBlue = Color.rgb(110, 220, 255)

    init {
        isClickable = true
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0f || h <= 0f) {
            return
        }

        val elapsed =
            System.currentTimeMillis() - animationStart

        val pulse =
            ((sin(elapsed / 700.0) + 1.0) * 0.5)
                .toFloat()

        drawBackground(
            canvas,
            w,
            h,
            pulse
        )

        drawTopStatus(
            canvas,
            w,
            h
        )

        drawOrbitRing(
            canvas,
            w,
            h,
            pulse
        )

        drawCenterSelector(
            canvas,
            w,
            h,
            pulse
        )

        drawMenuItems(
            canvas,
            w,
            h,
            pulse
        )

        drawBottomHint(
            canvas,
            w,
            h
        )

        postInvalidateDelayed(40L)
    }

    // ---------------------------------------------------------
    // BACKGROUND
    // ---------------------------------------------------------

    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float,
        pulse: Float
    ) {
        paint.style = Paint.Style.FILL

        paint.shader = LinearGradient(
            0f,
            0f,
            0f,
            h,
            intArrayOf(
                Color.rgb(1, 4, 10),
                Color.rgb(2, 15, 28),
                Color.rgb(4, 25, 42)
            ),
            floatArrayOf(
                0f,
                0.55f,
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

        val cx = w / 2f
        val cy = h * 0.50f

        val glow = RadialGradient(
            cx,
            cy,
            minOf(w, h) * 0.48f,
            intArrayOf(
                Color.argb(
                    (65 + pulse * 25f).toInt(),
                    0,
                    160,
                    255
                ),
                Color.argb(
                    25,
                    0,
                    110,
                    190
                ),
                Color.TRANSPARENT
            ),
            floatArrayOf(
                0f,
                0.48f,
                1f
            ),
            Shader.TileMode.CLAMP
        )

        glowPaint.shader = glow

        canvas.drawCircle(
            cx,
            cy,
            minOf(w, h) * 0.48f,
            glowPaint
        )

        glowPaint.shader = null

        // خطوط ظریف محیطی
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(
            22,
            100,
            200,
            255
        )

        for (i in 1..8) {
            val y =
                h * 0.12f +
                    i * 58f

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
            30f,
            18f,
            w - 30f,
            76f
        )

        drawGlassPanel(
            canvas,
            rect,
            28f,
            Color.argb(
                45,
                255,
                255,
                255
            ),
            Color.argb(
                95,
                60,
                195,
                255
            )
        )

        textPaint.textAlign = Paint.Align.LEFT
        textPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        textPaint.textSize = 15f
        textPaint.color = Color.WHITE

        canvas.drawText(
            "BMW LUXURY",
            55f,
            43f,
            textPaint
        )

        textPaint.textSize = 9f
        textPaint.color = lightBlue

        canvas.drawText(
            "iDRIVE • VEHICLE OS",
            55f,
            61f,
            textPaint
        )

        textPaint.textAlign = Paint.Align.RIGHT
        textPaint.textSize = 11f
        textPaint.color = Color.rgb(
            100,
            255,
            170
        )

        canvas.drawText(
            "● ONLINE",
            w - 55f,
            43f,
            textPaint
        )

        textPaint.textSize = 9f
        textPaint.color = Color.argb(
            180,
            200,
            225,
            240
        )

        canvas.drawText(
            "DEMO / CAN READY",
            w - 55f,
            61f,
            textPaint
        )
    }

    // ---------------------------------------------------------
    // ORBIT RING
    // ---------------------------------------------------------

    private fun drawOrbitRing(
        canvas: Canvas,
        w: Float,
        h: Float,
        pulse: Float
    ) {
        val cx = w / 2f
        val cy = h * 0.50f

        val radius =
            minOf(w, h) * 0.285f

        paint.style = Paint.Style.STROKE

        paint.strokeWidth = 1f
        paint.color = Color.argb(
            60,
            90,
            200,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )

        paint.strokeWidth = 2f
        paint.color = Color.argb(
            (75 + pulse * 60f).toInt(),
            0,
            180,
            255
        )

        canvas.drawArc(
            RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
            ),
            -70f,
            105f,
            false,
            paint
        )

        paint.strokeWidth = 1f
        paint.color = Color.argb(
            35,
            255,
            255,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            radius + 28f,
            paint
        )
    }

    // ---------------------------------------------------------
    // CENTER SELECTOR
    // ---------------------------------------------------------

    private fun drawCenterSelector(
        canvas: Canvas,
        w: Float,
        h: Float,
        pulse: Float
    ) {
        val cx = w / 2f
        val cy = h * 0.50f

        val radius =
            minOf(w, h) * 0.105f

        // Outer glow
        val glow = RadialGradient(
            cx,
            cy,
            radius * 2.1f,
            intArrayOf(
                Color.argb(
                    (120 + pulse * 70f).toInt(),
                    0,
                    160,
                    255
                ),
                Color.argb(
                    35,
                    0,
                    110,
                    210
                ),
                Color.TRANSPARENT
            ),
            floatArrayOf(
                0f,
                0.45f,
                1f
            ),
            Shader.TileMode.CLAMP
        )

        glowPaint.shader = glow

        canvas.drawCircle(
            cx,
            cy,
            radius * 2.1f,
            glowPaint
        )

        glowPaint.shader = null

        // Outer circle
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.argb(
            150,
            70,
            205,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            radius * 1.55f,
            paint
        )

        // Rotating light arc
        val rotation =
            ((System.currentTimeMillis() - animationStart) / 8L)
                .toFloat() % 360f

        paint.strokeWidth = 5f
        paint.color = Color.CYAN

        canvas.drawArc(
            RectF(
                cx - radius * 1.45f,
                cy - radius * 1.45f,
                cx + radius * 1.45f,
                cy + radius * 1.45f
            ),
            rotation,
            95f,
            false,
            paint
        )

        // Center glass
        val centerRect = RectF(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius
        )

        drawGlassPanel(
            canvas,
            centerRect,
            radius,
            Color.argb(
                100,
                4,
                28,
                50
            ),
            Color.argb(
                180,
                100,
                220,
                255
            )
        )

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        textPaint.textSize = 21f
        textPaint.color = Color.WHITE

        canvas.drawText(
            items[selectedIndex],
            cx,
            cy + 5f,
            textPaint
        )

        textPaint.textSize = 9f
        textPaint.color = lightBlue

        canvas.drawText(
            "SELECT",
            cx,
            cy + 25f,
            textPaint
        )
    }

    // ---------------------------------------------------------
    // MENU ITEMS
    // ---------------------------------------------------------

    private fun drawMenuItems(
        canvas: Canvas,
        w: Float,
        h: Float,
        pulse: Float
    ) {
        val cx = w / 2f
        val cy = h * 0.50f

        val orbitRadius =
            minOf(w, h) * 0.285f

        val step =
            360f / items.size.toFloat()

        for (i in items.indices) {

            if (i == selectedIndex) {
                continue
            }

            val angle =
                Math.toRadians(
                    (-90f + i * step).toDouble()
                )

            val x =
                cx +
                    cos(angle).toFloat() *
                    orbitRadius

            val y =
                cy +
                    sin(angle).toFloat() *
                    orbitRadius

            drawMenuItem(
                canvas,
                x,
                y,
                items[i]
            )
        }

        // Selected item indicator
        val selectedAngle =
            Math.toRadians(
                (-90f + selectedIndex * step).toDouble()
            )

        val selectedX =
            cx +
                cos(selectedAngle).toFloat() *
                orbitRadius

        val selectedY =
            cy +
                sin(selectedAngle).toFloat() *
                orbitRadius

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.argb(
            (90 + pulse * 90f).toInt(),
            0,
            210,
            255
        )

        canvas.drawCircle(
            selectedX,
            selectedY,
            48f + pulse * 4f,
            paint
        )
    }

    private fun drawMenuItem(
        canvas: Canvas,
        x: Float,
        y: Float,
        label: String
    ) {
        val radius = 43f

        val gradient = RadialGradient(
            x - 10f,
            y - 12f,
            radius * 1.4f,
            intArrayOf(
                Color.argb(
                    100,
                    60,
                    180,
                    255
                ),
                Color.argb(
                    45,
                    255,
                    255,
                    255
                ),
                Color.argb(
                    25,
                    0,
                    80,
                    130
                )
            ),
            floatArrayOf(
                0f,
                0.5f,
                1f
            ),
            Shader.TileMode.CLAMP
        )

        paint.style = Paint.Style.FILL
        paint.shader = gradient

        canvas.drawCircle(
            x,
            y,
            radius,
            paint
        )

        paint.shader = null

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.color = Color.argb(
            145,
            80,
            205,
            255
        )

        canvas.drawCircle(
            x,
            y,
            radius,
            paint
        )

        // مرکز نقطه نور
        paint.style = Paint.Style.FILL
        paint.color = blue

        canvas.drawCircle(
            x,
            y - 18f,
            3f,
            paint
        )

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.create(
            Typeface.DEFAULT,
            Typeface.BOLD
        )
        textPaint.textSize = 10f
        textPaint.color = Color.WHITE

        canvas.drawText(
            label,
            x,
            y + 5f,
            textPaint
        )
    }

    // ---------------------------------------------------------
    // BOTTOM HINT
    // ---------------------------------------------------------

    private fun drawBottomHint(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val rect = RectF(
            w / 2f - 125f,
            h - 70f,
            w / 2f + 125f,
            h - 25f
        )

        drawGlassPanel(
            canvas,
            rect,
            22f,
            Color.argb(
                30,
                255,
                255,
                255
            ),
            Color.argb(
                55,
                80,
                190,
                255
            )
        )

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.textSize = 11f
        textPaint.color = Color.WHITE

        canvas.drawText(
            "←  SWIPE  →",
            w / 2f,
            h - 48f,
            textPaint
        )

        textPaint.textSize = 8f
        textPaint.color = Color.argb(
            150,
            180,
            215,
            235
        )

        canvas.drawText(
            "BMW iDRIVE CONTROL",
            w / 2f,
            h - 33f,
            textPaint
        )
    }

    // ---------------------------------------------------------
    // GLASS
    // ---------------------------------------------------------

    private fun drawGlassPanel(
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
        paint.strokeWidth = 1.4f
        paint.color = strokeColor

        canvas.drawRoundRect(
            rect,
            radius,
            radius,
            paint
        )

        paint.strokeWidth = 1f
        paint.color = Color.argb(
            50,
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

        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                return true
            }

            MotionEvent.ACTION_UP -> {

                val diffX =
                    event.x - downX

                val diffY =
                    event.y - downY

                // Swipe
                if (
                    abs(diffX) > swipeLimit &&
                    abs(diffX) > abs(diffY)
                ) {

                    if (diffX > 0f) {
                        onSwipeRight?.invoke()
                    } else {
                        onSwipeLeft?.invoke()
                    }

                    performClick()
                    return true
                }

                // Menu touch
                handleMenuTouch(
                    event.x,
                    event.y
                )

                performClick()
                return true
            }
        }

        return true
    }

    private fun handleMenuTouch(
        x: Float,
        y: Float
    ) {
        val w = width.toFloat()
        val h = height.toFloat()

        val cx = w / 2f
        val cy = h * 0.50f

        val dx = x - cx
        val dy = y - cy

        val distance =
            sqrt(
                dx * dx +
                    dy * dy
            )

        // مرکز
        if (distance < 105f) {

            onMenuClick?.invoke("AI")

            return
        }

        val orbitRadius =
            minOf(w, h) * 0.285f

        if (
            distance <
            orbitRadius + 65f &&
            distance >
            orbitRadius - 65f
        ) {

            var angle =
                Math.toDegrees(
                    atan2(
                        dy.toDouble(),
                        dx.toDouble()
                    )
                )

            angle += 90.0

            if (angle < 0.0) {
                angle += 360.0
            }

            val sector =
                (
                    angle /
                        (360.0 / items.size)
                    ).toInt()
                    .coerceIn(
                        0,
                        items.size - 1
                    )

            selectedIndex = sector

            animationStart =
                System.currentTimeMillis()

            invalidate()

            onMenuClick?.invoke(
                items[selectedIndex]
            )
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}

