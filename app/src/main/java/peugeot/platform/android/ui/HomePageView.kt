package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.vehicle.VehicleData
import kotlin.math.sin


class HomePageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var vehicleData =
        VehicleData.demo()


    private var pulse =
        0f


    private val blue =
        Color.rgb(
            70,
            190,
            255
        )



    fun setVehicleData(
        data: VehicleData
    ) {

        vehicleData =
            data

        invalidate()

    }



    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        val width =
            this.width.toFloat()


        val height =
            this.height.toFloat()



        canvas.drawColor(
            Color.rgb(
                3,
                7,
                13
            )
        )



        drawHeader(
            canvas,
            width
        )


        drawAIOrb(
            canvas,
            width / 2f,
            height / 2f - 80f
        )


        drawVehicleInfo(
            canvas,
            width,
            height
        )


        drawCards(
            canvas,
            width,
            height
        )


        pulse += 0.04f


        postInvalidateOnAnimation()

    }
        private fun drawHeader(
        canvas: Canvas,
        width: Float
    ) {


        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.color =
            Color.WHITE


        paint.textSize =
            30f



        canvas.drawText(
            "PEUGEOT VEHICLE OS",
            width / 2f,
            60f,
            paint
        )

    }




    private fun drawAIOrb(
        canvas: Canvas,
        x: Float,
        y: Float
    ) {


        val wave =
            ((sin(pulse.toDouble()) + 1) / 2)
                .toFloat()



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            3f + wave * 4f


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
            80f + wave * 12f,
            paint
        )



        paint.style =
            Paint.Style.FILL


        paint.color =
            blue



        canvas.drawCircle(
            x,
            y,
            50f,
            paint
        )



        paint.color =
            Color.WHITE


        paint.textAlign =
            Paint.Align.CENTER


        paint.textSize =
            26f



        canvas.drawText(
            "AI",
            x,
            y + 9f,
            paint
        )

    }




    private fun drawVehicleInfo(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {


        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.WHITE


        paint.textSize =
            55f



        canvas.drawText(
            "${vehicleData.speedKmh}",
            width / 2f,
            height / 2f + 80f,
            paint
        )



        paint.color =
            blue


        paint.textSize =
            18f



        canvas.drawText(
            "km/h",
            width / 2f,
            height / 2f + 115f,
            paint
        )



        paint.color =
            Color.LTGRAY


        paint.textSize =
            16f



        canvas.drawText(
            "RPM : ${vehicleData.rpm}",
            width / 2f,
            height / 2f + 150f,
            paint
        )



        canvas.drawText(
            "TEMP : ${vehicleData.engineTempC} °C",
            width / 2f,
            height / 2f + 180f,
            paint
        )

    }
        private fun drawCards(
        canvas: Canvas,
        width: Float,
        height: Float
    ) {


        drawGlassCard(
            canvas,
            40f,
            height - 180f,
            width / 2f - 20f,
            height - 60f,
            "BATTERY",
            "${vehicleData.batteryVoltage} V"
        )



        drawGlassCard(
            canvas,
            width / 2f + 20f,
            height - 180f,
            width - 40f,
            height - 60f,
            "CAN",
            if(vehicleData.canConnected)
                "CONNECTED"
            else
                "WAITING"
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
    ) {


        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                120,
                25,
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



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            2f


        paint.color =
            blue



        canvas.drawRoundRect(
            left,
            top,
            right,
            bottom,
            25f,
            25f,
            paint
        )



        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.WHITE


        paint.textSize =
            16f



        canvas.drawText(
            title,
            (left + right) / 2f,
            top + 35f,
            paint
        )



        paint.color =
            blue


        paint.textSize =
            18f



        canvas.drawText(
            value,
            (left + right) / 2f,
            top + 75f,
            paint
        )

    }




    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {


        if(
            event.action ==
            MotionEvent.ACTION_UP
        ) {

            return true

        }


        return true

    }

}
