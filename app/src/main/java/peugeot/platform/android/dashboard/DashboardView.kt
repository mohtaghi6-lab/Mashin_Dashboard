package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View

import peugeot.platform.android.ai.AIState
import peugeot.platform.android.vehicle.VehicleData

import kotlin.math.cos
import kotlin.math.sin


class DashboardView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private val vehicleRenderer =
        VehicleRenderer()


    private val warningLights =
        WarningLights()


    private var data =
        VehicleData.demo()


    private var aiState =
        AIState.IDLE


    private var animation =
        0f



    var onAIOrbClick:
            (() -> Unit)? = null



    private val blue =
        Color.rgb(
            0,
            170,
            255
        )


    private val dark =
        Color.rgb(
            2,
            6,
            12
        )



    fun setVehicleData(
        value: VehicleData
    ) {

        data = value

        invalidate()

    }



    fun setAIState(
        value: AIState
    ) {

        aiState = value

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



        canvas.drawColor(
            dark
        )



        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD




        // Header

        paint.color =
            Color.WHITE


        paint.textSize =
            26f


        canvas.drawText(
            "PEUGEOT VEHICLE OS",
            w / 2f,
            45f,
            paint
        )





        // Center vehicle

        vehicleRenderer.drawVehicle(

            canvas,

            w / 2f,

            h * 0.38f,

            1f

        )






        // RPM

        drawGauge(

            canvas,

            w * 0.25f,

            h * 0.45f,

            120f,

            data.rpm.toFloat(),

            8000f,

            "RPM"

        )





        // SPEED

        drawGauge(

            canvas,

            w * 0.75f,

            h * 0.45f,

            120f,

            data.speedKmh.toFloat(),

            240f,

            "km/h"

        )






        // Warning Lights

        warningLights.draw(

            canvas,

            w / 2f - 160f,

            h * 0.80f

        )






        // Information

        drawInfo(

            canvas,

            w,

            h

        )






        // AI Orb

        drawAIOrb(

            canvas,

            w / 2f,

            h * 0.68f

        )





        animation += 0.03f


        postInvalidateOnAnimation()

    }







    private fun drawGauge(

        canvas: Canvas,

        cx: Float,

        cy: Float,

        radius: Float,

        value: Float,

        max: Float,

        label: String

    ) {



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            12f



        paint.color =
            Color.rgb(
                15,
                45,
                75
            )



        canvas.drawCircle(

            cx,

            cy,

            radius,

            paint

        )



        val percent =
            (value / max)
                .coerceIn(
                    0f,
                    1f
                )



        paint.color =
            blue



        val rect =
            RectF(

                cx - radius,

                cy - radius,

                cx + radius,

                cy + radius

            )



        canvas.drawArc(

            rect,

            135f,

            270f * percent,

            false,

            paint

        )



        paint.style =
            Paint.Style.FILL



        paint.color =
            Color.WHITE



        paint.textSize =
            42f



        canvas.drawText(

            value.toInt().toString(),

            cx,

            cy + 10,

            paint

        )



        paint.textSize =
            18f



        paint.color =
            blue



        canvas.drawText(

            label,

            cx,

            cy + 38,

            paint

        )





        // Needle

        val angle =

            Math.toRadians(

                (135f + 270f * percent)
                    .toDouble()

            )



        paint.strokeWidth =
            3f


        paint.color =
            Color.WHITE



        canvas.drawLine(

            cx,

            cy,

            cx +
                    cos(angle).toFloat()
                    *
                    radius,

            cy +
                    sin(angle).toFloat()
                    *
                    radius,

            paint

        )


    }







    private fun drawInfo(

        canvas: Canvas,

        w: Float,

        h: Float

    ) {



        paint.textSize =
            17f



        paint.color =
            Color.LTGRAY



        canvas.drawText(

            "TEMP ${data.engineTempC}°C",

            w * 0.25f,

            h * 0.93f,

            paint

        )



        canvas.drawText(

            "VOLT 13.8V",

            w * 0.50f,

            h * 0.93f,

            paint

        )



        paint.color =
            Color.GREEN



        canvas.drawText(

            "CAN / GPCU READY",

            w * 0.75f,

            h * 0.93f,

            paint

        )

    }







    private fun drawAIOrb(

        canvas: Canvas,

        cx: Float,

        cy: Float

    ) {



        val pulse =

            ((sin(animation.toDouble()) + 1)
                    .toFloat())



        paint.style =
            Paint.Style.STROKE



        paint.strokeWidth =
            5f



        paint.color =
            Color.argb(

                150,

                0,

                180,

                255

            )



        canvas.drawCircle(

            cx,

            cy,

            45f + pulse * 8f,

            paint

        )



        paint.style =
            Paint.Style.FILL



        paint.color =
            when(aiState){


                AIState.LISTENING ->
                    Color.CYAN


                AIState.THINKING ->
                    Color.YELLOW


                AIState.SPEAKING ->
                    Color.GREEN


                AIState.ERROR ->
                    Color.RED


                else ->
                    blue

            }




        canvas.drawCircle(

            cx,

            cy,

            30f,

            paint

        )




        paint.color =
            Color.WHITE


        paint.textSize =
            18f



        canvas.drawText(

            "AI",

            cx,

            cy + 7,

            paint

        )

    }







    override fun onTouchEvent(

        event: MotionEvent

    ): Boolean {



        if(

            event.action ==
            MotionEvent.ACTION_UP

        ){



            val dx =
                event.x -
                        width / 2f



            val dy =
                event.y -
                        height / 2f



            if(

                dx * dx +
                        dy * dy <
                        10000

            ){

                onAIOrbClick?.invoke()

            }

        }



        return true

    }

}
