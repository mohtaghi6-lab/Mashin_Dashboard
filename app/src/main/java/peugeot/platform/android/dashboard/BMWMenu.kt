package peugeot.platform.android.dashboard

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


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
            ((String) -> Unit)? = null


    private var downX = 0f
    private var downY = 0f


    private val swipeLimit =
        120f



    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        val cx =
            width / 2f

        val cy =
            height / 2f


        val radius =
            minOf(width, height) * 0.28f



        // background glass

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                70,
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



        // outer ring

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            5f

        paint.color =
            Color.CYAN


        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )



        // menu items

        val itemRadius =
            radius - 60f


        for (i in items.indices) {


            val angle =
                Math.toRadians(
                    (-90 + i * 60).toDouble()
                )


            val x =
                cx + cos(angle).toFloat() * itemRadius


            val y =
                cy + sin(angle).toFloat() * itemRadius



            if (i == selected) {

                paint.style =
                    Paint.Style.FILL

                paint.color =
                    Color.argb(
                        100,
                        0,
                        170,
                        255
                    )

                canvas.drawCircle(
                    x,
                    y,
                    38f,
                    paint
                )
            }



            paint.textAlign =
                Paint.Align.CENTER


            paint.typeface =
                Typeface.DEFAULT_BOLD


            paint.textSize =
                if(i == selected)
                    20f
                else
                    16f



            paint.color =
                if(i == selected)
                    Color.CYAN
                else
                    Color.WHITE



            canvas.drawText(
                items[i],
                x,
                y + 6f,
                paint
            )
        }



        // AI center


        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.rgb(
                0,
                130,
                240
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
            26f


        paint.color =
            Color.WHITE


        paint.typeface =
            Typeface.DEFAULT_BOLD


        canvas.drawText(
            "AI",
            cx,
            cy + 9f,
            paint
        )



        paint.textSize =
            12f


        paint.color =
            Color.CYAN


        canvas.drawText(
            items[selected],
            cx,
            cy + 85f,
            paint
        )

    }





    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {


        when(event.action){


            MotionEvent.ACTION_DOWN -> {

                downX =
                    event.x

                downY =
                    event.y

                return true
            }



            MotionEvent.ACTION_UP -> {


                val diffX =
                    event.x - downX


                val diffY =
                    event.y - downY



                // swipe handling

                if(
                    kotlin.math.abs(diffX) >
                    kotlin.math.abs(diffY)
                    &&
                    kotlin.math.abs(diffX) >
                    swipeLimit
                ){

                    performClick()

                    return true
                }




                val cx =
                    width / 2f


                val cy =
                    height / 2f



                val dx =
                    event.x - cx


                val dy =
                    event.y - cy



                val distance =
                    sqrt(
                        dx * dx +
                                dy * dy
                    )



                // AI button

                if(distance < 70f){

                    selected = 5

                    invalidate()

                    onMenuClick?.invoke(
                        "AI"
                    )

                    performClick()

                    return true
                }




                val angle =
                    Math.toDegrees(
                        atan2(
                            dy,
                            dx
                        ).toDouble()
                    )



                var index =
                    (((angle + 90 + 360) % 360) / 60)
                        .toInt()



                if(index >= items.size){

                    index =
                        items.size - 1
                }



                selected =
                    index


                invalidate()



                onMenuClick?.invoke(
                    items[selected]
                )



                performClick()

                return true
            }
        }


        return true
    }





    override fun performClick(): Boolean {

        super.performClick()

        return true
    }

}
