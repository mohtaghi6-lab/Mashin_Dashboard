package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

class ClockPageView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val backgroundTop = Color.rgb(1, 6, 15)
    private val backgroundBottom = Color.rgb(5, 24, 42)
    private val blue = Color.rgb(0, 180, 255)
    private val cyan = Color.rgb(80, 220, 255)

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0f || h <= 0f) return

        drawBackground(canvas, w, h)
        drawHeader(canvas, w)
        drawClock(canvas, w / 2f, h * 0.47f)
        drawDate(canvas, w, h)
        drawVehicleStatus(canvas, w, h)
        drawBottomHint(canvas, w, h)

        postInvalidateDelayed(500L)
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
            backgroundTop,
            backgroundBottom,
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

        paint.color = Color.argb(
            45,
            0,
            180,
            255
        )

        canvas.drawCircle(
            w / 2f,
            h * 0.42f,
            minOf(w, h) * 0.42f,
            paint
        )

        paint.color = Color.argb(
            25,
            80,
            220,
            255
        )

        canvas.drawCircle(
            w / 2f,
            h * 0.05f,
            w * 0.35f,
            paint
        )
    }

    private fun drawHeader(
        canvas: Canvas,
        w: Float
    ) {
        val rect = RectF(
            35f,
            25f,
            w - 35f,
            85f
        )

        paint.style = Paint.Style.FILL
        paint.color = Color.argb(
            65,
            255,
            255,
            255
        )

        canvas.drawRoundRect(
            rect,
            30f,
            30f,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.color = Color.argb(
            150,
            0,
            190,
            255
        )

        canvas.drawRoundRect(
            rect,
            30f,
            30f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 14f
        paint.color = Color.WHITE

        canvas.drawText(
            "BMW LUXURY INTERFACE",
            w / 2f,
            51f,
            paint
        )

        paint.textSize = 9f
        paint.color = cyan

        canvas.drawText(
            "CLOCK • VEHICLE OS",
            w / 2f,
            70f,
            paint
        )
    }

    private fun drawClock(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {
        val radius = minOf(width, height) * 0.30f

        /*
         * Outer ambient glow
         */

        paint.style = Paint.Style.FILL

        paint.setShadowLayer(
            45f,
            0f,
            0f,
            Color.argb(
                150,
                0,
                170,
                255
            )
        )

        paint.color = Color.argb(
            35,
            0,
            160,
            240
        )

        canvas.drawCircle(
            cx,
            cy,
            radius * 1.08f,
            paint
        )

        paint.clearShadowLayer()

        /*
         * Main glass circle
         */

        paint.color = Color.argb(
            55,
            255,
            255,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )

        /*
         * Outer ring
         */

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.color = Color.argb(
            220,
            0,
            190,
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
            100,
            255,
            255,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            radius * 0.93f,
            paint
        )

        /*
         * Tick marks
         */

        for (i in 0 until 60) {

            val angle =
                Math.toRadians(
                    i * 6.0 - 90.0
                )

            val outer =
                radius * 0.88f

            val inner =
                if (i % 5 == 0) {
                    radius * 0.78f
                } else {
                    radius * 0.83f
                }

            paint.strokeWidth =
                if (i % 5 == 0) 3f else 1f

            paint.color =
                if (i % 5 == 0) {
                    Color.WHITE
                } else {
                    Color.argb(
                        120,
                        180,
                        220,
                        240
                    )
                }

            canvas.drawLine(
                cx + cos(angle).toFloat() * inner,
                cy + sin(angle).toFloat() * inner,
                cx + cos(angle).toFloat() * outer,
                cy + sin(angle).toFloat() * outer,
                paint
            )
        }

        /*
         * Numbers
         */

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = radius * 0.13f
        paint.color = Color.WHITE

        val numbers = arrayOf(
            "12",
            "3",
            "6",
            "9"
        )

        val angles = arrayOf(
            -90.0,
            0.0,
            90.0,
            180.0
        )

        for (i in numbers.indices) {

            val angle =
                Math.toRadians(
                    angles[i]
                )

            val distance =
                radius * 0.69f

            val x =
                cx +
                    cos(angle).toFloat() *
                    distance

            val y =
                cy +
                    sin(angle).toFloat() *
                    distance +
                    paint.textSize * 0.35f

            canvas.drawText(
                numbers[i],
                x,
                y,
                paint
            )
        }

        /*
         * Current time
         */

        val now = Date()

        val time =
            SimpleDateFormat(
                "HH:mm:ss",
                Locale.getDefault()
            ).format(now)

        paint.textSize = radius * 0.20f
        paint.color = Color.WHITE

        canvas.drawText(
            time,
            cx,
            cy + radius * 0.10f,
            paint
        )

        /*
         * Date
         */

        val date =
            SimpleDateFormat(
                "EEEE • dd MMMM",
                Locale.getDefault()
            ).format(now)

        paint.textSize = radius * 0.065f
        paint.color = cyan

        canvas.drawText(
            date,
            cx,
            cy + radius * 0.23f,
            paint
        )

        /*
         * Center hub
         */

        paint.color = blue

        canvas.drawCircle(
            cx,
            cy,
            7f,
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

    private fun drawDate(
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
            210,
            235,
            250
        )

        val date =
            SimpleDateFormat(
                "yyyy/MM/dd",
                Locale.getDefault()
            ).format(Date())

        canvas.drawText(
            date,
            w / 2f,
            h * 0.78f,
            paint
        )
    }

    private fun drawVehicleStatus(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val y = h * 0.84f

        val left =
            RectF(
                40f,
                y,
                w * 0.46f,
                y + 52f
            )

        val right =
            RectF(
                w * 0.54f,
                y,
                w - 40f,
                y + 52f
            )

        drawStatusPanel(
            canvas,
            left,
            "VEHICLE",
            "READY"
        )

        drawStatusPanel(
            canvas,
            right,
            "AI ASSISTANT",
            "ONLINE"
        )
    }

    private fun drawStatusPanel(
        canvas: Canvas,
        rect: RectF,
        title: String,
        value: String
    ) {
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(
            55,
            255,
            255,
            255
        )

        canvas.drawRoundRect(
            rect,
            20f,
            20f,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.argb(
            120,
            0,
            190,
            255
        )

        canvas.drawRoundRect(
            rect,
            20f,
            20f,
            paint
        )

        val centerX =
            rect.centerX()

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 10f
        paint.color = Color.argb(
            160,
            200,
            225,
            240
        )

        canvas.drawText(
            title,
            centerX,
            rect.top + 20f,
            paint
        )

        paint.textSize = 14f
        paint.color = Color.CYAN

        canvas.drawText(
            value,
            centerX,
            rect.top + 40f,
            paint
        )
    }

    private fun drawBottomHint(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 11f
        paint.color = Color.argb(
            160,
            190,
            220,
            240
        )

        canvas.drawText(
            "← SWIPE TO RETURN",
            w / 2f,
            h - 22f,
            paint
        )
    }
}
