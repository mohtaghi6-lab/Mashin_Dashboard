package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View


class VehicleSettingsPageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var canMode =
        "DEMO MODE"


    private var steeringStatus =
        "NOT CONFIGURED"



    private val buttons =
        arrayOf(
            "VOL +",
            "VOL -",
            "NEXT",
            "PREV",
            "VOICE",
            "CALL"
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
            Color.rgb(3,7,13)
        )



        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.color =
            Color.WHITE


        paint.textSize =
            30f



        canvas.drawText(
            "VEHICLE SETTINGS",
            w/2,
            70f,
            paint
        )



        drawCard(
            canvas,
            40f,
            120f,
            w-40f,
            210f,
            "CAN STATUS",
            canMode
        )



        drawCard(
            canvas,
            40f,
            240f,
            w-40f,
            330f,
            "STEERING WHEEL",
            steeringStatus
        )



        paint.color =
            Color.rgb(70,190,255)


        paint.textSize =
            20f


        canvas.drawText(
            "Button Mapping",
            w/2,
            390f,
            paint
        )



        var y =
            430f



        for(button in buttons){


            drawButton(
                canvas,
                button,
                w/2,
                y
            )


            y += 55f

        }

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


        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.rgb(10,20,32)



        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            20f,
            20f,
            paint
        )



        paint.color =
            Color.WHITE


        paint.textSize =
            16f



        canvas.drawText(
            title,
            (left+right)/2,
            top+30,
            paint
        )



        paint.color =
            Color.rgb(80,255,150)


        paint.textSize =
            18f



        canvas.drawText(
            value,
            (left+right)/2,
            top+65,
            paint
        )

    }




    private fun drawButton(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float
    ){

        paint.color =
            Color.rgb(20,120,220)


        canvas.drawRoundRect(
            x-120,
            y-22,
            x+120,
            y+22,
            20f,
            20f,
            paint
        )


        paint.color =
            Color.WHITE


        paint.textSize =
            17f


        canvas.drawText(
            text,
            x,
            y+6,
            paint
        )

    }




    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {


        if(
            event.action ==
            MotionEvent.ACTION_UP
        ){

            if(
                event.y > 400
            ){

                steeringStatus =
                    "CONFIGURED"


                invalidate()

                return true
            }

        }


        return true
    }

}
