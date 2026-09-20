package peugeot.platform.android.ui

import android.view.MotionEvent
import android.view.View
import kotlin.math.abs


class SwipeController(
    private val view: View,
    private val onSwipeRight: () -> Unit,
    private val onSwipeLeft: () -> Unit
) {


    private var downX = 0f
    private var downY = 0f


    private val threshold = 120f



    fun attach() {


        view.setOnTouchListener { _, event ->


            when(event.action) {


                MotionEvent.ACTION_DOWN -> {

                    downX = event.x
                    downY = event.y

                    true
                }


                MotionEvent.ACTION_UP -> {


                    val diffX =
                        event.x - downX


                    val diffY =
                        event.y - downY



                    if (
                        abs(diffX) >
                        abs(diffY)
                    ) {


                        if (
                            diffX >
                            threshold
                        ) {

                            onSwipeRight()

                        }


                        else if (
                            diffX <
                            -threshold
                        ) {

                            onSwipeLeft()

                        }

                    }


                    true
                }


                else -> true
            }

        }

    }

}
