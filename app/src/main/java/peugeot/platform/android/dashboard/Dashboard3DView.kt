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
import kotlin.math.sqrt


class Dashboard3DView(
    context: Context
) : View(context) {

    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)

    private var vehicleData =
        VehicleData.demo()

    private var aiState =
        AIState.IDLE

    var onAIOrbClick:
        (() -> Unit)? = null

    private val blue =
        Color.rgb(0, 170, 255)

    private val darkBlue =
        Color.rgb(2, 8, 18)

    private val panelColor =
        Color.argb(105, 255, 255, 255)

    private val borderColor =
        Color.argb(135, 0, 180, 255)


    fun setVehicleData(
        data: VehicleData
    ) {
        vehicleData = data
        invalidate()
    }


    fun setAIState(
        state: AIState
    ) {
        aiState = state
        invalidate()
    }


    override fun onDraw(
        canvas: Canvas
    ) {
        super.onDraw(canvas)

        val w =
            width.toFloat()

        val h =
            height.toFloat()


        drawBackground(
            canvas,
            w,
            h
        )


        drawHeader(
            canvas,
            w
        )


        drawGauge(
            canvas,
            w * 0.25f,
            h * 0.50f,
            min(w, h) * 0.20f,
            vehicleData.speedKmh.toFloat(),
            240f,
            "KM/H"
        )


        drawGauge(
            canvas,
            w * 0.75f,
            h * 0.50f,
            min(w, h) * 0.20f,
            vehicleData.rpm.toFloat(),
            8000f,
            "RPM"
        )


        drawCenterSpeed(
            canvas,
            w / 2f,
            h * 0.43f
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


        postInvalidateDelayed(
            40L
        )
    }


    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        paint.style =
            Paint.Style.FILL

        paint.shader =
            LinearGradient(
                0f,
                0f,
                0f,
                h,
                Color.rgb(1, 4, 10),
                Color.rgb(8, 25, 42),
                Shader.TileMode.CLAMP
            )

        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            paint
        )

        paint.shader =
            null


        // Ambient center glow

        paint.color =
            Color.argb(
                35,
                0,
                160,
                255
            )

        canvas.drawCircle(
            w / 2f,
            h * 0.48f,
            min(w, h) * 0.46f,
            paint
        )


        // Top ambient glow

        paint.color =
            Color.argb(
                28,
                0,
                100,
                255
            )

        canvas.drawCircle(
            w / 2f,
            0f,
            w * 0.45f,
            paint
        )
    }


    private fun drawHeader(
        canvas: Canvas,
        w: Float
    ) {

        val rect =
            RectF(
                35f,
                22f,
                w - 35f,
                92f
            )


        // Glass panel

        paint.style =
            Paint.Style.FILL

        paint.color =
            panelColor

        canvas.drawRoundRect(
            rect,
            34f,
            34f,
            paint
        )


        // Blue border

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            2f

        paint.color =
            borderColor

        canvas.drawRoundRect(
            rect,
            34f,
            34f,
            paint
        )


        // Header title

        paint.style =
            Paint.Style.FILL

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            25f

        paint.color =
            Color.WHITE

        canvas.drawText(
            "PEUGEOT  •  VEHICLE OS",
            w / 2f,
            66f,
            paint
        )


        paint.textSize =
            11f

        paint.color =
            Color.rgb(
                130,
                210,
                255
            )

        canvas.drawText(
            "BMW LUXURY INTERFACE",
            w / 2f,
            84f,
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
                .coerceIn(
                    0f,
                    1f
                )


        val rect =
            RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
            )


        // Outer glass halo

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            28f

        paint.strokeCap =
            Paint.Cap.ROUND

        paint.color =
            Color.argb(
                45,
                80,
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

        paint.strokeWidth =
            3f

        paint.color =
            Color.argb(
                150,
                120,
                220,
                255
            )

        canvas.drawArc(
            rect,
            135f,
            270f,
            false,
            paint
        )


        // Progress

        paint.strokeWidth =
            9f

        paint.color =
            blue

        canvas.drawArc(
            rect,
            135f,
            270f * progress,
            false,
            paint
        )


        // Tick marks

        paint.strokeWidth =
            2.5f

        paint.color =
            Color.argb(
                210,
                230,
                245,
                255
            )


        for (i in 0..36) {

            val angle =
                Math.toRadians(
                    135.0 + i * 7.5
                )

            val inner =
                radius - 18f

            val outer =
                if (i % 5 == 0) {
                    radius + 5f
                } else {
                    radius - 5f
                }


            canvas.drawLine(
                cx + cos(angle).toFloat() * inner,
                cy + sin(angle).toFloat() * inner,
                cx + cos(angle).toFloat() * outer,
                cy + sin(angle).toFloat() * outer,
                paint
            )
        }


        // Needle

        val needleAngle =
            Math.toRadians(
                135.0 + 270.0 * progress
            )

        paint.strokeWidth =
            5f

        paint.color =
            Color.WHITE

        canvas.drawLine(
            cx,
            cy,
            cx + cos(needleAngle).toFloat() *
                    (radius - 35f),
            cy + sin(needleAngle).toFloat() *
                    (radius - 35f),
            paint
        )


        // Center hub

        paint.style =
            Paint.Style.FILL

        paint.color =
            blue

        canvas.drawCircle(
            cx,
            cy,
            12f,
            paint
        )


        paint.color =
            Color.WHITE

        canvas.drawCircle(
            cx,
            cy,
            4f,
            paint
        )


        // Value

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            radius * 0.25f

        paint.color =
            Color.WHITE

        canvas.drawText(
            value.toInt().toString(),
            cx,
            cy + 25f,
            paint
        )


        paint.textSize =
            17f

        paint.color =
            Color.CYAN

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

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.color =
            Color.WHITE

        paint.textSize =
            70f

        canvas.drawText(
            vehicleData.speedKmh.toString(),
            cx,
            cy,
            paint
        )


        paint.textSize =
            16f

        paint.color =
            blue

        canvas.drawText(
            "CURRENT SPEED",
            cx,
            cy + 34f,
            paint
        )


        paint.textSize =
            11f

        paint.color =
            Color.argb(
                180,
                220,
                240,
                255
            )

        canvas.drawText(
            "km/h",
            cx,
            cy + 52f,
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
            (time % 2000L)
                .toFloat() / 2000f

        val wave =
            sin(
                pulse * Math.PI * 2.0
            ).toFloat()

        val size =
            62f + wave * 8f


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


        // Outer energy ring

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            7f

        paint.color =
            stateColor

        paint.alpha =
            210

        canvas.drawCircle(
            cx,
            cy,
            size + 34f,
            paint
        )


        // Glow

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                65,
                0,
                170,
                255
            )

        canvas.drawCircle(
            cx,
            cy,
            size + 22f,
            paint
        )


        // Main orb

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


        // Inner highlight

        paint.color =
            Color.argb(
                90,
                120,
                220,
                255
            )

        canvas.drawCircle(
            cx - 16f,
            cy - 18f,
            size * 0.35f,
            paint
        )


        // AI

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            28f

        paint.color =
            Color.WHITE

        paint.alpha =
            255

        canvas.drawText(
            "AI",
            cx,
            cy + 8f,
            paint
        )


        // State

        paint.textSize =
            12f

        paint.color =
            stateColor

        val stateText =
            when (aiState) {

                AIState.IDLE ->
                    "آماده‌ام"

                AIState.LISTENING ->
                    "گوش می‌کنم"

                AIState.THINKING ->
                    "در حال پردازش"

                AIState.SPEAKING ->
                    "در حال صحبت"

                AIState.ERROR ->
                    "خطا"
            }

        canvas.drawText(
            stateText,
            cx,
            cy + 36f,
            paint
        )
    }


    private fun drawAIWave(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ) {

        val time =
            System.currentTimeMillis()

        val progress =
            (time % 1600L)
                .toFloat() / 1600f


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


        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            3f

        paint.color =
            stateColor


        val radius1 =
            92f + progress * 55f

        val radius2 =
            92f + ((progress + 0.5f) % 1f) * 55f


        paint.alpha =
            ((1f - progress) * 180f)
                .toInt()
                .coerceIn(
                    0,
                    180
                )


        canvas.drawCircle(
            cx,
            cy,
            radius1,
            paint
        )


        paint.alpha =
            100

        canvas.drawCircle(
            cx,
            cy,
            radius2,
            paint
        )


        paint.alpha =
            255
    }


    private fun drawCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val items =
            arrayOf(
                "CAR",
                "MUSIC",
                "NAVI",
                "PHONE",
                "SCAN"
            )


        val width =
            120f

        val height =
            62f


        val gap =
            8f


        val totalWidth =
            items.size * width +
                    (items.size - 1) * gap


        val start =
            (w - totalWidth) / 2f


        for (i in items.indices) {

            val x =
                start +
                        i * (width + gap)


            val rect =
                RectF(
                    x,
                    h - 92f,
                    x + width,
                    h - 30f
                )


            // Glass background

            paint.style =
                Paint.Style.FILL

            paint.color =
                panelColor

            canvas.drawRoundRect(
                rect,
                24f,
                24f,
                paint
            )


            // Blue border

            paint.style =
                Paint.Style.STROKE

            paint.strokeWidth =
                1.5f

            paint.color =
                Color.argb(
                    100,
                    0,
                    180,
                    255
                )

            canvas.drawRoundRect(
                rect,
                24f,
                24f,
                paint
            )


            // Text

            paint.style =
                Paint.Style.FILL

            paint.textAlign =
                Paint.Align.CENTER

            paint.typeface =
                Typeface.DEFAULT_BOLD

            paint.textSize =
                14f

            paint.color =
                Color.WHITE

            canvas.drawText(
                items[i],
                x + width / 2f,
                h - 54f,
                paint
            )
        }
    }


    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        if (
            event.action ==
            MotionEvent.ACTION_UP
        ) {

            val dx =
                event.x - width / 2f

            val dy =
                event.y - height * 0.70f


            val distance =
                sqrt(
                    dx * dx +
                            dy * dy
                )


            if (
                distance < 140f
            ) {

                onAIOrbClick?.invoke()
            }


            performClick()

            return true
        }


        return true
    }


    override fun performClick():
            Boolean {

        super.performClick()

        return true
    }
}
