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



    private var aiX =
        0f



    private var aiY =
        0f



    var onAIOrbClick:
            (() -> Unit)? = null






    private val blue =
        Color.rgb(
            0,
            170,
            255
        )



    private val background =
        Color.rgb(
            2,
            7,
            15
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
            35f,
            95f,
            w - 35f,
            h - 55f
        )





        vehicleRenderer.drawVehicle(

            canvas,

            w / 2f,

            h * 0.38f,

            1.15f

        )





        drawGauge(

            canvas,

            w * 0.25f,

            h * 0.46f,

            135f,

            data.rpm.toFloat(),

            8000f,

            "RPM"

        )





        drawGauge(

            canvas,

            w * 0.75f,

            h * 0.46f,

            135f,

            data.speedKmh.toFloat(),

            240f,

            "KM/H"

        )





        warningLights.draw(

            canvas,

            w / 2f - 150f,

            h * 0.79f

        )





        drawInfo(

            canvas,

            w,

            h

        )





        aiX =
            w / 2f



        aiY =
            h * 0.68f





        drawAIOrb(

            canvas,

            aiX,

            aiY

        )





        animation += 0.04f


        postInvalidateOnAnimation()

    }








    private fun drawHeader(

        canvas: Canvas,

        w: Float

    ){



        paint.textAlign =
            Paint.Align.CENTER



        paint.typeface =
            Typeface.DEFAULT_BOLD



        paint.color =
            Color.WHITE



        paint.textSize =
            28f




        canvas.drawText(

            "PEUGEOT VEHICLE OS",

            w / 2f,

            55f,

            paint

        )





        paint.textSize =
            14f



        paint.color =
            blue





        canvas.drawText(

            "INTELLIGENT LUXURY DASHBOARD",

            w / 2f,

            78f,

            paint

        )


    }
        private fun drawGlassPanel(

        canvas: Canvas,

        left: Float,

        top: Float,

        right: Float,

        bottom: Float

    ){


        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                80,
                255,
                255,
                255
            )



        canvas.drawRoundRect(

            left,

            top,

            right,

            bottom,

            40f,

            40f,

            paint

        )



        paint.style =
            Paint.Style.STROKE



        paint.strokeWidth =
            2f



        paint.color =
            Color.argb(
                160,
                0,
                170,
                255
            )



        canvas.drawRoundRect(

            left,

            top,

            right,

            bottom,

            40f,

            40f,

            paint

        )



        paint.style =
            Paint.Style.FILL

    }









    private fun drawBackgroundGlow(

        canvas: Canvas,

        w: Float,

        h: Float

    ){



        val glow =
            Paint(
                Paint.ANTI_ALIAS_FLAG
            )



        glow.shader =
            RadialGradient(

                w / 2f,

                h / 2f,

                600f,

                Color.argb(
                    110,
                    0,
                    170,
                    255
                ),

                Color.TRANSPARENT,

                Shader.TileMode.CLAMP

            )



        canvas.drawCircle(

            w / 2f,

            h / 2f,

            600f,

            glow

        )

    }









    private fun drawGauge(

        canvas: Canvas,

        cx: Float,

        cy: Float,

        radius: Float,

        value: Float,

        max: Float,

        label: String

    ){



        val percent =
            (value / max)
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





        // حلقه تیره BMW

        paint.style =
            Paint.Style.STROKE



        paint.strokeWidth =
            16f



        paint.strokeCap =
            Paint.Cap.ROUND



        paint.color =
            Color.rgb(
                25,
                45,
                70
            )



        canvas.drawArc(

            rect,

            135f,

            270f,

            false,

            paint

        )





        // نور آبی مقدار

        paint.color =
            blue



        canvas.drawArc(

            rect,

            135f,

            270f * percent,

            false,

            paint

        )







        // خطوط دور گیج

        paint.strokeWidth =
            2f



        paint.color =
            Color.WHITE



        for(i in 0..12){


            val angle =
                Math.toRadians(

                    (
                        135 +
                        i * 22.5

                    ).toDouble()

                )



            canvas.drawLine(

                cx +
                        cos(angle).toFloat()
                        *
                        (radius - 18),


                cy +
                        sin(angle).toFloat()
                        *
                        (radius - 18),


                cx +
                        cos(angle).toFloat()
                        *
                        (radius - 5),


                cy +
                        sin(angle).toFloat()
                        *
                        (radius - 5),


                paint

            )

        }







        // عقرب

        val angle =
            Math.toRadians(

                (
                    135f +
                    270f * percent

                ).toDouble()

            )



        paint.strokeWidth =
            4f



        paint.color =
            Color.WHITE



        canvas.drawLine(

            cx,

            cy,


            cx +
                    cos(angle).toFloat()
                    *
                    (radius - 20),


            cy +
                    sin(angle).toFloat()
                    *
                    (radius - 20),


            paint

        )







        // مرکز عقربه

        paint.style =
            Paint.Style.FILL



        paint.color =
            Color.WHITE



        canvas.drawCircle(

            cx,

            cy,

            9f,

            paint

        )



        paint.color =
            blue



        canvas.drawCircle(

            cx,

            cy,

            4f,

            paint

        )







        // عدد اصلی

        paint.color =
            Color.WHITE



        paint.textAlign =
            Paint.Align.CENTER



        paint.textSize =
            42f



        canvas.drawText(

            value.toInt().toString(),

            cx,

            cy + 15,

            paint

        )







        paint.textSize =
            18f



        paint.color =
            blue



        canvas.drawText(

            label,

            cx,

            cy + 45,

            paint

        )

    }
        private fun drawInfo(

        canvas: Canvas,

        w: Float,

        h: Float

    ){


        paint.textAlign =
            Paint.Align.CENTER



        paint.textSize =
            16f



        paint.color =
            Color.LTGRAY



        canvas.drawText(

            "ENGINE ${data.engineTempC}°C",

            w * 0.25f,

            h * 0.94f,

            paint

        )



        canvas.drawText(

            "VOLT 13.8V",

            w * 0.50f,

            h * 0.94f,

            paint

        )



        paint.color =
            Color.GREEN



        canvas.drawText(

            "CAN READY",

            w * 0.75f,

            h * 0.94f,

            paint

        )

    }









    private fun drawAIOrb(

        canvas: Canvas,

        cx: Float,

        cy: Float

    ){



        val pulse =

            (
                    sin(animation.toDouble())
                    + 1

            ).toFloat()





        // حلقه بیرونی نور

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

            55f + pulse * 12f,

            paint

        )







        // هسته AI

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



        paint.textAlign =
            Paint.Align.CENTER



        paint.textSize =
            20f



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
                event.x - aiX



            val dy =
                event.y - aiY





            if(

                dx * dx +
                dy * dy <
                9000

            ){

                onAIOrbClick?.invoke()

            }

        }



        return true

    }


}
