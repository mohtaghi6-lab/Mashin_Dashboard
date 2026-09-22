package peugeot.platform.android.dashboard


import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface



class GlassPanel {



    private val panelPaint =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        )



    private val borderPaint =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        )



    private val titlePaint =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        )



    private val valuePaint =
        Paint(
            Paint.ANTI_ALIAS_FLAG
        )






    init {


        titlePaint.textAlign =
            Paint.Align.CENTER


        titlePaint.typeface =
            Typeface.DEFAULT_BOLD



        valuePaint.textAlign =
            Paint.Align.CENTER


        valuePaint.typeface =
            Typeface.DEFAULT_BOLD


    }








    fun draw(

        canvas: Canvas,

        rect: RectF,

        title: String,

        value: String,

        active: Boolean = false

    ) {



        // Glass body

        panelPaint.style =
            Paint.Style.FILL



        panelPaint.color =
            if(active)

                Color.argb(
                    120,
                    0,
                    170,
                    255
                )

            else

                Color.argb(
                    75,
                    40,
                    60,
                    90
                )




        canvas.drawRoundRect(

            rect,

            30f,

            30f,

            panelPaint

        )







        // Border

        borderPaint.style =
            Paint.Style.STROKE



        borderPaint.strokeWidth =
            if(active) 3f else 2f



        borderPaint.color =
            if(active)

                Color.argb(
                    220,
                    0,
                    200,
                    255
                )

            else

                Color.argb(
                    130,
                    0,
                    170,
                    255
                )




        canvas.drawRoundRect(

            rect,

            30f,

            30f,

            borderPaint

        )







        // Title

        titlePaint.color =
            Color.LTGRAY



        titlePaint.textSize =
            14f



        canvas.drawText(

            title,

            rect.centerX(),

            rect.centerY() - 10f,

            titlePaint

        )








        // Value

        valuePaint.color =
            Color.WHITE



        valuePaint.textSize =
            24f



        canvas.drawText(

            value,

            rect.centerX(),

            rect.centerY() + 25f,

            valuePaint

        )

    }


}
