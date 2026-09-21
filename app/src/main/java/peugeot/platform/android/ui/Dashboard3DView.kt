package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.View
import peugeot.platform.android.vehicle.VehicleData
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


class Dashboard3DView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var vehicleData =
        VehicleData.demo()



    fun setVehicleData(
        data: VehicleData
    ) {

        vehicleData = data

        invalidate()
    }



    override fun onDraw(
        canvas: Canvas
    ) {

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


        drawGauge(
            canvas,
            w * 0.28f,
            h * 0.50f,
            min(w,h) * 0.22f,
            vehicleData.speedKmh.toFloat(),
            240f,
            "km/h"
        )


        drawGauge(
            canvas,
            w * 0.72f,
            h * 0.50f,
            min(w,h) * 0.22f,
            vehicleData.rpm.toFloat(),
            8000f,
            "RPM"
        )


        postInvalidateDelayed(40)

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
                Color.BLACK,
                Color.rgb(5,18,32),
                Shader.TileMode.CLAMP
            )


        canvas.drawRect(
            0f,
            0f,
            w,
            h,
            paint
        )


        paint.shader = null


        paint.color =
            Color.argb(
                60,
                40,
                160,
                255
            )


        canvas.drawCircle(
            w/2,
            h/2,
            min(w,h)*0.45f,
            paint
        )

    }





    private fun drawGauge(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        value: Float,
        max: Float,
        unit: String
    ){

        val progress =
            (value/max)
                .coerceIn(0f,1f)



        val rect =
            RectF(
                cx-radius,
                cy-radius,
                cx+radius,
                cy+radius
            )



        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            20f


        paint.strokeCap =
            Paint.Cap.ROUND


        paint.color =
            Color.argb(
                60,
                100,
                200,
                255
            )


        canvas.drawArc(
            rect,
            135f,
            270f,
            false,
            paint
        )



        paint.color =
            Color.rgb(
                70,
                200,
                255
            )


        canvas.drawArc(
            rect,
            135f,
            270f*progress,
            false,
            paint
        )



        val angle =
            Math.toRadians(
                135.0 +
                270.0*progress
            )



        paint.strokeWidth =
            5f


        paint.color =
            Color.WHITE



        canvas.drawLine(
            cx,
            cy,
            cx+
                cos(angle).toFloat()
                *(radius-30),

            cy+
                sin(angle).toFloat()
                *(radius-30),

            paint
        )



        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.rgb(
               70,
               200,
               255
            )


        canvas.drawCircle(
            cx,
            cy,
            10f,
            paint
        )



        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.textSize =
            radius*0.25f


        paint.color =
            Color.WHITE



        canvas.drawText(
            value.toInt().toString(),
            cx,
            cy+20,
            paint
        )


        paint.textSize =
            18f


        paint.color =
            Color.CYAN


        canvas.drawText(
            unit,
            cx,
            cy+55,
            paint
        )


        paint.textAlign =
            Paint.Align.LEFT

    }

}
