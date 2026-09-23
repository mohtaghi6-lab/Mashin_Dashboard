package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

import peugeot.platform.android.ai.AIState
import peugeot.platform.android.vehicle.VehicleData

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class Dashboard3DView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var vehicleData = VehicleData.demo()
    private var aiState = AIState.IDLE

    var onAIOrbClick: (() -> Unit)? = null

    var onCarClick: (() -> Unit)? = null
    var onMusicClick: (() -> Unit)? = null
    var onNavigationClick: (() -> Unit)? = null
    var onPhoneClick: (() -> Unit)? = null
    var onScanClick: (() -> Unit)? = null

    private val blue = Color.rgb(0, 170, 255)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        if (w <= 0f || h <= 0f) {
            return
        }

        drawBackground(canvas, w, h)
        drawHeader(canvas, w)

        drawGauge(
            canvas,
            w * 0.25f,
            h * 0.48f,
            minOf(w, h) * 0.20f,
            vehicleData.speedKmh.toFloat(),
            240f,
            "KM/H"
        )

        drawGauge(
            canvas,
            w * 0.75f,
            h * 0.48f,
            minOf(w, h) * 0.20f,
            vehicleData.rpm.toFloat(),
            8000f,
            "RPM"
        )

        drawCenterSpeed(
            canvas,
            w / 2f,
            h * 0.42f
        )

        drawAIWave(
            canvas,
            w / 2f,
            h * 0.70f
        )

        drawAIOrb(
            canvas,
            w / 2f,
            h * 0.70f
        )

        drawCards(
            canvas,
            w,
            h
        )

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
            Color.rgb(1, 5, 12),
            Color.rgb(8, 28, 48),
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
            40,
            0,
            160,
            255
        )

        canvas.drawCircle(
            w / 2f,
            h * 0.45f,
            minOf(w, h) * 0.45f,
            paint
        )

        paint.color = Color.argb(
            30,
            0,
            220,
            255
        )

        canvas.drawCircle(
            w / 2f,
            0f,
            w * 0.40f,
            paint
        )
    }

    private fun drawHeader(
        canvas: Canvas,
        w: Float
    ) {
        val rect = RectF(
            35f,
            20f,
            w - 35f,
            90f
        )

        paint.style = Paint.Style.FILL
        paint.color = Color.argb(
            90,
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
        paint.strokeWidth = 2f
        paint.color = Color.argb(
            180,
            0,
            180,
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
        paint.textSize = 24f
        paint.color = Color.WHITE

        canvas.drawText(
            "PEUGEOT  •  VEHICLE OS",
            w / 2f,
            62f,
            paint
        )

        paint.textSize = 11f
        paint.color = Color.CYAN

        canvas.drawText(
            "BMW LUXURY INTERFACE",
            w / 2f,
            82f,
            paint
        )
    }

    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        maxValue: Float,
        unit: String
    ) {
        val progress =
            (value / maxValue)
                .coerceIn(0f, 1f)

        val rect = RectF(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius
        )

        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 28f
        paint.color = Color.argb(
            45,
            0,
            170,
            255
        )

        canvas.drawArc(
            rect,
            135f,
            270f,
            false,
            paint
        )

        paint.strokeWidth = 4f
        paint.color = Color.CYAN

        canvas.drawArc(
            rect,
            135f,
            270f,
            false,
            paint
        )

        paint.strokeWidth = 10f
        paint.color = blue

        canvas.drawArc(
            rect,
            135f,
            270f * progress,
            false,
            paint
        )

        paint.strokeWidth = 2f
        paint.color = Color.WHITE

        for (i in 0..36) {

            val angle =
                Math.toRadians(
                    135.0 + i * 7.5
                )

            val inner =
                radius - 18f

            val outer =
                radius + 5f

            canvas.drawLine(
                cx +
                    cos(angle).toFloat() *
                    inner,

                cy +
                    sin(angle).toFloat() *
                    inner,

                cx +
                    cos(angle).toFloat() *
                    outer,

                cy +
                    sin(angle).toFloat() *
                    outer,

                paint
            )
        }

        val needleAngle =
            Math.toRadians(
                135.0 +
                    270.0 * progress
            )

        paint.strokeWidth = 5f
        paint.color = Color.WHITE

        canvas.drawLine(
            cx,
            cy,
            cx +
                cos(needleAngle).toFloat() *
                (radius - 35f),

            cy +
                sin(needleAngle).toFloat() *
                (radius - 35f),

            paint
        )

        paint.style = Paint.Style.FILL
        paint.color = blue

        canvas.drawCircle(
            cx,
            cy,
            12f,
            paint
        )

        paint.color = Color.WHITE

        canvas.drawCircle(
            cx,
            cy,
            4f,
            paint
        )

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = radius * 0.25f
        paint.color = Color.WHITE

        canvas.drawText(
            value.toInt().toString(),
            cx,
            cy + 25f,
            paint
        )

        paint.textSize = 16f
        paint.color = Color.CYAN

        canvas.drawText(
            unit,
            cx,
            cy + radius * 0.55f,
            paint
        )
    }

    private fun drawCenterSpeed(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 70f
        paint.color = Color.WHITE

        canvas.drawText(
            vehicleData.speedKmh.toString(),
            cx,
            cy,
            paint
        )

        paint.textSize = 15f
        paint.color = Color.CYAN

        canvas.drawText(
            "CURRENT SPEED",
            cx,
            cy + 35f,
            paint
        )
    }

    private fun drawAIOrb(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {
        val time =
            System.currentTimeMillis()

        val pulse =
            sin(
                (time % 2000L)
                    .toDouble()
            ).toFloat()

        val size =
            60f +
                pulse * 8f

        val stateColor =
            when (aiState) {
                AIState.LISTENING ->
                    Color.GREEN

                AIState.THINKING ->
                    Color.YELLOW

                AIState.SPEAKING ->
                    Color.CYAN

                AIState.ERROR ->
                    Color.RED

                else ->
                    blue
            }

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 7f
        paint.color = stateColor

        canvas.drawCircle(
            cx,
            cy,
            size + 35f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.color = Color.argb(
            80,
            0,
            160,
            255
        )

        canvas.drawCircle(
            cx,
            cy,
            size + 20f,
            paint
        )

        paint.color =
            Color.rgb(
                0,
                130,
                240
            )

        canvas.drawCircle(
            cx,
            cy,
            size,
            paint
        )

        paint.color = Color.argb(
            120,
            180,
            240,
            255
        )

        canvas.drawCircle(
            cx - 18f,
            cy - 22f,
            size * 0.30f,
            paint
        )

        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 28f
        paint.color = Color.WHITE

        canvas.drawText(
            "AI",
            cx,
            cy + 10f,
            paint
        )

        paint.textSize = 12f
        paint.color = stateColor

        canvas.drawText(
            aiText(),
            cx,
            cy + 38f,
            paint
        )
    }

    private fun aiText(): String {
        return when (aiState) {

            AIState.IDLE ->
                "آماده‌ام"

            AIState.LISTENING ->
                "گوش می‌کنم"

            AIState.THINKING ->
                "پردازش"

            AIState.SPEAKING ->
                "صحبت"

            AIState.ERROR ->
                "خطا"
        }
    }

    private fun drawAIWave(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {
        val progress =
            (
                System.currentTimeMillis() %
                    1600L
                ).toFloat() / 1600f

        val color =
            when (aiState) {

                AIState.LISTENING ->
                    Color.GREEN

                AIState.THINKING ->
                    Color.YELLOW

                AIState.SPEAKING ->
                    Color.CYAN

                AIState.ERROR ->
                    Color.RED

                else ->
                    blue
            }

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.color = color
        paint.alpha = 160

        canvas.drawCircle(
            cx,
            cy,
            95f + progress * 55f,
            paint
        )

        paint.alpha = 255
    }

    private fun drawCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val cards =
            arrayOf(
                "CAR",
                "MUSIC",
                "NAVI",
                "PHONE",
                "SCAN"
            )

        val cardWidth = 120f
        val cardHeight = 60f
        val gap = 10f

        val totalWidth =
            cards.size * cardWidth +
                (cards.size - 1) * gap

        val start =
            (w - totalWidth) / 2f

        for (i in cards.indices) {

            val x =
                start +
                    i * (cardWidth + gap)

            val rect =
                RectF(
                    x,
                    h - 90f,
                    x + cardWidth,
                    h - 30f
                )

            paint.style = Paint.Style.FILL
            paint.color = Color.argb(
                80,
                255,
                255,
                255
            )

            canvas.drawRoundRect(
                rect,
                22f,
                22f,
                paint
            )

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2f
            paint.color = Color.CYAN

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
                cards[i],
                x + cardWidth / 2f,
                h - 55f,
                paint
            )
        }
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (event.actionMasked ==
            MotionEvent.ACTION_UP
        ) {

            val w = width.toFloat()
            val h = height.toFloat()

            val orbX = w / 2f
            val orbY = h * 0.70f

            val dx =
                event.x - orbX

            val dy =
                event.y - orbY

            val distance =
                sqrt(
                    dx * dx +
                        dy * dy
                )

            if (distance < 150f) {

                onAIOrbClick?.invoke()

                performClick()

                return true
            }

            val cardWidth = 120f
            val gap = 10f
            val cardTop = h - 90f
            val cardBottom = h - 30f

            val cards =
                arrayOf(
                    "CAR",
                    "MUSIC",
                    "NAVI",
                    "PHONE",
                    "SCAN"
                )

            val totalWidth =
                cards.size * cardWidth +
                    (cards.size - 1) * gap

            val start =
                (w - totalWidth) / 2f

            if (
                event.y >= cardTop &&
                event.y <= cardBottom
            ) {

                for (i in cards.indices) {

                    val left =
                        start +
                            i * (cardWidth + gap)

                    val right =
                        left + cardWidth

                    if (
                        event.x >= left &&
                        event.x <= right
                    ) {

                        when (cards[i]) {

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

        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
