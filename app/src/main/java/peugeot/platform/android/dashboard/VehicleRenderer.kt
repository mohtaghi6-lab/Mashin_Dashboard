package peugeot.platform.android.dashboard

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF


class VehicleRenderer {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)



    fun drawVehicle(

        canvas: Canvas,

        cx: Float,

        cy: Float,

        scale: Float = 1f

    ) {



        paint.style =
            Paint.Style.FILL



        // Shadow زیر خودرو

        paint.color =
            Color.argb(
                120,
                0,
                120,
                255
            )


        canvas.drawOval(

            RectF(

                cx - 90 * scale,

                cy + 70 * scale,

                cx + 90 * scale,

                cy + 95 * scale

            ),

            paint

        )





        // بدنه خودرو

        paint.color =
            Color.rgb(
                190,
                200,
                215
            )


        canvas.drawRoundRect(

            RectF(

                cx - 75 * scale,

                cy - 45 * scale,

                cx + 75 * scale,

                cy + 55 * scale

            ),

            25f * scale,

            25f * scale,

            paint

        )





        // سقف خودرو

        paint.color =
            Color.rgb(
                40,
                55,
                75
            )


        canvas.drawRoundRect(

            RectF(

                cx - 45 * scale,

                cy - 75 * scale,

                cx + 45 * scale,

                cy - 20 * scale

            ),

            20f * scale,

            20f * scale,

            paint

        )





        // شیشه جلو

        paint.color =
            Color.rgb(
                20,
                80,
                130
            )


        canvas.drawRoundRect(

            RectF(

                cx - 35 * scale,

                cy - 65 * scale,

                cx + 35 * scale,

                cy - 30 * scale

            ),

            10f * scale,

            10f * scale,

            paint

        )





        // چراغ جلو چپ

        paint.color =
            Color.CYAN


        canvas.drawCircle(

            cx - 55 * scale,

            cy - 5 * scale,

            8f * scale,

            paint

        )



        // چراغ جلو راست

        canvas.drawCircle(

            cx + 55 * scale,

            cy - 5 * scale,

            8f * scale,

            paint

        )





        // چرخ‌ها

        paint.color =
            Color.BLACK


        canvas.drawCircle(

            cx - 55 * scale,

            cy + 55 * scale,

            18f * scale,

            paint

        )


        canvas.drawCircle(

            cx + 55 * scale,

            cy + 55 * scale,

            18f * scale,

            paint

        )


    }


}
