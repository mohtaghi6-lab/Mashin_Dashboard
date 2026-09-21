package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.vehicle.VehicleData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class HomePageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var vehicleData =
        VehicleData.demo()


    private var aiState =
        AIState.IDLE


    private var pulse =
        0f


    var onAIOrbClick:
            (() -> Unit)? = null



    private val blue =
        Color.rgb(
            74,
            196,
            255
        )


    private val cyan =
        Color.rgb(
            116,
            231,
            255
        )


    private val white =
        Color.WHITE


    private val muted =
        Color.rgb(
            142,
            164,
            180
        )


    private val panel =
        Color.argb(
            150,
            10,
            22,
            36
        )



    private val timeFormat =
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        )


    private val dateFormat =
        SimpleDateFormat(
            "EEE, dd MMM",
            Locale.ENGLISH
        )



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


        if (
            w <= 0f ||
            h <= 0f
        ) {
            return
        }



        drawBackground(
            canvas,
            w,
            h
        )


        drawHeader(
            canvas,
            w,
            h
        )


        drawGauge(
            canvas,
            w * 0.20f,
            h * 0.43f,
            min(w, h) * 0.14f,
            vehicleData.speedKmh.toFloat(),
            240f,
            "km/h",
            "SPEED"
        )


        drawGauge(
            canvas,
            w * 0.80f,
            h * 0.43f,
            min(w, h) * 0.14f,
            vehicleData.rpm.toFloat(),
            8000f,
            "RPM",
            "ENGINE"
        )


        drawAIOrb(
            canvas,
            w / 2f,
            h * 0.43f,
            min(w, h) * 0.10f
        )


        drawCenterInfo(
            canvas,
            w,
            h
        )


        drawStatusCards(
            canvas,
            w,
            h
        )


        drawBottomCards(
            canvas,
            w,
            h
        )


        pulse += 0.035f


        if (
            pulse > 1000f
        ) {

            pulse = 0f

        }


        postInvalidateDelayed(40L)

    }



    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {


        val backgroundGradient =
            LinearGradient(
                0f,
                0f,
                0f,
                h,
                Color.rgb(
                    2,
                    8,
                    16
                ),
                Color.rgb(
                    7,
                    18,
                    30
                ),
                Shader.TileMode.CLAMP
            )


        paint.shader =
            backgroundGradient


        paint.style =
            Paint.Style.FILL


        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            paint
        )


        paint.shader = null



        val glow =
            RadialGradient(
                w / 2f,
                h * 0.42f,
                min(w, h) * 0.55f,
                intArrayOf(
                    Color.argb(
                        55,
                        45,
                        150,
                        220
                    ),
                    Color.argb(
                        20,
                        25,
                        100,
                        160
                    ),
                    Color.TRANSPARENT
                ),
                null,
                Shader.TileMode.CLAMP
            )


        paint.shader =
            glow


        canvas.drawCircle(
            w / 2f,
            h * 0.42f,
            min(w, h) * 0.55f,
            paint
        )


        paint.shader = null


        paint.color =
            Color.argb(
                30,
                100,
                200,
                255
            )


        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            1f


        val gridSize =
            80f


        var x =
            0f


        while (
            x < w
        ) {

            canvas.drawLine(
                x,
                0f,
                x,
                h,
                paint
            )

            x += gridSize
        }


        var y =
            0f


        while (
            y < h
        ) {

            canvas.drawLine(
                0f,
                y,
                w,
                y,
                paint
            )

            y += gridSize
        }


        paint.style =
            Paint.Style.FILL

    }
        private fun drawHeader(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val now =
            Date()


        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.LEFT


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.textSize =
            28f


        paint.color =
            white


        canvas.drawText(
            timeFormat.format(now),
            35f,
            48f,
            paint
        )


        paint.textSize =
            11f


        paint.typeface =
            Typeface.DEFAULT


        paint.color =
            muted


        canvas.drawText(
            dateFormat.format(now),
            37f,
            68f,
            paint
        )


        paint.textAlign =
            Paint.Align.RIGHT


        paint.textSize =
            14f


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.color =
            white


        canvas.drawText(
            "PEUGEOT PARS",
            w - 35f,
            45f,
            paint
        )


        paint.textSize =
            10f


        paint.color =
            cyan


        canvas.drawText(
            "LUXURY VEHICLE OS",
            w - 35f,
            63f,
            paint
        )


        paint.textAlign =
            Paint.Align.LEFT

    }





    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        maxValue: Float,
        unit: String,
        label: String
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


        paint.style =
            Paint.Style.STROKE


        paint.strokeCap =
            Paint.Cap.ROUND


        paint.strokeWidth =
            14f


        paint.color =
            Color.argb(
                45,
                100,
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


        paint.strokeWidth =
            5f


        paint.color =
            cyan


        canvas.drawArc(
            rect,
            135f,
            270f * progress,
            false,
            paint
        )


        paint.strokeWidth =
            2f


        paint.color =
            Color.argb(
                100,
                180,
                230,
                255
            )


        for (
            i in 0..24
        ) {

            val angle =
                Math.toRadians(
                    135.0 +
                    i * 11.25
                )


            val outer =
                radius + 10f


            val inner =
                if (
                    i % 3 == 0
                ) {
                    radius - 5f
                } else {
                    radius + 2f
                }



            canvas.drawLine(
                cx +
                    cos(angle).toFloat()
                    * inner,

                cy +
                    sin(angle).toFloat()
                    * inner,

                cx +
                    cos(angle).toFloat()
                    * outer,

                cy +
                    sin(angle).toFloat()
                    * outer,

                paint
            )

        }



        val needle =
            Math.toRadians(
                135.0 +
                270.0 * progress
            )


        paint.strokeWidth =
            4f


        paint.strokeCap =
            Paint.Cap.ROUND


        paint.color =
            white



        canvas.drawLine(
            cx,
            cy,

            cx +
                cos(needle).toFloat()
                *
                (radius - 18f),

            cy +
                sin(needle).toFloat()
                *
                (radius - 18f),

            paint
        )


        paint.style =
            Paint.Style.FILL


        paint.color =
            blue


        canvas.drawCircle(
            cx,
            cy,
            7f,
            paint
        )



        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.textSize =
            radius * 0.28f


        paint.color =
            white



        canvas.drawText(
            value.toInt().toString(),
            cx,
            cy + radius * 0.12f,
            paint
        )


        paint.textSize =
            11f


        paint.color =
            cyan


        canvas.drawText(
            unit,
            cx,
            cy + radius * 0.31f,
            paint
        )


        paint.textSize =
            9f


        paint.color =
            muted


        canvas.drawText(
            label,
            cx,
            cy + radius * 0.49f,
            paint
        )


        paint.textAlign =
            Paint.Align.LEFT


        paint.strokeCap =
            Paint.Cap.BUTT

    }
        private fun drawAIOrb(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float
    ) {


        val time =
            System.currentTimeMillis()



        val wave =
            (
                (sin(time / 250.0) + 1.0)
                / 2.0
            ).toFloat()



        val statePower =
            when(aiState) {

                AIState.LISTENING ->
                    1.35f


                AIState.THINKING ->
                    1.55f


                AIState.SPEAKING ->
                    1.75f


                else ->
                    1.20f
            }



        val glowRadius =
            radius *
                (
                    statePower +
                    wave * 0.25f
                )



        val glowPaint =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            )



        glowPaint.shader =
            RadialGradient(

                cx,

                cy,

                glowRadius,

                intArrayOf(

                    Color.argb(
                        220,
                        130,
                        235,
                        255
                    ),

                    Color.argb(
                        100,
                        70,
                        196,
                        255
                    ),

                    Color.argb(
                        20,
                        30,
                        120,
                        220
                    ),

                    Color.TRANSPARENT

                ),

                floatArrayOf(
                    0f,
                    0.45f,
                    0.70f,
                    1f
                ),

                Shader.TileMode.CLAMP
            )



        canvas.drawCircle(
            cx,
            cy,
            glowRadius,
            glowPaint
        )



        paint.shader =
            RadialGradient(

                cx - radius * 0.25f,

                cy - radius * 0.30f,

                radius * 1.15f,

                intArrayOf(

                    Color.rgb(
                        220,
                        250,
                        255
                    ),

                    Color.rgb(
                        70,
                        196,
                        255
                    ),

                    Color.rgb(
                        18,
                        83,
                        145
                    ),

                    Color.rgb(
                        3,
                        20,
                        40
                    )

                ),

                floatArrayOf(
                    0f,
                    0.35f,
                    0.72f,
                    1f
                ),

                Shader.TileMode.CLAMP
            )



        paint.style =
            Paint.Style.FILL



        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )



        paint.shader =
            null



        paint.style =
            Paint.Style.STROKE



        paint.strokeWidth =
            2.5f



        paint.color =
            Color.argb(
                220,
                180,
                245,
                255
            )



        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )



        paint.strokeWidth =
            1f



        paint.color =
            Color.argb(
                120,
                120,
                220,
                255
            )



        canvas.drawCircle(
            cx,
            cy,
            radius * 1.25f,
            paint
        )



        paint.style =
            Paint.Style.FILL



        paint.textAlign =
            Paint.Align.CENTER



        paint.typeface =
            Typeface.DEFAULT_BOLD



        paint.textSize =
            radius * 0.32f



        paint.color =
            Color.WHITE



        canvas.drawText(
            "MRT",
            cx,
            cy + radius * 0.10f,
            paint
        )



        paint.textSize =
            radius * 0.12f



        paint.typeface =
            Typeface.DEFAULT



        val stateText =
            when(aiState) {

                AIState.IDLE ->
                    "READY"


                AIState.LISTENING ->
                    "LISTENING"


                AIState.THINKING ->
                    "THINKING"


                AIState.SPEAKING ->
                    "SPEAKING"


                AIState.ERROR ->
                    "ERROR"

            }



        paint.color =
            Color.argb(
                230,
                225,
                248,
                255
            )



        canvas.drawText(
            stateText,
            cx,
            cy + radius * 0.42f,
            paint
        )



        paint.textAlign =
            Paint.Align.LEFT

    }
            private fun drawCenterInfo(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        paint.textAlign =
            Paint.Align.CENTER

        paint.typeface =
            Typeface.DEFAULT_BOLD

        paint.textSize =
            15f

        paint.color =
            white


        canvas.drawText(
            "سلام MRT",
            w / 2f,
            h * 0.61f,
            paint
        )


        paint.textSize =
            11f

        paint.typeface =
            Typeface.DEFAULT

        paint.color =
            muted


        canvas.drawText(
            "آماده دریافت فرمان صوتی",
            w / 2f,
            h * 0.645f,
            paint
        )


        paint.textSize =
            9f

        paint.color =
            cyan


        canvas.drawText(
            "VOICE ASSISTANT ONLINE",
            w / 2f,
            h * 0.68f,
            paint
        )


        paint.textAlign =
            Paint.Align.LEFT

    }





    private fun drawStatusCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val top =
            h * 0.72f


        val bottom =
            h * 0.82f


        val margin =
            30f


        val gap =
            12f


        val cardW =
            (w - margin * 2f - gap * 2f) / 3f



        drawGlassCard(
            canvas,
            margin,
            top,
            margin + cardW,
            bottom,
            "ENGINE",
            "${vehicleData.engineTempC}°C"
        )


        drawGlassCard(
            canvas,
            margin + cardW + gap,
            top,
            margin + cardW * 2f + gap,
            bottom,
            "FUEL",
            "${vehicleData.fuelPercent}%"
        )


        drawGlassCard(
            canvas,
            margin + cardW * 2f + gap * 2f,
            top,
            w - margin,
            bottom,
            "CAN",
            if(vehicleData.canConnected)
                "ONLINE"
            else
                "STANDBY"
        )

    }





    private fun drawBottomCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {

        val top =
            h * 0.85f


        val bottom =
            h * 0.94f


        val margin =
            30f


        val gap =
            12f


        val cardW =
            (w - margin * 2f - gap * 2f) / 3f



        drawGlassCard(
            canvas,
            margin,
            top,
            margin + cardW,
            bottom,
            "BLUETOOTH",
            "READY"
        )


        drawGlassCard(
            canvas,
            margin + cardW + gap,
            top,
            margin + cardW * 2f + gap,
            bottom,
            "MUSIC",
            "READY"
        )


        drawGlassCard(
            canvas,
            margin + cardW * 2f + gap * 2f,
            top,
            w - margin,
            bottom,
            "SYSTEM",
            "ONLINE"
        )

    }





    private fun drawGlassCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        title: String,
        value: String
    ) {


        val rect =
            RectF(
                left,
                top,
                right,
                bottom
            )


        paint.style =
            Paint.Style.FILL


        paint.color =
            panel


        canvas.drawRoundRect(
            rect,
            18f,
            18f,
            paint
        )



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            1f


        paint.color =
            Color.argb(
                75,
                110,
                210,
                255
            )


        canvas.drawRoundRect(
            rect,
            18f,
            18f,
            paint
        )



        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.LEFT


        paint.textSize =
            8f


        paint.color =
            muted


        canvas.drawText(
            title,
            left + 14f,
            top + 23f,
            paint
        )



        paint.textSize =
            12f


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.color =
            white


        canvas.drawText(
            value,
            left + 14f,
            top + 43f,
            paint
        )



        paint.color =
            cyan


        canvas.drawCircle(
            right - 15f,
            top + 18f,
            3f,
            paint
        )

    }





    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {


        if(
            event.action ==
            MotionEvent.ACTION_UP
        ) {


            val cx =
                width / 2f


            val cy =
                height * 0.43f


            val radius =
                min(width, height) * 0.14f



            val dx =
                event.x - cx


            val dy =
                event.y - cy



            val distance =
                kotlin.math.sqrt(
                    dx * dx +
                    dy * dy
                )



            if(
                distance <= radius
            ) {

                onAIOrbClick?.invoke()

                performClick()

                return true
            }

        }


        return true
    }





    override fun performClick(): Boolean {

        super.performClick()

        return true
    }

}
