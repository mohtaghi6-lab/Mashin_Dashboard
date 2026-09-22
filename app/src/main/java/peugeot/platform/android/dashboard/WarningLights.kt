package peugeot.platform.android.dashboard

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint



class WarningLights {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)



    fun draw(

        canvas: Canvas,

        x: Float,

        y: Float

    ) {



        val lights =
            listOf(

                "ENGINE" to false,

                "ABS" to false,

                "AIRBAG" to false,

                "OIL" to false,

                "BAT" to false,

                "TEMP" to false

            )



        paint.textAlign =
            Paint.Align.CENTER



        paint.textSize =
            12f



        var offset =
            0f



        for(light in lights){


            paint.color =
                if(light.second)
                    Color.RED
                else
                    Color.DKGRAY



            canvas.drawCircle(

                x + offset,

                y,

                12f,

                paint

            )



            paint.color =
                Color.WHITE



            paint.textSize =
                9f



            canvas.drawText(

                light.first,

                x + offset,

                y + 28f,

                paint

            )



            offset += 65f

        }

    }

}
