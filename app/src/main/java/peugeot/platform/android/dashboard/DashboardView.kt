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




        // Title

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




        // Speed gauge

        drawGauge(

            canvas,

            w * 0.27f,

            h * 0.48f,

            130f,

            data.speedKmh.toFloat(),

            240f,

            "km/h"

        )





        // RPM gauge

        drawGauge(

            canvas,

            w * 0.73f,

            h * 0.48f,

            130f,

            data.rpm.toFloat(),

            8000f,

            "RPM"

        )






        // Vehicle information

        drawInfo(

            canvas,

            w,

            h

        )





        // AI Center

        drawAIOrb(

            canvas,

            w / 2f,

            h * 0.62f

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

        title: String

    ) {



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            10f



        paint.color =
            Color.rgb(
                20,
                50,
                80
            )



        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )



        paint.color =
            blue



        val sweep =
            270f *
            (value / max)



        val rect =
            RectF(

                cx-radius,

                cy-radius,

                cx+radius,

                cy+radius

            )



        canvas.drawArc(

            rect,

            135f,

            sweep,

            false,

            paint

        )





        paint.style =
            Paint.Style.FILL



        paint.color =
            Color.WHITE



        paint.textSize =
            48f



        canvas.drawText(

            value.toInt().toString(),

            cx,

            cy+10,

            paint

        )





        paint.textSize =
            18f


        paint.color =
            blue



        canvas.drawText(

            title,

            cx,

            cy+40,

            paint

        )





        // Needle


        val angle =
            Math.toRadians(

                (135 + sweep).toDouble()

            )



        paint.color =
            Color.WHITE


        paint.strokeWidth =
            4f



        canvas.drawLine(

            cx,

            cy,

            cx + cos(angle).toFloat()*radius,

            cy + sin(angle).toFloat()*radius,

            paint

        )

    }








    private fun drawInfo(

        canvas: Canvas,

        w: Float,

        h: Float

    ) {



        paint.textSize =
            18f



        paint.color =
            Color.LTGRAY



        canvas.drawText(

            "TEMP : ${data.engineTempC} °C",

            w/2,

            h*0.84f,

            paint

        )



        canvas.drawText(

            "VOLT : 13.8 V",

            w/2,

            h*0.88f,

            paint

        )



        paint.color =
            Color.GREEN



        canvas.drawText(

            "CAN : OK     GPCU : OK",

            w/2,

            h*0.92f,

            paint

        )


    }








    private fun drawAIOrb(

        canvas: Canvas,

        cx: Float,

        cy: Float

    ) {


        val pulse =

            (sin(animation.toDouble())+1)

                .toFloat()



        paint.style =
            Paint.Style.STROKE



        paint.strokeWidth =
            4f



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

            45+pulse*8,

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


                else ->
                    blue

            }




        canvas.drawCircle(

            cx,

            cy,

            32f,

            paint

        )




        paint.color =
            Color.WHITE


        paint.textSize =
            18f



        canvas.drawText(

            "AI",

            cx,

            cy+7,

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
                event.x-width/2f


            val dy =
                event.y-height/2f



            if(

                dx*dx+dy*dy < 10000

            ){

                onAIOrbClick?.invoke()

            }

        }



        return true

    }


}
