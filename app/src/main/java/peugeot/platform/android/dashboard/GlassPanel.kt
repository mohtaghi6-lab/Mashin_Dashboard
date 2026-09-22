package peugeot.platform.android.dashboard


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface



class GlassPanel {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)



    fun draw(

        canvas: Canvas,

        rect: RectF,

        title: String,

        value: String

    ) {


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

            30f,

            30f,

            paint

        )



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

            30f,

            30f,

            paint

        )


        paint.style =
            Paint.Style.FILL


        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD


        paint.color =
            Color.WHITE


        paint.textSize =
            20f


        canvas.drawText(

            value,

            rect.centerX(),

            rect.centerY()+10f,

            paint

        )

    }

}
