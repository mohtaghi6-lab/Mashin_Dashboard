package peugeot.platform.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import peugeot.platform.android.ui.StartupView
import peugeot.platform.android.ai.AIEngine
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.ai.SpeechManager
import peugeot.platform.android.ai.VoiceManager

import peugeot.platform.android.can.CANReceiver

import peugeot.platform.android.dashboard.BMWMenu
import peugeot.platform.android.dashboard.Dashboard3DView

import peugeot.platform.android.ui.CallPageView
import peugeot.platform.android.ui.CarPageView
import peugeot.platform.android.ui.ClockPageView
import peugeot.platform.android.ui.ErrorScannerPageView
import peugeot.platform.android.ui.HomePageView
import peugeot.platform.android.ui.MainMenuController
import peugeot.platform.android.ui.MainMenuView
import peugeot.platform.android.ui.MusicPageView
import peugeot.platform.android.ui.NavigationPageView
import peugeot.platform.android.ui.VehicleSettingsPageView
import peugeot.platform.android.ui.SwipeController
import peugeot.platform.android.ui.MainMenuPage
import peugeot.platform.android.vehicle.ErrorScannerEngine
import peugeot.platform.android.vehicle.VehicleData
import peugeot.platform.android.vehicle.VehicleDataController


class MainActivity : Activity() {


    private lateinit var displayBootManager: DisplayBootManager
private lateinit var startupView: StartupView
    private lateinit var root: FrameLayout


    private lateinit var dashboard: Dashboard3DView

    private lateinit var bmwMenu: BMWMenu

    private lateinit var homePageView: HomePageView

    private lateinit var clockPageView: ClockPageView

    private lateinit var carPageView: CarPageView

    private lateinit var musicPageView: MusicPageView

    private lateinit var navigationPageView: NavigationPageView

    private lateinit var callPageView: CallPageView

    private lateinit var errorScannerPageView: ErrorScannerPageView

    private lateinit var vehicleSettingsPageView: VehicleSettingsPageView


    private lateinit var mainMenuView: MainMenuView

    private lateinit var mainMenuController: MainMenuController


    private lateinit var vehicleDataController: VehicleDataController

    private lateinit var errorScannerEngine: ErrorScannerEngine


    private lateinit var voiceManager: VoiceManager

    private lateinit var speechManager: SpeechManager

    private lateinit var aiEngine: AIEngine


    private var pendingVoiceStart = false


    companion object {

        private const val AUDIO_PERMISSION_REQUEST = 1001

    }



    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        requestWindowFeature(
            Window.FEATURE_NO_TITLE
        )


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY



        root =
            FrameLayout(this)



        /*
         * BMW 3D DASHBOARD
         */

        dashboard =
            Dashboard3DView(this).apply {

                visibility = View.VISIBLE

            }



        root.addView(
            dashboard,
            fullScreenParams()
        )




        /*
         * BMW ROTARY MENU
         */

        bmwMenu =
            BMWMenu(this).apply {


                visibility =
                    View.VISIBLE



                onMenuClick =
                    { item ->


                        when(item) {


                            "CAR" ->
                                showPage(
                                    MainMenuPage.CAR
                                )


                            "MUSIC" ->
                                showPage(
                                    MainMenuPage.MUSIC
                                )


                            "AI" -> {

                                if(
                                    ::voiceManager.isInitialized
                                ) {

                                    voiceManager
                                        .startContinuousListening()

                                }

                            }


                            "PHONE" ->
                                showPage(
                                    MainMenuPage.CALL
                                )


                            "NAVI" ->
                                showPage(
                                    MainMenuPage.NAVIGATION
                                )


                            "SCAN" ->
                                showPage(
                                    MainMenuPage.SCAN
                                )

                        }


                    }


            }



        root.addView(
            bmwMenu,
            fullScreenParams()
        )



        homePageView =
            HomePageView(this).apply {

                visibility =
                    View.GONE

                setVehicleData(
                    VehicleData.demo()
                )

            }


        root.addView(
            homePageView,
            fullScreenParams()
        )



        clockPageView =
            ClockPageView(this).apply {

                visibility =
                    View.GONE

            }


