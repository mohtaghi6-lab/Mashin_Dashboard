package peugeot.platform.android.ui

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View


class MainMenuView(
    context: Context
) : View(context) {


    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)


    private var currentPage =
        MainMenuPage.HOME


    var onPageSelected:
            ((MainMenuPage) -> Unit)? = null



    private val menuItems =
        arrayOf(

            MainMenuPage.HOME,

            MainMenuPage.CAR,

            MainMenuPage.MUSIC,

            MainMenuPage.NAVIGATION,

            MainMenuPage.CALL,

            MainMenuPage.SCAN,

            MainMenuPage.SETTINGS
        )



    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)


        val w =
            width.toFloat()

        val h =
            height.toFloat()



        paint.textAlign =
            Paint.Align.CENTER


        paint.typeface =
            Typeface.DEFAULT_BOLD



        paint.color =
            Color.WHITE


        paint.textSize =
            30f



        canvas.drawText(
            pageTitle(),
            w / 2,
            70f,
            paint
        )



        var y =
            150f



        for(item in menuItems) {


            paint.color =
                if(item == currentPage)

                    Color.rgb(
                        70,
                        190,
                        255
                    )

                else

                    Color.rgb(
                        20,
                        40,
                        60
                    )



            canvas.drawRoundRect(

                80f,

                y-30,

                w-80f,

                y+30,

                25f,

                25f,

                paint
            )



            paint.color =
                Color.WHITE


            paint.textSize =
                18f



            canvas.drawText(

                item.name,

                w/2,

                y+7,

                paint
            )


            y += 70f
        }

    }




    fun setPage(
        page: MainMenuPage
    ) {

        currentPage =
            page

        invalidate()

    }




    private fun pageTitle():

            String {


        return when(currentPage) {


            MainMenuPage.HOME ->
                "HOME"


            MainMenuPage.CAR ->
                "CAR"


            MainMenuPage.MUSIC ->
                "MUSIC"


            MainMenuPage.NAVIGATION ->
                "NAVIGATION"


            MainMenuPage.CALL ->
                "CALL"


            MainMenuPage.SCAN ->
                "DIAGNOSTICS"


            MainMenuPage.SETTINGS ->
                "SETTINGS"

        }

    }




    override fun onTouchEvent(
        event: MotionEvent
    ): Boolean {


        if(
            event.action ==
            MotionEvent.ACTION_UP
        ) {


            val index =
                ((event.y - 120) / 70).toInt()



            if(
                index >= 0 &&
                index < menuItems.size
            ) {


                currentPage =
                    menuItems[index]


                onPageSelected?.invoke(
                    currentPage
                )


                invalidate()

                return true
            }

        }


        return true
    }

}
