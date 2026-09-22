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
        Color.rgb(
            0,
            170,
            255
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



        drawLuxuryBackground(
            canvas,
            w,
            h
        )



        drawTopBar(
            canvas,
            w
        )



        drawGauge(
            canvas,
            w * 0.27f,
            h * 0.52f,
            min(w,h) * 0.19f,
            vehicleData.speedKmh.toFloat(),
            240f,
            "km/h"
        )



        drawGauge(
            canvas,
            w * 0.73f,
            h * 0.52f,
            min(w,h) * 0.19f,
            vehicleData.rpm.toFloat(),
            8000f,
            "RPM"
        )



        drawDigitalCenter(
            canvas,
            w / 2f,
            h * 0.48f
        )



        drawAIOrb(
            canvas,
            w / 2f,
            h * 0.72f
        )



        drawBottomCards(
            canvas,
            w,
            h
        )



        postInvalidateDelayed(
            40
        )


    }




    private fun drawLuxuryBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ){

        paint.style =
            Paint.Style.FILL


        paint.shader =
            LinearGradient(

                0f,
                0f,

                0f,
                h,

                Color.rgb(
                    2,
                    5,
                    12
                ),

                Color.rgb(
                    10,
                    25,
                    40
                ),

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



        paint.color =
            Color.argb(
                45,
                0,
                170,
                255
            )


        canvas.drawCircle(
            w/2f,
            h/2f,
            min(w,h)*0.45f,
            paint
        )


    }





    private fun drawTopBar(
        canvas: Canvas,
        w: Float
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

            RectF(
                40f,
                25f,
                w-40f,
                90f
            ),

            30f,
            30f,

            paint

        )


        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.WHITE


        paint.textSize =
            28f


        paint.typeface =
            Typeface.DEFAULT_BOLD


        canvas.drawText(

            "PEUGEOT VEHICLE OS  •  BMW MODE",

            w/2f,

            68f,

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
                cx-radius,
                cy-radius,
                cx+radius,
                cy+radius
            )



        // Outer glass ring

        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            26f


        paint.strokeCap =
            Paint.Cap.ROUND


        paint.color =
            Color.argb(
                50,
                180,
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



        // Active blue ring

        paint.strokeWidth =
            10f


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
            3f


        paint.color =
            Color.WHITE



        for(
            i in 0..36
        ){

            val angle =
                Math.toRadians(
                    135.0 +
                    (i * 7.5)
                )


            val outer =
                radius + 5f


            val inner =
                if(
                    i % 5 == 0
                )
                    radius - 22f
                else
                    radius - 10f



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




        // Needle

        val needleAngle =
            Math.toRadians(

                135.0 +
                (270.0 * progress)

            )


        paint.color =
            Color.WHITE


        paint.strokeWidth =
            6f



        canvas.drawLine(

            cx,

            cy,


            cx +
                cos(needleAngle).toFloat()
                *
                (radius-35f),


            cy +
                sin(needleAngle).toFloat()
                *
                (radius-35f),


            paint

        )




        // Center

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




        // Number

        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.WHITE


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.textSize =
            radius * 0.28f



        canvas.drawText(

            value.toInt()
                .toString(),

            cx,

            cy + 25f,

            paint

        )




        paint.textSize =
            20f


        paint.color =
            blue



        canvas.drawText(

            unit,

            cx,

            cy + radius*0.55f,

            paint

        )

    }







    private fun drawDigitalCenter(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ){



        paint.style =
            Paint.Style.FILL



        paint.textAlign =
            Paint.Align.CENTER



        paint.typeface =
            Typeface.DEFAULT_BOLD



        paint.color =
            Color.WHITE



        paint.textSize =
            70f



        canvas.drawText(

            vehicleData.speedKmh
                .toString(),

            cx,

            cy,

            paint

        )



        paint.textSize =
            20f



        paint.color =
            Color.CYAN



        canvas.drawText(

            "CURRENT SPEED",

            cx,

            cy+35f,

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
                    System.currentTimeMillis()
                    % 2000
                    ).toFloat()
                    /
                    2000f



        val size =
            65f +
            pulse*15f



        paint.style =
            Paint.Style.FILL



        paint.color =
            Color.argb(
                70,
                0,
                180,
                255
            )



        canvas.drawCircle(

            cx,

            cy,

            size+35f,

            paint

        )



        paint.color =
            Color.rgb(
                0,
                150,
                255
            )



        canvas.drawCircle(

            cx,

            cy,

            size,

            paint

        )



        paint.color =
            Color.WHITE


        paint.textAlign =
            Paint.Align.CENTER



        paint.textSize =
            24f


        paint.typeface =
            Typeface.DEFAULT_BOLD



        canvas.drawText(

            "AI",

            cx,

            cy+8f,

            paint

        )



        paint.textSize =
            13f



        canvas.drawText(

            when(aiState){

                AIState.LISTENING ->
                    "LISTENING"


                AIState.THINKING ->
                    "THINKING"


                AIState.SPEAKING ->
                    "SPEAKING"


                AIState.ERROR ->
                    "ERROR"


                else ->
                    "READY"

            },

            cx,

            cy+35f,

            paint

        )

    }
        private fun drawBottomCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ) {


        val names =
            arrayOf(
                "CAR",
                "MUSIC",
                "NAVI",
                "PHONE",
                "SCAN"
            )



        val cardWidth =
            150f


        val startX =
            (w -
                    (names.size * cardWidth)
                    ) / 2f



        for(
            i in names.indices
        ){


            val left =
                startX +
                (i * cardWidth)



            paint.style =
                Paint.Style.FILL



            paint.color =
                Color.argb(
                    90,
                    255,
                    255,
                    255
                )



            canvas.drawRoundRect(

                RectF(

                    left,
                    h-110f,

                    left+120f,
                    h-45f

                ),

                25f,

                25f,

                paint

            )



            paint.textAlign =
                Paint.Align.CENTER



            paint.color =
                Color.WHITE



            paint.textSize =
                18f



            paint.typeface =
                Typeface.DEFAULT_BOLD



            canvas.drawText(

                names[i],

                left+60f,

                h-70f,

                paint

            )


        }


    }






    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {


        if(
            event.action ==
            MotionEvent.ACTION_UP
        ){


            val x =
                event.x


            val y =
                event.y



            val cx =
                width/2f



            val cy =
                height*0.72f



            val distance =

                kotlin.math.sqrt(

                    (x-cx)*(x-cx) +

                    (y-cy)*(y-cy)

                )



            if(
                distance < 120f
            ){

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
