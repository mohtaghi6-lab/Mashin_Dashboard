package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.MotionEvent

class CallPageView(context: Context) : View(context) {

    var onBackClick: (() -> Unit)? = null
    var onCallClick: (() -> Unit)? = null
    var onEndClick: (() -> Unit)? = null

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val blue = Color.rgb(70, 190, 255)
    private val green = Color.rgb(80, 220, 170)
    private val red = Color.rgb(235, 90, 95)
    private val dark = Color.rgb(3, 7, 13)

    private var connected = false
    private var inCall = false
    private var contactName = "No Device"

    private val backRect = RectF()
    private val callRect = RectF()
    private val endRect = RectF()

    init {
        isClickable = true
        isFocusable = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        drawBackground(canvas, w, h)
        drawHeader(canvas, w)
        drawPhoneOrb(canvas, w, h)
        drawStatus(canvas, w, h)
        drawControls(canvas, w, h)
        drawFooter(canvas, w, h)
    }

    private fun drawBackground(canvas: Canvas, w: Float, h: Float) {
        canvas.drawRect(
            0f, 0f, w, h,
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                shader = LinearGradient(
                    0f, 0f, 0f, h,
                    Color.rgb(3, 7, 13),
                    Color.rgb(7, 20, 34),
                    Shader.TileMode.CLAMP
                )
            }
        )

        val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                w * 0.5f,
                h * 0.39f,
                w * 0.45f,
                intArrayOf(
                    Color.argb(70, 35, 125, 210),
                    Color.argb(20, 35, 100, 170),
                    Color.TRANSPARENT
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }

        canvas.drawCircle(
            w * 0.5f,
            h * 0.39f,
            w * 0.45f,
            glow
        )
    }

    private fun drawHeader(canvas: Canvas, w: Float) {
        drawGlassPanel(
            canvas,
            28f,
            22f,
            w - 28f,
            108f,
            28f
        )

        backRect.set(
            38f, 34f, 105f, 96f
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 25f
        paint.color = Color.WHITE

        canvas.drawText(
            "PHONE",
            82f,
            61f,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f
        paint.color = Color.rgb(105, 180, 225)

        canvas.drawText(
            "BMW LUXURY CALL SYSTEM",
            82f,
            84f,
            paint
        )

        paint.textSize = 30f
        paint.color = Color.WHITE

        canvas.drawText(
            "‹",
            50f,
            73f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 12f
        paint.color = if (connected) green else Color.rgb(150, 165, 175)

        canvas.drawText(
            if (connected) "BLUETOOTH CONNECTED" else "BLUETOOTH OFFLINE",
            w - 52f,
            64f,
            paint
        )

        paint.textSize = 11f
        paint.color = Color.rgb(90, 145, 175)

        canvas.drawText(
            "CAR MICROPHONE • READY",
            w - 52f,
            84f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawPhoneOrb(canvas: Canvas, w: Float, h: Float) {
        val cx = w * 0.5f
        val cy = h * 0.38f
        val radius = minOf(w * 0.18f, 105f)

        val glow = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = RadialGradient(
                cx, cy, radius * 1.8f,
                intArrayOf(
                    Color.argb(80, 55, 175, 255),
                    Color.argb(25, 55, 130, 220),
                    Color.TRANSPARENT
                ),
                null,
                Shader.TileMode.CLAMP
            )
        }

        canvas.drawCircle(cx, cy, radius * 1.8f, glow)

        paint.style = Paint.Style.FILL
        paint.color = Color.argb(180, 8, 32, 52)
        canvas.drawCircle(cx, cy, radius, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        paint.color = if (inCall) green else blue
        canvas.drawCircle(cx, cy, radius, paint)

        paint.strokeWidth = 1.5f
        paint.color = Color.argb(100, 160, 220, 255)
        canvas.drawCircle(cx, cy, radius + 16f, paint)

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 54f
        paint.color = Color.WHITE

        canvas.drawText(
            "☎",
            cx,
            cy + 19f,
            paint
        )

        paint.textSize = 12f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(120, 200, 255)

        canvas.drawText(
            if (inCall) "ACTIVE CALL" else "PHONE LINK",
            cx,
            cy + radius + 34f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawStatus(canvas: Canvas, w: Float, h: Float) {
        val y = h * 0.59f

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 24f
        paint.color = Color.WHITE

        canvas.drawText(
            if (connected) contactName else "No Device",
            w * 0.5f,
            y,
            paint
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 13f
        paint.color = if (inCall) green else Color.rgb(105, 170, 205)

        canvas.drawText(
            when {
                inCall -> "CALL IN PROGRESS"
                connected -> "READY FOR BLUETOOTH CALL"
                else -> "CONNECT A PHONE TO START"
            },
            w * 0.5f,
            y + 27f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawControls(canvas: Canvas, w: Float, h: Float) {
        val y = h * 0.75f
        val callX = w * 0.34f
        val endX = w * 0.66f

        callRect.set(
            callX - 82f, y - 32f,
            callX + 82f, y + 32f
        )

        endRect.set(
            endX - 82f, y - 32f,
            endX + 82f, y + 32f
        )

        drawButton(
            canvas,
            callRect,
            if (inCall) "ANSWERED" else "CALL",
            green
        )

        drawButton(
            canvas,
            endRect,
            "END",
            red
        )
    }

    private fun drawButton(
        canvas: Canvas,
        rect: RectF,
        label: String,
        accent: Int
    ) {
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(145, 12, 28, 43)

        canvas.drawRoundRect(
            rect,
            22f,
            22f,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.8f
        paint.color = accent

        canvas.drawRoundRect(
            rect,
            22f,
            22f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 14f
        paint.color = Color.WHITE

        canvas.drawText(
            label,
            rect.centerX(),
            rect.centerY() + 5f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawFooter(canvas: Canvas, w: Float, h: Float) {
        drawGlassPanel(
            canvas,
            28f,
            h - 74f,
            w - 28f,
            h - 20f,
            20f
        )

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 11f
        paint.color = Color.argb(210, 185, 215, 235)

        canvas.drawText(
            "‹  BACK TO BMW HOME",
            50f,
            h - 40f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.color = if (connected) green else Color.rgb(105, 150, 175)

        canvas.drawText(
            if (connected) "LINK ACTIVE" else "LINK STANDBY",
            w - 50f,
            h - 40f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
    }

    private fun drawGlassPanel(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        radius: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(115, 16, 31, 48)

        canvas.drawRoundRect(
            left, top, right, bottom,
            radius, radius, paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.3f
        paint.color = Color.argb(95, 100, 180, 255)

        canvas.drawRoundRect(
            left, top, right, bottom,
            radius, radius, paint
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_UP) {
            return true
        }

        val x = event.x
        val y = event.y

        when {
            backRect.contains(x, y) -> {
                onBackClick?.invoke()
            }

            callRect.contains(x, y) -> {
                connected = true
                inCall = true
                if (contactName == "No Device") {
                    contactName = "Bluetooth Phone"
                }
                onCallClick?.invoke()
                invalidate()
            }

            endRect.contains(x, y) -> {
                inCall = false
                onEndClick?.invoke()
                invalidate()
            }

            y >= height - 90f -> {
                onBackClick?.invoke()
            }
        }

        return true
    }

    fun setBluetoothState(
        state: Boolean,
        name: String
    ) {
        connected = state
        contactName = name
        if (!state) {
            inCall = false
        }
        invalidate()
    }
}
