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

import peugeot.platform.android.dashboard.DashboardView

import peugeot.platform.android.ui.CallPageView
import peugeot.platform.android.ui.CarPageView
import peugeot.platform.android.ui.ErrorScannerPageView
import peugeot.platform.android.ui.MainMenuController
import peugeot.platform.android.ui.MainMenuPage
import peugeot.platform.android.ui.MainMenuView
import peugeot.platform.android.ui.MusicPageView
import peugeot.platform.android.ui.NavigationPageView

import peugeot.platform.android.vehicle.VehicleData
import peugeot.platform.android.vehicle.VehicleDataController


class MainActivity : Activity() {


    private lateinit var displayBootManager: DisplayBootManager


    private lateinit var voiceManager: VoiceManager
    private lateinit var aiEngine: AIEngine
    private lateinit var speechManager: SpeechManager


    private lateinit var dashboard: DashboardView

    private lateinit var carPageView: CarPageView
    private lateinit var musicPageView: MusicPageView
    private lateinit var navigationPageView: NavigationPageView
    private lateinit var callPageView: CallPageView
    private lateinit var errorScannerPageView: ErrorScannerPageView


    private lateinit var mainMenuView: MainMenuView
    private lateinit var mainMenuController: MainMenuController


    private lateinit var vehicleDataController: VehicleDataController


    private lateinit var root: FrameLayout


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



        // =========================
        // DASHBOARD
        // =========================

        dashboard =
            DashboardView(this)


        dashboard.setVehicleData(
            VehicleData.demo()
        )


        root.addView(
            dashboard,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )



        // =========================
        // CAR
        // =========================

        carPageView =
            CarPageView(this)


        carPageView.visibility =
            View.GONE


        root.addView(
            carPageView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )



        // =========================
        // MUSIC
        // =========================

        musicPageView =
            MusicPageView(this)


        musicPageView.visibility =
            View.GONE


        root.addView(
            musicPageView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )



        // =========================
        // NAVIGATION
        // =========================

        navigationPageView =
            NavigationPageView(this)


        navigationPageView.visibility =
            View.GONE


        root.addView(
            navigationPageView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )


        // =========================
        // CALL
        // =========================

        callPageView =
            CallPageView(this)


        callPageView.visibility =
            View.GONE


        root.addView(
            callPageView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )


        // =========================
        // ERROR SCANNER
        // =========================

        errorScannerPageView =
            ErrorScannerPageView(this)


        errorScannerPageView.visibility =
            View.GONE


        root.addView(
            errorScannerPageView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
                // =========================
        // MENU
        // =========================

        mainMenuView =
            MainMenuView(this)


        root.addView(
            mainMenuView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )


        setContentView(root)



        // =========================
        // BOOT MANAGER
        // =========================

        displayBootManager =
            DisplayBootManager()


        displayBootManager.onWindowReady()



        // =========================
        // VEHICLE DATA
        // =========================

        vehicleDataController =
            VehicleDataController { data ->


                runOnUiThread {


                    dashboard.setVehicleData(
                        data
                    )


                    carPageView.setVehicleData(
                        data
                    )


                    errorScannerPageView.setVehicleData(
                        data
                    )

                }

            }


        vehicleDataController.useDemoMode()



        // =========================
        // MENU CONTROLLER
        // =========================

        mainMenuController =
            MainMenuController { page ->


                runOnUiThread {


                    showPage(
                        page
                    )

                }

            }



        mainMenuView.onPageSelected =
            { page ->


                mainMenuController.open(
                    page
                )

            }



        // =========================
        // AI ENGINE
        // =========================

        aiEngine =
            AIEngine(this)



        // =========================
        // SPEECH MANAGER
        // =========================

        speechManager =
            SpeechManager(

                context = this,

                onStateChanged = { state ->


                    runOnUiThread {


                        dashboard.setAIState(
                            state
                        )

                    }

                }

            )



        // =========================
        // VOICE MANAGER
        // =========================

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


                            onResponse = { response ->


                                runOnUiThread {


                                    speechManager.speak(
                                        response
                                    )

                                }

                            },


                            onError = {


                                runOnUiThread {


                                    dashboard.setAIState(
                                        AIState.ERROR
                                    )

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

                    }

                }

            )



        // =========================
        // AI ORB CLICK
        // =========================

        dashboard.onAIOrbClick = {


            if(

                checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

            ){


                voiceManager.startListening()


            } else {


                pendingVoiceStart = true


                requestMicrophonePermission()

            }

        }



        requestMicrophonePermission()

    }
        // =========================
    // PAGE CONTROL
    // =========================

    private fun showPage(
        page: MainMenuPage
    ) {


        dashboard.visibility =
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



        when(page) {


            MainMenuPage.HOME -> {

                dashboard.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.VISIBLE
            }



            MainMenuPage.CAR -> {

                carPageView.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.GONE
            }



            MainMenuPage.MUSIC -> {

                musicPageView.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.GONE
            }



            MainMenuPage.NAVIGATION -> {

                navigationPageView.visibility =
                    View.VISIBLE

                navigationPageView.refresh()

                mainMenuView.visibility =
                    View.GONE
            }



            MainMenuPage.CALL -> {

                callPageView.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.GONE
            }



            MainMenuPage.SCAN -> {

                errorScannerPageView.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.GONE
            }



            MainMenuPage.SETTINGS -> {

                dashboard.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.VISIBLE

                mainMenuView.setPage(
                    MainMenuPage.SETTINGS
                )
            }

        }

    }



    // =========================
    // MICROPHONE PERMISSION
    // =========================

    private fun requestMicrophonePermission() {


        if (

            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.M

        ) {


            if (

                checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED

            ) {


                requestPermissions(

                    arrayOf(
                        Manifest.permission.RECORD_AUDIO
                    ),

                    AUDIO_PERMISSION_REQUEST
                )

            }

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



        if (

            requestCode ==
            AUDIO_PERMISSION_REQUEST &&

            grantResults.isNotEmpty() &&

            grantResults[0] ==
            PackageManager.PERMISSION_GRANTED

        ) {


            if(pendingVoiceStart) {


                pendingVoiceStart = false


                voiceManager.startListening()

            }

        }

    }



    override fun onDestroy() {


        if(::voiceManager.isInitialized) {

            voiceManager.destroy()

        }



        if(::speechManager.isInitialized) {

            speechManager.destroy()

        }



        if(::displayBootManager.isInitialized) {

            displayBootManager.destroy()

        }



        super.onDestroy()

    }

}
