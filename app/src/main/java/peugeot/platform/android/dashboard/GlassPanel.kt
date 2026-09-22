package peugeot.platform.android.dashboard

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF


class GlassPanel {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)



    fun draw(

        canvas: Canvas,

        rect: RectF,

        title: String,

        value: String

    ) {


        // Glass background

        paint.style =
            Paint.Style.FILL


        paint.color =
            Color.argb(
                80,
                30,
                50,
                80
            )


        canvas.drawRoundRect(

            rect,

            25f,

            25f,

            paint

        )



        // Blue edge

        paint.style =
            Paint.Style.STROKE


        paint.strokeWidth =
            2f


        paint.color =
            Color.argb(
                180,
                0,
                170,
                255
            )


        canvas.drawRoundRect(

            rect,

            25f,

            25f,

            paint

        )



        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER



        // Title

        paint.color =
            Color.LTGRAY


        paint.textSize =
            14f



        canvas.drawText(

            title,

            rect.centerX(),

            rect.centerY() - 8f,

            paint

        )



        // Value

        paint.color =
            Color.WHITE


        paint.textSize =
            20f



        canvas.drawText(

            value,

            rect.centerX(),

            rect.centerY() + 22f,

            paint

        )

    }

}
