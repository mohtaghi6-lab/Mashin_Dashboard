package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.View


class StartupView(
    context: Context
) : View(context) {



    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)



    private var progress =
        0f



    var onFinished:
            (() -> Unit)? = null





    override fun onDraw(
        canvas: Canvas
    ){

        super.onDraw(canvas)



        val w =
            width.toFloat()


        val h =
            height.toFloat()



        // Background

        canvas.drawColor(
            Color.BLACK
        )



        // AI Glow


        paint.color =
            Color.argb(

                (120 * progress).toInt(),

                0,

                150,

                255

            )



        canvas.drawCircle(

            w/2f,

            h/2f-50f,

            120f * progress,

            paint

        )





        paint.color =
            Color.rgb(

                0,

                160,

                255

            )



        canvas.drawCircle(

            w/2f,

            h/2f-50f,

            55f * progress,

            paint

        )





        // Text


        paint.textAlign =
            Paint.Align.CENTER



        paint.typeface =
            Typeface.DEFAULT_BOLD



        paint.color =
            Color.WHITE



        paint.alpha =
            (255*progress).toInt()



        paint.textSize =
            38f



        canvas.drawText(

            "PEUGEOT",

            w/2f,

            h/2f+80f,

            paint

        )




        paint.textSize =
            24f



        paint.color =
            Color.CYAN



        canvas.drawText(

            "VEHICLE OS v31",

            w/2f,

            h/2f+120f,

            paint

        )



        if(
            progress < 1f
        ){

            progress += 0.02f

            postInvalidateDelayed(
                20
            )

        }
        else {


            postDelayed({

                onFinished?.invoke()


            },800)


        }



    }



}
