package peugeot.platform.android.ui

import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


class SwipeController(
    private val onSwipeRight: () -> Unit,
    private val onSwipeLeft: () -> Unit
) : View.OnTouchListener {



    private var downX =
        0f


    private var downY =
        0f



    private val threshold =
        120f





    override fun onTouch(
        v: View?,
        event: MotionEvent
    ): Boolean {



        when(event.action){



            MotionEvent.ACTION_DOWN -> {


                downX =
                    event.x


                downY =
                    event.y


                return true

            }




            MotionEvent.ACTION_UP -> {


                val diffX =
                    event.x - downX



                val diffY =
                    event.y - downY




                if(
                    abs(diffX) >
                    abs(diffY)
                ){


                    if(
                        diffX > threshold
                    ){

                        onSwipeRight()

                    }
                    else if(
                        diffX < -threshold
                    ){

                        onSwipeLeft()

                    }


                }



                return true

            }

        }


        return false

    }


}
