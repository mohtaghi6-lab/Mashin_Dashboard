package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.sin


class VehicleSettingsPageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var animation =
        0f


    private var canStatus =
        "CAN : DEMO MODE"


    private var steeringStatus =
        "STEERING : READY"


    private var driveMode =
        "COMFORT"



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
                3,
                8,
                15
            )
        )



        animation += 0.05f



        drawTitle(
            canvas,
            w
        )


        drawGlassCard(
            canvas,
            40f,
            130f,
            w-40f,
            230f,
            "VEHICLE CONNECTION",
            canStatus
        )


        drawGlassCard(
            canvas,
            40f,
            260f,
            w-40f,
            360f,
            "STEERING WHEEL",
            steeringStatus
        )


        drawGlassCard(
            canvas,
            40f,
            390f,
            w-40f,
            490f,
            "DRIVING MODE",
            driveMode
        )


        drawBottomHint(
            canvas,
            w,
            h
        )


        postInvalidateOnAnimation()

    }




    private fun drawTitle(
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
            "VEHICLE SETTINGS",
            width/2,
            70f,
            paint
        )


        paint.color =
            Color.rgb(
                70,
                190,
                255
            )


        paint.textSize =
            16f


        canvas.drawText(
            "PEUGEOT VEHICLE OS",
            width/2,
            100f,
            paint
        )

    }




    private fun drawGlassCard(
        canvas: Canvas,
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        title: String,
        value: String
    ){


        val glow =
            ((sin(animation.toDouble())+1)/2)
                .toFloat()



        paint.style =
            Paint.Style.FILL


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
            30f,
            30f,
            paint
        )


        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            2f + glow


        paint.color =
            Color.argb(
                180,
                70,
                190,
                255
            )


        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            30f,
            30f,
            paint
        )



        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER



        paint.color =
            Color.WHITE


        paint.textSize =
            18f


        canvas.drawText(
            title,
            (left+right)/2,
            top+35,
            paint
        )



        paint.color =
            Color.rgb(
                80,
                255,
                150
            )


        paint.textSize =
            22f


        canvas.drawText(
            value,
            (left+right)/2,
            top+75,
            paint
        )

    }




    private fun drawBottomHint(
        canvas: Canvas,
        width: Float,
        height: Float
    ){

        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.GRAY


        paint.textSize =
            15f


        canvas.drawText(
            "Tap to configure",
            width/2,
            height-40,
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

            if(event.y > 250 &&
                event.y < 380
            ){

                steeringStatus =
                    "STEERING : CONFIGURED"


                invalidate()

            }


            if(event.y > 390 &&
                event.y < 500
            ){

                driveMode =
                    if(
                        driveMode ==
                        "COMFORT"
                    )
                        "SPORT"
                    else
                        "COMFORT"


                invalidate()

            }

        }


        return true
    }

}
