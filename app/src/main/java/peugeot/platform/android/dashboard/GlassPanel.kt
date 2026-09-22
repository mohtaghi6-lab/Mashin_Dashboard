package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.View


class GlassPanel(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)



    private val rect =
        RectF()



    var cornerRadius =
        35f



    var glassAlpha =
        90



    override fun onDraw(
        canvas: Canvas
    ){

        super.onDraw(canvas)



        val w =
            width.toFloat()


        val h =
            height.toFloat()



        rect.set(

            0f,

            0f,

            w,

            h

        )



        // Glass background

        paint.style =
            Paint.Style.FILL



        paint.color =
            Color.argb(

                glassAlpha,

                255,

                255,

                255

            )



        canvas.drawRoundRect(

            rect,

            cornerRadius,

            cornerRadius,

            paint

        )





        // Glass border

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

            rect,

            cornerRadius,

            cornerRadius,

            paint

        )





        // Inner glow

        paint.style =
            Paint.Style.FILL



        paint.color =
            Color.argb(

                35,

                0,

                170,

                255

            )



        canvas.drawRoundRect(

            RectF(

                8f,

                8f,

                w-8f,

                h-8f

            ),

            cornerRadius,

            cornerRadius,

            paint

        )


    }

}
