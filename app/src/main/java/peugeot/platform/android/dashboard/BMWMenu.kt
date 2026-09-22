package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


class BMWMenu(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)



    private val items =
        arrayOf(

            "CAR",
            "MUSIC",
            "NAVI",
            "PHONE",
            "SCAN",
            "AI"

        )



    private var selected =
        0



    var onMenuClick:
            ((String)->Unit)? = null





    override fun onDraw(
        canvas: Canvas
    ) {


        super.onDraw(canvas)



        val cx =
            width/2f


        val cy =
            height/2f



        val radius =
            170f



        // background glass circle


        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                80,
                255,
                255,
                255
            )


        canvas.drawCircle(

            cx,

            cy,

            radius,

            paint

        )



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            3f


        paint.color =
            Color.CYAN



        canvas.drawCircle(

            cx,

            cy,

            radius,

            paint

        )




        // center AI


        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.rgb(
                0,
                140,
                255
            )


        canvas.drawCircle(

            cx,

            cy,

            55f,

            paint

        )



        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            24f


        paint.color =
            Color.WHITE



        canvas.drawText(

            "AI",

            cx,

            cy+8f,

            paint

        )





        // menu items


        for(
            i in items.indices
        ){


            val angle =

                Math.toRadians(

                    (-90 + i*60).toDouble()

                )



            val x =

                cx +

                cos(angle).toFloat()

                *

                120f



            val y =

                cy +

                sin(angle).toFloat()

                *

                120f





            paint.color =

                if(i == selected)

                    Color.CYAN

                else

                    Color.WHITE




            paint.textSize =
                20f



            canvas.drawText(

                items[i],

                x,

                y,

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


            val cx =
                width/2f


            val cy =
                height/2f



            val angle =

                Math.toDegrees(

                    atan2(

                        event.y-cy,

                        event.x-cx

                    ).toDouble()

                )



            var index =

                (((angle+90+360)%360)/60)

                    .toInt()



            if(index >= items.size)

                index = items.size-1



            selected =
                index



            invalidate()



            onMenuClick?.invoke(

                items[selected]

            )



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
