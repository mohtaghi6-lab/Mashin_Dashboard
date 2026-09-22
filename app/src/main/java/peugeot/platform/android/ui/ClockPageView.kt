package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.View
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ClockPageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private val timeFormat =
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        )


    private val dateFormat =
        SimpleDateFormat(
            "EEEE, dd MMMM",
            Locale.getDefault()
        )





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



        drawClock(
            canvas,
            w/2f,
            h/2f
        )



        postInvalidateDelayed(
            1000L
        )


    }






    private fun drawBackground(
        canvas: Canvas,
        w: Float,
        h: Float
    ){


        paint.shader =
            LinearGradient(

                0f,
                0f,

                0f,
                h,

                Color.rgb(
                    2,
                    6,
                    15
                ),

                Color.rgb(
                    15,
                    45,
                    70
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
                70,
                0,
                170,
                255
            )


        canvas.drawCircle(

            w/2f,

            h/2f,

            300f,

            paint

        )


    }







    private fun drawClock(
        canvas: Canvas,
        cx: Float,
        cy: Float
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

                cx-250f,

                cy-170f,

                cx+250f,

                cy+170f

            ),

            50f,

            50f,

            paint

        )




        paint.textAlign =
            Paint.Align.CENTER



        paint.typeface =
            Typeface.DEFAULT_BOLD



        paint.color =
            Color.WHITE



        paint.textSize =
            110f



        canvas.drawText(

            timeFormat.format(
                Date()
            ),

            cx,

            cy,

            paint

        )





        paint.typeface =
            Typeface.DEFAULT



        paint.textSize =
            24f



        paint.color =
            Color.CYAN



        canvas.drawText(

            dateFormat.format(
                Date()
            ),

            cx,

            cy+60f,

            paint

        )


    }


}
