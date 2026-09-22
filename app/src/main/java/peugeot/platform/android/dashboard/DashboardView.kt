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


    private val glass =
        Color.argb(
            90,
            255,
            255,
            255
        )



    private val background =
        Color.rgb(
            3,
            8,
            18
        )





    fun setVehicleData(
        value: VehicleData
    ){

        data = value

        invalidate()

    }






    fun setAIState(
        value: AIState
    ){

        aiState = value

        invalidate()

    }







    override fun onDraw(
        canvas: Canvas
    ){

        super.onDraw(canvas)



        val w =
            width.toFloat()


        val h =
            height.toFloat()



        canvas.drawColor(
            background
        )



        drawBackgroundGlow(
            canvas,
            w,
            h
        )



        drawHeader(
            canvas,
            w
        )



        drawGlassPanel(
            canvas,
            40f,
            90f,
            w-40f,
            h-80f
        )



        vehicleRenderer.drawVehicle(

            canvas,

            w/2f,

            h*0.37f,

            1.15f

        )





        drawGauge(

            canvas,

            w*0.25f,

            h*0.45f,

            130f,

            data.rpm.toFloat(),

            8000f,

            "RPM"

        )





        drawGauge(

            canvas,

            w*0.75f,

            h*0.45f,

            130f,

            data.speedKmh.toFloat(),

            240f,

            "KM/H"

        )






        warningLights.draw(

            canvas,

            w/2f-150f,

            h*0.78f

        )






        drawInfo(

            canvas,

            w,

            h

        )






        drawAIOrb(

            canvas,

            w/2f,

            h*0.67f

        )





        animation +=0.04f

        postInvalidateOnAnimation()

    }









    private fun drawHeader(
        canvas: Canvas,
        w:Float
    ){

        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            28f


        paint.color =
            Color.WHITE



        canvas.drawText(

            "PEUGEOT VEHICLE OS",

            w/2f,

            55f,

            paint

        )



        paint.textSize =
            14f


        paint.color =
            blue



        canvas.drawText(

            "BMW STYLE INTELLIGENT DASHBOARD",

            w/2f,

            78f,

            paint

        )


    }









    private fun drawGlassPanel(

        canvas:Canvas,

        left:Float,

        top:Float,

        right:Float,

        bottom:Float

    ){



        paint.style =
            Paint.Style.FILL


        paint.color =
            glass



        canvas.drawRoundRect(

            left,

            top,

            right,

            bottom,

            35f,

            35f,

            paint

        )



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            2f


        paint.color =
            Color.argb(
                120,
                0,
                170,
                255
            )


        canvas.drawRoundRect(

            left,

            top,

            right,

            bottom,

            35f,

            35f,

            paint

        )



        paint.style =
            Paint.Style.FILL

    }









    private fun drawBackgroundGlow(

        canvas:Canvas,

        w:Float,

        h:Float

    ){

        val glow =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            )


        glow.shader =
            RadialGradient(

                w/2f,

                h/2f,

                500f,

                Color.argb(
                    90,
                    0,
                    150,
                    255
                ),

                Color.TRANSPARENT,

                Shader.TileMode.CLAMP

            )


        canvas.drawCircle(

            w/2f,

            h/2f,

            500f,

            glow

        )

    }









    private fun drawGauge(

        canvas:Canvas,

        cx:Float,

        cy:Float,

        radius:Float,

        value:Float,

        max:Float,

        label:String

    ){


        val percent =
            (value/max)
                .coerceIn(0f,1f)



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            14f


        paint.strokeCap =
            Paint.Cap.ROUND



        paint.color =
            Color.rgb(
                20,
                50,
                80
            )


        canvas.drawArc(

            RectF(
                cx-radius,
                cy-radius,
                cx+radius,
                cy+radius
            ),

            135f,

            270f,

            false,

            paint

        )



        paint.color =
            blue



        canvas.drawArc(

            RectF(
                cx-radius,
                cy-radius,
                cx+radius,
                cy+radius
            ),

            135f,

            270f*percent,

            false,

            paint

        )





        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.WHITE


        paint.textSize =
            45f


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

            label,

            cx,

            cy+40,

            paint

        )



    }









    private fun drawInfo(

        canvas:Canvas,

        w:Float,

        h:Float

    ){



        paint.textSize =
            16f



        paint.color =
            Color.LTGRAY



        canvas.drawText(

            "ENGINE ${data.engineTempC}°C",

            w*.25f,

            h*.94f,

            paint

        )



        canvas.drawText(

            "VOLT 13.8V",

            w*.5f,

            h*.94f,

            paint

        )



        paint.color =
            Color.GREEN



        canvas.drawText(

            "CAN READY",

            w*.75f,

            h*.94f,

            paint

        )



    }









    private fun drawAIOrb(

        canvas:Canvas,

        cx:Float,

        cy:Float

    ){



        val pulse =
            ((sin(animation.toDouble())+1)
                    .toFloat())



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            6f



        paint.color =
            Color.argb(

                180,

                0,

                170,

                255

            )



        canvas.drawCircle(

            cx,

            cy,

            55f+pulse*10,

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

            35f,

            paint

        )



        paint.color =
            Color.WHITE


        paint.textSize =
            20f


        canvas.drawText(

            "AI",

            cx,

            cy+7,

            paint

        )

    }









    override fun onTouchEvent(

        event:MotionEvent

    ):Boolean{


        if(event.action ==
            MotionEvent.ACTION_UP){



            val dx =
                event.x-width/2f


            val dy =
                event.y-height*.67f



            if(dx*dx+dy*dy < 8000){

                onAIOrbClick?.invoke()

            }


        }


        return true

    }


}