        root.addView(
            clockPageView,
            fullScreenParams()
        )



        carPageView =
            CarPageView(this).apply {

                visibility =
                    View.GONE

            }


        root.addView(
            carPageView,
            fullScreenParams()
        )



        musicPageView =
            MusicPageView(this).apply {

                visibility =
                    View.GONE

            }


        root.addView(
            musicPageView,
            fullScreenParams()
        )



        navigationPageView =
            NavigationPageView(this).apply {

                visibility =
                    View.GONE

            }


        root.addView(
            navigationPageView,
            fullScreenParams()
        )



        callPageView =
            CallPageView(this).apply {

                visibility =
                    View.GONE

            }


        root.addView(
            callPageView,
            fullScreenParams()
        )



        errorScannerPageView =
            ErrorScannerPageView(this).apply {

                visibility =
                    View.GONE

            }


        root.addView(
            errorScannerPageView,
            fullScreenParams()
        )



        vehicleSettingsPageView =
            VehicleSettingsPageView(this).apply {

                visibility =
                    View.GONE

            }


        root.addView(
            vehicleSettingsPageView,
            fullScreenParams()
        )



        mainMenuView =
            MainMenuView(this).apply {

                visibility =
                    View.GONE

                setPage(
                    MainMenuPage.HOME
                )

            }


        root.addView(
            mainMenuView,
            fullScreenParams()
        )



       setContentView(root)
startupView =
    StartupView(this)


root.addView(
    startupView,
    fullScreenParams()
)



startupView.onFinished = {

    runOnUiThread {

        startupView.visibility =
            View.GONE


        showPage(
            MainMenuPage.HOME
        )

    }

}


        displayBootManager =
            DisplayBootManager()


        displayBootManager.onWindowReady()



        errorScannerEngine =
            ErrorScannerEngine()



        CANReceiver.attachErrorScanner(
            errorScannerEngine
        )



        vehicleDataController =
            VehicleDataController { data ->


                runOnUiThread {


                    updateVehicleViews(
                        data
                    )


                }


            }


        vehicleDataController.useDemoMode()
        
        /*
         * BMW MENU CONTROLLER
         */

        mainMenuController =
            MainMenuController { page ->


                runOnUiThread {


                    showPage(
                        page
                    )


                }


            }




        /*
         * AI ENGINE
         */

        aiEngine =
            AIEngine(this)




        /*
         * SPEECH
         */

        speechManager =
            SpeechManager(

                context = this,


                onStateChanged = { state ->


                    runOnUiThread {


                        dashboard.setAIState(
                            state
                        )


                        homePageView.setAIState(
                            state
                        )


                    }


                }


            )




        /*
         * VOICE AI
         */

        voiceManager =
            VoiceManager(

                context = this,


                onResult = { text ->


                    runOnUiThread {


                        dashboard.setAIState(
                            AIState.THINKING
                        )


                        aiEngine.process(

                            text = text,


                            onResponse = { answer ->


                                runOnUiThread {


                                    speechManager.speak(
                                        answer
                                    )


                                }


                            },


                            onError = {


                                runOnUiThread {


                                    dashboard.setAIState(
                                        AIState.ERROR
                                    )


                                    speechManager.speak(
                                        "خطا در پردازش درخواست"
                                    )


                                    voiceManager
                                        .resumeContinuousListening()


                                }


                            }


                        )


                    }


                },



                onStateChanged = { state ->


                    runOnUiThread {


                        dashboard.setAIState(
                            state
                        )


                        homePageView.setAIState(
                            state
                        )


                    }


                }


            )



        /*
         * AUTO VOICE START
         */

