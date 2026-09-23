package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class BMWMenu(context: Context) : View(context) {

    var onMenuClick: ((String) -> Unit)? = null

    var onSwipeRight: (() -> Unit)? = null
    var onSwipeLeft: (() -> Unit)? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

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

    init {
        isClickable = true
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0f || h <= 0f) return

        drawTopStatus(canvas, w, h)
        drawCenterSelector(canvas, w, h)
        drawMenuItems(canvas, w, h)
        drawBottomHint(canvas, w, h)

        postInvalidateDelayed(40L)
    }

    private fun drawTopStatus(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.style = Paint.Style.FILL

        paint.shader = LinearGradient(
            0f,
            0f,
            w,
            0f,
            Color.argb(0, 0, 180, 255),
            Color.argb(90, 0, 180, 255),
            Shader.TileMode.CLAMP
        )

        canvas.drawRect(
            0f,
            0f,
            w,
            4f,
            paint
        )

        paint.shader = null

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 15f
        paint.color = Color.WHITE

        canvas.drawText(
            "BMW LUXURY INTERFACE",
            35f,
            38f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 12f
        paint.color = Color.CYAN

        canvas.drawText(
            "VEHICLE OS",
            w - 35f,
            38f,
            paint
        )
    }

    private fun drawCenterSelector(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val cx = w / 2f
        val cy = h * 0.50f

        val radius = minOf(w, h) * 0.105f

        paint.style = Paint.Style.FILL
        paint.color = Color.argb(35, 0, 180, 255)

        canvas.drawCircle(
            cx,
            cy,
            radius * 1.55f,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.argb(120, 0, 200, 255)

        canvas.drawCircle(
            cx,
            cy,
            radius * 1.35f,
            paint
        )

        paint.strokeWidth = 5f
        paint.color = Color.CYAN

        canvas.drawArc(
            RectF(
                cx - radius * 1.45f,
                cy - radius * 1.45f,
                cx + radius * 1.45f,
                cy + radius * 1.45f
            ),
            -70f,
            140f,
            false,
            paint
        )

        paint.style = Paint.Style.FILL

        paint.setShadowLayer(
            30f,
            0f,
            0f,
            Color.argb(180, 0, 170, 255)
        )

        paint.color = Color.rgb(5, 25, 45)

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )

        paint.clearShadowLayer()

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.color = Color.WHITE

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )

        paint.style = Paint.Style.FILL

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 22f
        paint.color = Color.WHITE

        canvas.drawText(
            items[selectedIndex],
            cx,
            cy + 8f,
            paint
        )

        paint.textSize = 10f
        paint.color = Color.CYAN

        canvas.drawText(
            "SELECT",
            cx,
            cy + 30f,
            paint
        )
    }

    private fun drawMenuItems(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val cx = w / 2f
        val cy = h * 0.50f

        val orbitRadius = minOf(w, h) * 0.27f

        for (i in items.indices) {

            if (i == selectedIndex) continue

            val angle =
                Math.toRadians(
                    (-90.0 + i * (360.0 / items.size))
                )

            val x =
                cx +
                    kotlin.math.cos(angle).toFloat() *
                    orbitRadius

            val y =
                cy +
                    kotlin.math.sin(angle).toFloat() *
                    orbitRadius

            val itemRadius = 42f

            paint.style = Paint.Style.FILL
            paint.color = Color.argb(
                70,
                255,
                255,
                255
            )

            canvas.drawCircle(
                x,
                y,
                itemRadius,
                paint
            )

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1.5f
            paint.color = Color.argb(
                150,
                0,
                200,
                255
            )

            canvas.drawCircle(
                x,
                y,
                itemRadius,
                paint
            )

            paint.style = Paint.Style.FILL
            paint.textAlign = Paint.Align.CENTER
            paint.typeface = Typeface.DEFAULT_BOLD
            paint.textSize = 11f
            paint.color = Color.WHITE

            canvas.drawText(
                items[i],
                x,
                y + 4f,
                paint
            )
        }
    }

    private fun drawBottomHint(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f
        paint.color = Color.argb(
            190,
            200,
            230,
            255
        )

        canvas.drawText(
            "←  SWIPE  →",
            w / 2f,
            h - 35f,
            paint
        )

        paint.textSize = 9f
        paint.color = Color.argb(
            130,
            180,
            210,
            230
        )

        canvas.drawText(
            "BMW iDRIVE CONTROL",
            w / 2f,
            h - 18f,
            paint
        )
    }

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

                /*
                 * اول Swipe را بررسی می‌کنیم.
                 */

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

                /*
                 * اگر Swipe نبود،
                 * بررسی لمس منو انجام می‌شود.
                 */

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
            kotlin.math.sqrt(
                dx * dx + dy * dy
            )

        /*
         * لمس مرکز = AI
         */

        if (distance < 100f) {

            onMenuClick?.invoke("AI")

            return
        }

        val orbitRadius =
            minOf(w, h) * 0.27f

        if (
            distance < orbitRadius + 60f &&
            distance > orbitRadius - 60f
        ) {

            var angle =
                Math.toDegrees(
                    kotlin.math.atan2(
                        dy.toDouble(),
                        dx.toDouble()
                    )
                )

            angle += 90.0

            if (angle < 0.0) {
                angle += 360.0
            }

            val sector =
                (angle / (360.0 / items.size))
                    .toInt()
                    .coerceIn(
                        0,
                        items.size - 1
                    )

            selectedIndex = sector

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
