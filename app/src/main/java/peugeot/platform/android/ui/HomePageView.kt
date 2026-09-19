package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.sin


class HomePageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var pulse =
        0f



    private val blue =
        Color.rgb(
            70,
            190,
            255
        )



    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        val w =
            width.toFloat()

        val h =
            height.toFloat()



        canvas.drawColor(
            Color.rgb(
                2,
                7,
                14
            )
        )



        pulse += 0.05f



        drawHeader(
            canvas,
            w
        )


        drawAIOrb(
            canvas,
            w/2,
            h/2-40
        )


        drawCards(
            canvas,
            w,
            h
        )


        postInvalidateOnAnimation()

    }




    private fun drawHeader(
        canvas: Canvas,
        width: Float
    ){

        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.color =
            Color.WHITE


        paint.textSize =
            32f



        canvas.drawText(
            "PEUGEOT VEHICLE OS",
            width/2,
            70f,
            paint
        )



        paint.color =
            blue


        paint.textSize =
            18f



        canvas.drawText(
            "BMW LUXURY MODE",
            width/2,
            105f,
            paint
        )

    }




    private fun drawAIOrb(
        canvas: Canvas,
        x: Float,
        y: Float
    ){

        val glow =
            ((sin(pulse.toDouble())+1)/2)
                .toFloat()



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            4f + glow*4



        paint.color =
            Color.argb(
                150,
                70,
                190,
                255
            )


        canvas.drawCircle(
            x,
            y,
            90f + glow*12,
            paint
        )



        paint.style =
            Paint.Style.FILL


        paint.color =
            blue


        canvas.drawCircle(
            x,
            y,
            55f,
            paint
        )



        paint.color =
            Color.WHITE


        paint.textSize =
            26f


        paint.textAlign =
            Paint.Align.CENTER


        canvas.drawText(
            "AI",
            x,
            y+8,
            paint
        )

    }




    private fun drawCards(
        canvas: Canvas,
        width: Float,
        height: Float
    ){

        drawCard(
            canvas,
            50f,
            height-210f,
            width/2-20f,
            height-80f,
            "MUSIC",
            "Bluetooth"
        )


        drawCard(
            canvas,
            width/2+20f,
            height-210f,
            width-50f,
            height-80f,
            "NAVIGATION",
            "Ready"
        )

    }




    private fun drawCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        title: String,
        value: String
    ){


        paint.color =
            Color.argb(
                120,
                20,
                35,
                55
            )


        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            25f,
            25f,
            paint
        )



        paint.color =
            Color.WHITE


        paint.textSize =
            18f


        paint.textAlign =
            Paint.Align.CENTER



        canvas.drawText(
            title,
            (left+right)/2,
            top+40,
            paint
        )


        paint.color =
            blue


        paint.textSize =
            16f


        canvas.drawText(
            value,
            (left+right)/2,
            top+75,
            paint
        )

    }



    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {

        return true

    }

}
