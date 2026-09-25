package peugeot.platform.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout

import peugeot.platform.android.ai.AIEngine
import peugeot.platform.android.ai.AIState
import peugeot.platform.android.ai.SpeechManager
import peugeot.platform.android.ai.VoiceManager

import peugeot.platform.android.can.CANReceiver

import peugeot.platform.android.dashboard.DashboardView

import peugeot.platform.android.ui.CallPageView
import peugeot.platform.android.ui.CarPageView
import peugeot.platform.android.ui.ClockPageView
import peugeot.platform.android.ui.ErrorScannerPageView
import peugeot.platform.android.ui.HomePageView
import peugeot.platform.android.ui.MainMenuController
import peugeot.platform.android.ui.MainMenuPage
import peugeot.platform.android.ui.MainMenuView
import peugeot.platform.android.ui.MusicPageView
import peugeot.platform.android.ui.NavigationPageView
import peugeot.platform.android.ui.VehicleSettingsPageView

import peugeot.platform.android.vehicle.ErrorScannerEngine
import peugeot.platform.android.vehicle.VehicleData
import peugeot.platform.android.vehicle.VehicleDataController


class MainActivity : Activity() {


    private lateinit var root: FrameLayout

    private lateinit var dashboard: DashboardView
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
        ==========================
        DASHBOARD
        ==========================
        */


        dashboard =
            DashboardView(this).apply {

                setVehicleData(
                    VehicleData.demo()
                )

                visibility =
                    View.GONE

            }


        root.addView(
            dashboard,
            fullScreenParams()
        )



        /*
        ==========================
        HOME PAGE
        ==========================
        */


        homePageView =
            HomePageView(this).apply {

                setVehicleData(
                    VehicleData.demo()
                )

                visibility =
                    View.VISIBLE

            }


        root.addView(
            homePageView,
            fullScreenParams()
        )



        clockPageView =
            ClockPageView(this)


        carPageView =
            CarPageView(this)


        musicPageView =
            MusicPageView(this)


        navigationPageView =
            NavigationPageView(this)


        callPageView =
            CallPageView(this)


        errorScannerPageView =
            ErrorScannerPageView(this)


        vehicleSettingsPageView =
            VehicleSettingsPageView(this)



        addHiddenPage(clockPageView)
        addHiddenPage(carPageView)
        addHiddenPage(musicPageView)
        addHiddenPage(navigationPageView)
        addHiddenPage(callPageView)
        addHiddenPage(errorScannerPageView)
        addHiddenPage(vehicleSettingsPageView)




        /*
        ==========================
        MAIN MENU
        ==========================
        */


        mainMenuView =
            MainMenuView(this).apply {

                setPage(
                    MainMenuPage.HOME
                )

            }


        root.addView(
            mainMenuView,
            fullScreenParams()
        )


        setContentView(root)



        /*
        ==========================
        ERROR SCANNER
        ==========================
        */


        errorScannerEngine =
            ErrorScannerEngine()


        CANReceiver.attachErrorScanner(
            errorScannerEngine
        )



        /*
        ==========================
        VEHICLE DATA
        ==========================
        */


        vehicleDataController =
            VehicleDataController { data ->


                runOnUiThread {

                    updateVehicleViews(
                        data
                    )

                }

            }



        // فعلاً Demo فعال است
        vehicleDataController.useDemoMode()



        /*
        ==========================
        MENU CONTROLLER
        ==========================
        */


        mainMenuController =
            MainMenuController {

                page ->

                runOnUiThread {

                    showPage(page)

                }

            }



        mainMenuView.onPageSelected =
            {

                page ->

                mainMenuController.open(
                    page
                )

            }



        /*
        ==========================
        AI
        ==========================
        */


        aiEngine =
            AIEngine(this)



        speechManager =
            SpeechManager(

                context = this,

                onStateChanged = {

                    state ->

                    dashboard.setAIState(state)

                    homePageView.setAIState(state)

                }

            )



        voiceManager =
            VoiceManager(

                context = this,


                onResult = {

                    text ->


                    aiEngine.process(

                        text,

                        onResponse = {

                            response ->

                            speechManager.speak(
                                response
                            )

                        },


                        onError = {

                            dashboard.setAIState(
                                AIState.ERROR
                            )

                        }

                    )

                },


                onStateChanged = {

                    state ->

                    dashboard.setAIState(
                        state
                    )

                    homePageView.setAIState(
                        state
                    )

                }

            )



        val startVoice = {


            if (
                checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                )
                ==
                PackageManager.PERMISSION_GRANTED
            ) {


                voiceManager.startListening()


            } else {


                pendingVoiceStart = true

                requestMicrophonePermission()

            }


        }



        dashboard.onAIOrbClick =
            startVoice


        homePageView.onAIOrbClick =
            startVoice



        requestMicrophonePermission()

    }





    private fun addHiddenPage(
        view: View
    ) {

        view.visibility =
            View.GONE


        root.addView(
            view,
            fullScreenParams()
        )

    }





    private fun updateVehicleViews(
        data: VehicleData
    ) {


        dashboard.setVehicleData(data)

        homePageView.setVehicleData(data)

        carPageView.setVehicleData(data)

        errorScannerPageView.setVehicleData(data)

    }





    private fun showPage(
        page: MainMenuPage
    ) {


        dashboard.visibility =
            View.GONE


        homePageView.visibility =
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

                homePageView.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.VISIBLE

            }


            MainMenuPage.CLOCK -> {

                clockPageView.visibility =
                    View.VISIBLE

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





    private fun fullScreenParams():
            FrameLayout.LayoutParams {


        return FrameLayout.LayoutParams(

            FrameLayout.LayoutParams.MATCH_PARENT,

            FrameLayout.LayoutParams.MATCH_PARENT

        )

    }





    private fun requestMicrophonePermission() {


        if (
            checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            )
            !=
            PackageManager.PERMISSION_GRANTED
        ) {


            requestPermissions(

                arrayOf(
                    Manifest.permission.RECORD_AUDIO
                ),

                AUDIO_PERMISSION_REQUEST

            )

        }

    }





    override fun onDestroy() {


        if (::vehicleDataController.isInitialized) {

            vehicleDataController.stop()

        }


        if (::voiceManager.isInitialized) {

            voiceManager.destroy()

        }


        if (::speechManager.isInitialized) {

            speechManager.destroy()

        }


        if (CANReceiver.isConnected()) {

            CANReceiver.disconnect()

        }


        super.onDestroy()

    }

}
