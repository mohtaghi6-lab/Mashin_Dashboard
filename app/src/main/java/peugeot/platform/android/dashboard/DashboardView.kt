package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.vehicle.VehicleData
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class DashboardView(context: Context) : View(context) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var data = VehicleData.demo()
    private var pulse = 0f
    private var aiState = AIState.IDLE

    var onAIOrbClick: (() -> Unit)? = null

    private val dark = Color.rgb(3, 7, 13)
    private val panel = Color.rgb(8, 15, 25)
    private val blue = Color.rgb(55, 185, 255)
    private val cyan = Color.rgb(0, 225, 255)
    private val white = Color.WHITE
    private val gray = Color.rgb(145, 160, 175)

    init {
        isFocusable = true
        postInvalidateOnAnimation()
    }

    fun setVehicleData(value: VehicleData) {
        data = value
        invalidate()
    }

    fun setAIState(value: AIState) {
        aiState = value
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width.toFloat()
        val h = height.toFloat()

        canvas.drawColor(dark)

        drawBackground(canvas, w, h)
        drawTopBar(canvas, w)
        drawSpeedometer(canvas, w, h)
        drawRpm(canvas, w, h)
        drawBottomInfo(canvas, w, h)
        drawAIOrb(canvas, w * 0.5f, h * 0.48f)

        pulse += 0.035f
        postInvalidateOnAnimation()
    }

    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        paint.style = Paint.Style.FILL

        val gradient = LinearGradient(
            0f,
            0f,
            0f,
            h,
            Color.rgb(2, 6, 12),
            Color.rgb(5, 13, 23),
            Shader.TileMode.CLAMP
        )

        paint.shader = gradient
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null

        paint.color = Color.argb(35, 70, 190, 255)

        canvas.drawCircle(
            w * 0.5f,
            h * 0.45f,
            min(w, h) * 0.42f,
            paint
        )
    }

    private fun drawTopBar(
        canvas: Canvas,
        w: Float
    ) {
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(130, 8, 18, 30)

        canvas.drawRoundRect(
            24f,
            18f,
            w - 24f,
            78f,
            24f,
            24f,
            paint
        )

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 22f
        paint.color = white

        canvas.drawText(
            "PEUGEOT",
            48f,
            55f,
            paint
        )

        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 18f
        paint.color = blue

        canvas.drawText(
            "VEHICLE OS",
            w * 0.5f,
            55f,
            paint
        )

        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = 20f
        paint.color = white

        canvas.drawText(
            currentTime(),
            w - 48f,
            55f,
            paint
        )
    }

    private fun drawSpeedometer(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val cx = w * 0.24f
        val cy = h * 0.53f

        val radius = min(w, h) * 0.22f

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = Color.rgb(20, 35, 48)

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

        val speedSweep =
            (data.speedKmh.coerceIn(0, 240) / 240f) * 270f

        paint.color = blue
        paint.strokeWidth = 12f

        canvas.drawArc(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius,
            135f,
            speedSweep,
            false,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = white
        paint.textSize = 48f

        canvas.drawText(
            "${data.speedKmh}",
            cx,
            cy + 12f,
            paint
        )

        paint.color = blue
        paint.textSize = 17f

        canvas.drawText(
            "km/h",
            cx,
            cy + 40f,
            paint
        )

        paint.color = gray
        paint.textSize = 13f

        canvas.drawText(
            "SPEED",
            cx,
            cy + 68f,
            paint
        )

        drawGaugeTicks(
            canvas,
            cx,
            cy,
            radius
        )
    }

    private fun drawGaugeTicks(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {
        paint.color = Color.argb(170, 130, 180, 210)
        paint.strokeWidth = 2f

        for (i in 0..12) {
            val angle =
                Math.toRadians(
                    (135 + i * 22.5).toDouble()
                )

            val outer = radius + 5f
            val inner =
                if (i % 3 == 0)
                    radius - 18f
                else
                    radius - 10f

            val x1 =
                cx + cos(angle).toFloat() * inner

            val y1 =
                cy + sin(angle).toFloat() * inner

            val x2 =
                cx + cos(angle).toFloat() * outer

            val y2 =
                cy + sin(angle).toFloat() * outer

            canvas.drawLine(
                x1,
                y1,
                x2,
                y2,
                paint
            )
        }
    }

    private fun drawRpm(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val cx = w * 0.76f
        val cy = h * 0.53f

        val radius = min(w, h) * 0.22f

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        paint.strokeCap = Paint.Cap.ROUND

        paint.color = Color.rgb(20, 35, 48)

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

        val rpmSweep =
            (data.rpm.coerceIn(0, 8000) / 8000f) * 270f

        paint.color =
            if (data.rpm > 6500)
                Color.rgb(255, 80, 70)
            else
                cyan

        paint.strokeWidth = 12f

        canvas.drawArc(
            cx - radius,
            cy - radius,
            cx + radius,
            cy + radius,
            135f,
            rpmSweep,
            false,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD

        paint.color = white
        paint.textSize = 43f

        canvas.drawText(
            "${data.rpm}",
            cx,
            cy + 12f,
            paint
        )

        paint.color = cyan
        paint.textSize = 17f

        canvas.drawText(
            "RPM",
            cx,
            cy + 40f,
            paint
        )

        paint.color = gray
        paint.textSize = 13f

        canvas.drawText(
            "ENGINE",
            cx,
            cy + 68f,
            paint
        )

        drawGaugeTicks(
            canvas,
            cx,
            cy,
            radius
        )
    }

    private fun drawBottomInfo(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {
        val top = h - 105f
        val bottom = h - 22f

        drawInfoCard(
            canvas,
            28f,
            top,
            w * 0.23f,
            bottom,
            "TEMP",
            "${data.engineTempC}°C"
        )

        drawInfoCard(
            canvas,
            w * 0.27f,
            top,
            w * 0.48f,
            bottom,
            "FUEL",
            "${data.fuelPercent}%"
        )

        drawInfoCard(
            canvas,
            w * 0.52f,
            top,
            w * 0.73f,
            bottom,
            "BATTERY",
            String.format(
                java.util.Locale.US,
                "%.1fV",
                data.batteryVoltage
            )
        )

        drawInfoCard(
            canvas,
            w * 0.77f,
            top,
            w - 28f,
            bottom,
            "CAN",
            if (data.canConnected)
                "ONLINE"
            else
                "WAITING"
        )
    }

    private fun drawInfoCard(
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
        paint.strokeWidth = 1.5f
        paint.color = Color.argb(
            80,
            70,
            190,
            255
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

        val cx = (left + right) / 2f

        paint.color = gray
        paint.textSize = 11f

        canvas.drawText(
            title,
            cx,
            top + 25f,
            paint
        )

        paint.color =
            if (title == "CAN" && value == "ONLINE")
                Color.rgb(80, 255, 150)
            else
                white

        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 16f

        canvas.drawText(
            value,
            cx,
            top + 53f,
            paint
        )
    }

    private fun drawAIOrb(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {
        val wave =
            ((sin(pulse.toDouble()) + 1.0) / 2.0)
                .toFloat()

        paint.style = Paint.Style.STROKE

        for (i in 3 downTo 1) {
            paint.strokeWidth =
                2f + i * 1.5f

            paint.color = Color.argb(
                25 + i * 25,
                50,
                190,
                255
            )

            canvas.drawCircle(
                cx,
                cy,
                48f + i * 18f + wave * 8f,
                paint
            )
        }

        paint.style = Paint.Style.FILL

        val orbColor =
            when (aiState) {
                AIState.LISTENING ->
                    Color.rgb(0, 225, 255)

                AIState.THINKING ->
                    Color.rgb(255, 180, 50)

                AIState.SPEAKING ->
                    Color.rgb(80, 255, 150)

                AIState.ERROR ->
                    Color.rgb(255, 60, 60)

                else ->
                    blue
            }

        val glow =
            RadialGradient(
                cx,
                cy,
                65f + wave * 10f,
                intArrayOf(
                    Color.argb(180, 60, 210, 255),
                    Color.argb(70, 50, 180, 255),
                    Color.TRANSPARENT
                ),
                null,
                Shader.TileMode.CLAMP
            )

        paint.shader = glow

        canvas.drawCircle(
            cx,
            cy,
            65f + wave * 10f,
            paint
        )

        paint.shader = null

        paint.color = Color.argb(
            230,
            Color.red(orbColor),
            Color.green(orbColor),
            Color.blue(orbColor)
        )

        canvas.drawCircle(
            cx,
            cy,
            43f,
            paint
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.color = white

        canvas.drawCircle(
            cx,
            cy,
            43f,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.color = white
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
        paint.textSize = 21f

        canvas.drawText(
            "AI",
            cx,
            cy + 7f,
            paint
        )

        paint.textSize = 11f
        paint.color = Color.argb(210, 220, 240, 255)

        val stateText =
            when (aiState) {
                AIState.LISTENING -> "LISTENING"
                AIState.THINKING -> "THINKING"
                AIState.SPEAKING -> "SPEAKING"
                AIState.ERROR -> "ERROR"
                else -> "READY"
            }

        canvas.drawText(
            stateText,
            cx,
            cy + 86f,
            paint
        )
    }

    private fun currentTime(): String {
        return java.text.SimpleDateFormat(
            "HH:mm",
            java.util.Locale.getDefault()
        ).format(
            java.util.Date()
        )
    }

    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {

            val dx =
                event.x - width / 2f

            val dy =
                event.y - height * 0.48f

            val distance =
                kotlin.math.sqrt(
                    dx * dx + dy * dy
                )

            if (distance < 110f) {
                onAIOrbClick?.invoke()
                return true
            }
        }

        return true
    }
}
