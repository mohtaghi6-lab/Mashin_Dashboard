package peugeot.platform.android.dashboard


import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View



class BMWMenu(
    context: Context
) : View(context) {



    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)



    private val blue =
        Color.rgb(
            0,
            170,
            255
        )



    private val items =
        arrayOf(

            "CAR",

            "MUSIC",

            "AI",

            "PHONE",

            "NAVI",

            "SCAN"

        )



    private var selected =
        2



    var onMenuClick:
            ((String)->Unit)? = null






    override fun onDraw(
        canvas: Canvas
    ){

        super.onDraw(canvas)



        val w =
            width.toFloat()



        val h =
            height.toFloat()



        paint.textAlign =
            Paint.Align.CENTER



        for(
            i in items.indices
        ){



            val angle =
                Math.toRadians(
                    (
                        i * 60 - 90

                    ).toDouble()

                )



            val radius =
                150f



            val x =
                w/2f +
                kotlin.math.cos(angle).toFloat()
                *
                radius



            val y =
                h/2f +
                kotlin.math.sin(angle).toFloat()
                *
                radius





            // شیشه منو

            paint.style =
                Paint.Style.FILL



            paint.color =
                if(i == selected)

                    Color.argb(
                        170,
                        0,
                        170,
                        255
                    )

                else

                    Color.argb(
                        80,
                        255,
                        255,
                        255
                    )





            canvas.drawCircle(

                x,

                y,

                55f,

                paint

            )






            // متن

            paint.color =
                Color.WHITE



            paint.textSize =
                16f



            canvas.drawText(

                items[i],

                x,

                y+6,

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


            val w =
                width.toFloat()



            val h =
                height.toFloat()



            for(
                i in items.indices
            ){


                val angle =
                    Math.toRadians(
                        (
                            i*60-90

                        ).toDouble()
                    )



                val x =
                    w/2f +
                    kotlin.math.cos(angle).toFloat()
                    *
                    150f



                val y =
                    h/2f +
                    kotlin.math.sin(angle).toFloat()
                    *
                    150f



                val dx =
                    event.x-x



                val dy =
                    event.y-y



                if(
                    dx*dx+dy*dy < 6000
                ){

                    selected = i

                    invalidate()


                    onMenuClick?.invoke(
                        items[i]
                    )


                }


            }

        }


        return true

    }


}
