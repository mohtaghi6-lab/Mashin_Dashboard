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


    private var selected = 0


    private var downX = 0f
    private var downY = 0f


    private val swipeThreshold = 120f


    var onMenuClick:
            ((String) -> Unit)? = null


    var onSwipeRight:
            (() -> Unit)? = null


    var onSwipeLeft:
            (() -> Unit)? = null



    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        val w =
            width.toFloat()

        val h =
            height.toFloat()


        val cx =
            w / 2f


        val cy =
            h / 2f


        val radius =
            minOf(w, h) * 0.30f



        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                45,
                0,
                25,
                45
            )


        canvas.drawCircle(
            cx,
            cy,
            radius + 35f,
            paint
        )



        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            10f

        paint.color =
            Color.argb(
                35,
                0,
                170,
                255
            )


        canvas.drawCircle(
            cx,
            cy,
            radius + 18f,
            paint
        )



        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                90,
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
            2.5f

        paint.color =
            Color.argb(
                180,
                0,
                190,
                255
            )


        canvas.drawCircle(
            cx,
            cy,
            radius,
            paint
        )



        val itemRadius =
            radius - 55f



        for(
            i in items.indices
        ){

            val angle =
                Math.toRadians(
                    (-90 + i * 60).toDouble()
                )


           val x =
    cx + cos(angle).toFloat() * itemRadius

            val y =
    cy + sin(angle).toFloat() * itemRadius



            val isSelected =
                i == selected



            if(isSelected){

                paint.style =
                    Paint.Style.FILL


                paint.color =
                    Color.argb(
                        70,
                        0,
                        190,
                        255
                    )


                canvas.drawCircle(
                    x,
                    y - 5f,
                    38f,
                    paint
                )



                paint.style =
                    Paint.Style.STROKE


                paint.strokeWidth =
                    3f


                paint.color =
                    Color.CYAN


                canvas.drawCircle(
                    x,
                    y - 5f,
                    34f,
                    paint
                )
            }



            paint.style =
                Paint.Style.FILL


            paint.textAlign =
                Paint.Align.CENTER


            paint.typeface =
                Typeface.DEFAULT_BOLD


            paint.textSize =
                if(isSelected)
                    19f
                else
                    16f



            paint.color =
                if(isSelected)
                    Color.CYAN
                else
                    Color.WHITE



            canvas.drawText(
                items[i],
                x,
                y + 5f,
                paint
            )
        }




        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                55,
                0,
                170,
                255
            )


        canvas.drawCircle(
            cx,
            cy,
            72f,
            paint
        )



        paint.color =
            Color.rgb(
                0,
                125,
                235
            )


        canvas.drawCircle(
            cx,
            cy,
            55f,
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
            55f,
            paint
        )



        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.textSize =
            25f


        paint.color =
            Color.WHITE


        canvas.drawText(
            "AI",
            cx,
            cy + 8f,
            paint
        )



        paint.textSize =
            11f


        paint.color =
            Color.argb(
                190,
                180,
                230,
                255
            )


        canvas.drawText(
            items[selected],
            cx,
            cy + 88f,
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



                // SWIPE

                if(
                    kotlin.math.abs(diffX) >
                    kotlin.math.abs(diffY)
                    &&
                    kotlin.math.abs(diffX) >
                    swipeThreshold
                ){

                    if(diffX > 0){

                        onSwipeRight?.invoke()

                    }else{

                        onSwipeLeft?.invoke()

                    }


                    performClick()

                    return true
                }




                // CLICK MENU


                val cx =
                    width / 2f


                val cy =
                    height / 2f


                val dx =
                    event.x - cx


                val dy =
                    event.y - cy



                val distance =
                    kotlin.math.sqrt(
                        dx * dx +
                                dy * dy
                    )



                if(distance < 65f){

                    selected = 5

                    invalidate()

                    onMenuClick?.invoke(
                        "AI"
                    )

                    performClick()

                    return true
                }



                if(
                    distance < 70f ||
                    distance >
                    minOf(width,height)*0.45f
                ){

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


                var normalized =
                    angle + 90.0


                if(normalized < 0)
                    normalized += 360.0



                val index =
                    (
                            (normalized + 30.0) /
                                    60.0
                            ).toInt() % items.size



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




    override fun performClick():
            Boolean {

        super.performClick()

        return true
    }

}