        startVoiceSystem()


    }



    private fun updateVehicleViews(
        data: VehicleData
    ) {


        dashboard.setVehicleData(
            data
        )


        homePageView.setVehicleData(
            data
        )


        carPageView.setVehicleData(
            data
        )


        errorScannerPageView.setVehicleData(
            data
        )


    }




    private fun fullScreenParams():
            FrameLayout.LayoutParams {


        return FrameLayout.LayoutParams(

            FrameLayout.LayoutParams.MATCH_PARENT,

            FrameLayout.LayoutParams.MATCH_PARENT

        )


    }




    private fun startVoiceSystem() {


        if (

            checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        ) {


            if(
                ::voiceManager.isInitialized
            ) {


                voiceManager
                    .startContinuousListening()


            }


        }
        else {


            pendingVoiceStart = true


            requestPermissions(

                arrayOf(
                    Manifest.permission.RECORD_AUDIO
                ),

                AUDIO_PERMISSION_REQUEST

            )


        }


    }





    override fun onRequestPermissionsResult(

        requestCode: Int,

        permissions: Array<out String>,

        grantResults: IntArray

    ) {


        super.onRequestPermissionsResult(

            requestCode,

            permissions,

            grantResults

        )



        if(

            requestCode ==
            AUDIO_PERMISSION_REQUEST

        ) {



            if(

                grantResults.isNotEmpty() &&

                grantResults[0] ==
                PackageManager.PERMISSION_GRANTED

            ) {



                if(

                    ::voiceManager.isInitialized &&

                    pendingVoiceStart

                ) {



                    pendingVoiceStart =
                        false



                    voiceManager
                        .startContinuousListening()



                }


            }


        }


    }






    private fun showPage(

        page: MainMenuPage

    ) {



       dashboard.visibility =
    View.GONE


bmwMenu.visibility =
    View.GONE


        bmwMenu.visibility =
            View.GONE


        clockPageView.visibility =
            View.GONE


        carPageView.visibility =
            View.GONE


        musicPageView.visibility =
            View.GONE


        navigationPageView.visibility =
            View.GONE


        callPageView.visibility =
            View.GONE


        errorScannerPageView.visibility =
            View.GONE


        vehicleSettingsPageView.visibility =
            View.GONE





        when(page) {

MainMenuPage.HOME -> {

    animateToPage(
        clockPageView,
        dashboard,
        -1
    )

    bmwMenu.visibility =
        View.VISIBLE

}



          MainMenuPage.CLOCK -> {

    animateToPage(
        dashboard,
        clockPageView,
        1
    )

}


            MainMenuPage.CAR -> {

                carPageView.visibility =
                    View.VISIBLE

            }



            MainMenuPage.MUSIC -> {

                musicPageView.visibility =
                    View.VISIBLE

            }



            MainMenuPage.NAVIGATION -> {

                navigationPageView.visibility =
                    View.VISIBLE


                navigationPageView.refresh()

            }



            MainMenuPage.CALL -> {

                callPageView.visibility =
                    View.VISIBLE

            }



            MainMenuPage.SCAN -> {

                errorScannerPageView.visibility =
                    View.VISIBLE


                errorScannerPageView.startScan()

            }



            MainMenuPage.SETTINGS -> {

                vehicleSettingsPageView.visibility =
                    View.VISIBLE

            }


        }


    }



private fun animateToPage(
    from: View,
    to: View,
    direction: Int
) {

    to.visibility = View.VISIBLE

    val width =
        root.width.toFloat()


    to.translationX =
        if(direction > 0)
            width
        else
            -width


    to.alpha = 0f



    from.animate()

        .translationX(
            if(direction > 0)
                -width
            else
                width
        )

        .alpha(0f)

        .setDuration(450)

        .start()



    to.animate()

        .translationX(0f)

        .alpha(1f)

        .setDuration(450)

        .withEndAction {

            from.visibility =
                View.GONE


            from.translationX =
                0f

        }

        .start()

}

    override fun onBackPressed() {


        showPage(
            MainMenuPage.HOME
        )


    }






    override fun onDestroy() {


        if(
            ::voiceManager.isInitialized
        ) {

            voiceManager.destroy()

        }



        if(
            ::speechManager.isInitialized
        ) {

            speechManager.destroy()

        }



        if(
            ::displayBootManager.isInitialized
        ) {

            displayBootManager.destroy()

        }



        if(
            CANReceiver.isConnected()
        ) {


            CANReceiver.stopReceiving()


            CANReceiver.disconnect()


        }



        super.onDestroy()


    }


}
