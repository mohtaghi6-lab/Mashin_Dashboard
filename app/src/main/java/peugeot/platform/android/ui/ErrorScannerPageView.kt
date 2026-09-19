package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.View
import android.view.MotionEvent
import peugeot.platform.android.vehicle.VehicleData


class ErrorScannerPageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var vehicleData =
        VehicleData.demo()


    private var scanning = false


    private var scanResult =
        "READY TO SCAN"


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
            "ECU ERROR SCANNER",
            w / 2,
            70f,
            paint
        )



        paint.color =
            Color.rgb(70,190,255)


        paint.textSize =
            20f


        canvas.drawText(
            if(vehicleData.canConnected)
                "CAN CONNECTION : ONLINE"
            else
                "CAN CONNECTION : WAITING",
            w / 2,
            120f,
            paint
        )



        paint.color =
            Color.rgb(10,20,32)


        canvas.drawRoundRect(
            40f,
            170f,
            w-40f,
            360f,
            25f,
            25f,
            paint
        )



        paint.color =
            Color.WHITE


        paint.textSize =
            24f


        canvas.drawText(
            "Detected Errors",
            w/2,
            220f,
            paint
        )


        paint.color =
            if(vehicleData.ecuErrorCount > 0)
                Color.RED
            else
                Color.rgb(80,255,150)


        paint.textSize =
            60f


        canvas.drawText(
            "${vehicleData.ecuErrorCount}",
            w/2,
            290f,
            paint
        )



        paint.color =
            Color.LTGRAY


        paint.textSize =
            18f


        canvas.drawText(
            scanResult,
            w/2,
            420f,
            paint
        )



        // Scan Button

        paint.color =
            Color.rgb(20,120,220)


        canvas.drawRoundRect(
            w/2-130,
            h-120,
            w/2+130,
            h-50,
            30f,
            30f,
            paint
        )


        paint.color =
            Color.WHITE


        paint.textSize =
            22f


        canvas.drawText(
            if(scanning)
                "SCANNING..."
            else
                "START SCAN",
            w/2,
            h-75,
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
                event.y >
                height-150
            ){

                startScan()

                return true
            }
        }


        return true
    }



    private fun startScan(){

        scanning = true

        scanResult =
            "Reading ECU modules..."

        invalidate()


        postDelayed({

            scanning = false


            scanResult =
                if(vehicleData.ecuErrorCount == 0)
                    "NO ERRORS FOUND"
                else
                    "${vehicleData.ecuErrorCount} ERROR(S) FOUND"


            invalidate()


        },2000)

    }

}
