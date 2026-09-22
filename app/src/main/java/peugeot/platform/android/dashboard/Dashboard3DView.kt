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
    ){

        vehicleData =
            data

        invalidate()

    }




    fun setAIState(
        state: AIState
    ){

        aiState =
            state

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
            min(w,h)*0.20f,
            vehicleData.speedKmh.toFloat(),
            240f,
            "KM/H"
        )



        drawGauge(
            canvas,
            w * 0.75f,
            h * 0.50f,
            min(w,h)*0.20f,
            vehicleData.rpm.toFloat(),
            8000f,
            "RPM"
        )



        drawCenterSpeed(
            canvas,
            w/2f,
            h*0.43f
        )



        drawAIOrb(
            canvas,
            w/2f,
            h*0.70f
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
                    30,
                    50
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



        paint.shader =
            null



        paint.color =
            Color.argb(
                50,
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
        private fun drawHeader(
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
                95f
            ),

            35f,
            35f,

            paint

        )



        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.textSize =
            26f


        paint.color =
            Color.WHITE



        canvas.drawText(

            "PEUGEOT  •  VEHICLE OS",

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
    ){


        val progress =
            (value/maxValue)
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



        paint.style =
            Paint.Style.STROKE



        paint.strokeCap =
            Paint.Cap.ROUND



        // outer glass ring

        paint.strokeWidth =
            25f


        paint.color =
            Color.argb(
                80,
                120,
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





        // blue progress

        paint.strokeWidth =
            9f


        paint.color =
            blue



        canvas.drawArc(

            rect,

            135f,

            270f*progress,

            false,

            paint

        )







        // ticks

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
                    (i*7.5)

                )



            val r1 =
                radius-18f



            val r2 =
                if(i%5==0)

                    radius+5f

                else

                    radius-5f




            canvas.drawLine(

                cx + cos(angle).toFloat()*r1,

                cy + sin(angle).toFloat()*r1,

                cx + cos(angle).toFloat()*r2,

                cy + sin(angle).toFloat()*r2,

                paint

            )


        }






        // needle

        val needle =
            Math.toRadians(

                135.0 +
                270.0*progress

            )



        paint.strokeWidth =
            5f


        paint.color =
            Color.WHITE



        canvas.drawLine(

            cx,

            cy,

            cx + cos(needle).toFloat() *
                    (radius-35f),


            cy + sin(needle).toFloat() *
                    (radius-35f),


            paint

        )






        // center

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






        // value

        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.textSize =
            radius*0.25f



        paint.color =
            Color.WHITE



        canvas.drawText(

            value.toInt().toString(),

            cx,

            cy+25f,

            paint

        )




        paint.textSize =
            18f



        paint.color =
            Color.CYAN



        canvas.drawText(

            unit,

            cx,

            cy+radius*0.55f,

            paint

        )


    }







    private fun drawCenterSpeed(
        canvas: Canvas,
        cx: Float,
        cy: Float
    ){


        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.color =
            Color.WHITE



        paint.textSize =
            72f



        canvas.drawText(

            vehicleData.speedKmh.toString(),

            cx,

            cy,

            paint

        )



        paint.textSize =
            18f



        paint.color =
            blue



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
        (System.currentTimeMillis() % 2000L)
            .toFloat() / 2000f


    val size =
        60f + pulse * 15f



    // Energy Ring

    paint.style =
        Paint.Style.STROKE


    paint.strokeWidth =
        8f


    paint.color =
        when(aiState){

            AIState.LISTENING ->
                Color.GREEN


            AIState.THINKING ->
                Color.YELLOW


            AIState.SPEAKING ->
                Color.CYAN


            AIState.ERROR ->
                Color.RED


            else ->
                Color.BLUE

        }



    canvas.drawCircle(

        cx,

        cy,

        size + 35f,

        paint

    )





    // Glow outer

    paint.style =
        Paint.Style.FILL


    paint.color =
        Color.argb(

            80,

            0,

            170,

            255

        )


    canvas.drawCircle(

        cx,

        cy,

        size + 20f,

        paint

    )





    // Main Orb

    paint.color =
        Color.rgb(

            0,

            140,

            255

        )


    canvas.drawCircle(

        cx,

        cy,

        size,

        paint

    )





    // AI Text

    paint.textAlign =
        Paint.Align.CENTER


    paint.typeface =
        Typeface.DEFAULT_BOLD


    paint.color =
        Color.WHITE


    paint.textSize =
        28f



    canvas.drawText(

        "AI",

        cx,

        cy + 8f,

        paint

    )






    // State Text

    paint.textSize =
        13f



    paint.color =
        when(aiState){

            AIState.LISTENING ->
                Color.GREEN


            AIState.THINKING ->
                Color.YELLOW


            AIState.SPEAKING ->
                Color.CYAN


            AIState.ERROR ->
                Color.RED


            else ->
                Color.WHITE

        }



    val stateText =

        when(aiState){

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

        cy + 35f,

        paint

    )

}







    private fun drawCards(
        canvas: Canvas,
        w: Float,
        h: Float
    ){


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



        val start =
            (w -
                    items.size*width
                    )/2f





        for(
            i in items.indices
        ){


            val x =
                start+i*width



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

                    x+5f,

                    h-90f,

                    x+width-5f,

                    h-30f

                ),

                25f,

                25f,

                paint

            )



            paint.textAlign =
                Paint.Align.CENTER


            paint.textSize =
                16f



            paint.typeface =
                Typeface.DEFAULT_BOLD



            paint.color =
                Color.WHITE



            canvas.drawText(

                items[i],

                x+width/2f,

                h-52f,

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


           val dx =
    event.x - width / 2f


val dy =
    event.y - height * 0.70f



            val distance =
                kotlin.math.sqrt(
                    dx*dx+dy*dy
                )



            if(
                distance < 130f
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
