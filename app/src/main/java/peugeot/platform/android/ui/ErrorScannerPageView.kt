package peugeot.platform.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import peugeot.platform.android.diagnostics.ErrorScanner
import peugeot.platform.android.diagnostics.Severity
import peugeot.platform.android.vehicle.VehicleData


class ErrorScannerPageView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var vehicleData =
        VehicleData.demo()


    private var scanning =
        false


    private var scanResult =
        "READY TO SCAN"


    private var errors =
        emptyList<peugeot.platform.android.diagnostics.ErrorCode>()


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
            width / 2f,
            65f,
            paint
        )



        paint.color =
            Color.rgb(
                70,
                190,
                255
            )


        paint.textSize =
            18f


        canvas.drawText(
            if (vehicleData.canConnected)
                "CAN CONNECTION : ONLINE"
            else
                "CAN CONNECTION : WAITING",
            width / 2f,
            105f,
            paint
        )



        paint.color =
            Color.rgb(
                10,
                20,
                32
            )


        canvas.drawRoundRect(
            40f,
            145f,
            width - 40f,
            height - 190f,
            25f,
            25f,
            paint
        )



        paint.color =
            Color.WHITE


        paint.textSize =
            22f


        canvas.drawText(
            "DIAGNOSTIC STATUS",
            width / 2f,
            190f,
            paint
        )



        val count =
            errors.size


        paint.color =
            if (count > 0)
                Color.RED
            else
                Color.rgb(
                    80,
                    255,
                    150
                )


        paint.textSize =
            55f


        canvas.drawText(
            "$count",
            width / 2f,
            255f,
            paint
        )



        paint.color =
            Color.LTGRAY


        paint.textSize =
            17f


        canvas.drawText(
            if (count == 0)
                "NO ACTIVE ERRORS"
            else
                "ACTIVE ERROR(S)",
            width / 2f,
            290f,
            paint
        )



        var y =
            335f


        errors.take(4).forEach { error ->


            paint.color =
                when (error.severity) {

                    Severity.CRITICAL ->
                        Color.RED

                    Severity.WARNING ->
                        Color.rgb(
                            255,
                            190,
                            60
                        )

                    Severity.INFO ->
                        Color.rgb(
                            70,
                            190,
                            255
                        )
                }


            paint.textSize =
                17f


            paint.textAlign =
                Paint.Align.LEFT


            canvas.drawText(
                error.code,
                65f,
                y,
                paint
            )


            paint.color =
                Color.WHITE


            canvas.drawText(
                error.title,
                145f,
                y,
                paint
            )


            y += 38f
        }



        paint.textAlign =
            Paint.Align.CENTER


        paint.color =
            Color.LTGRAY


        paint.textSize =
            16f


        canvas.drawText(
            scanResult,
            width / 2f,
            height - 145f,
            paint
        )



        paint.color =
            Color.rgb(
                20,
                120,
                220
            )


        canvas.drawRoundRect(
            width / 2f - 130f,
            height - 105f,
            width / 2f + 130f,
            height - 40f,
            30f,
            30f,
            paint
        )



        paint.color =
            Color.WHITE


        paint.textSize =
            20f


        canvas.drawText(
            if (scanning)
                "SCANNING..."
            else
                "START SCAN",
            width / 2f,
            height - 64f,
            paint
        )

    }



    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {


        if (
            event.action ==
            MotionEvent.ACTION_UP
        ) {


            if (
                event.y >
                height - 130f
            ) {

                startScan()

                return true
            }
        }


        return true

    }



    private fun startScan() {


        if (scanning) {
            return
        }


        scanning =
            true


        scanResult =
            "READING ECU MODULES..."


        invalidate()



        postDelayed({

            errors =
                ErrorScanner.scanVehicle(
                    vehicleData
                )


            scanning =
                false


            scanResult =
                if (errors.isEmpty())
                    "SCAN COMPLETE — NO ACTIVE ERRORS"
                else
                    "SCAN COMPLETE — ${errors.size} ERROR(S) FOUND"


            invalidate()


        }, 1200)

    }

}
