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
import peugeot.platform.android.ui.ClockPageView
import peugeot.platform.android.ui.ErrorScannerPageView
import peugeot.platform.android.ui.HomePageView
import peugeot.platform.android.ui.MainMenuController
import peugeot.platform.android.ui.MainMenuPage
import peugeot.platform.android.ui.MainMenuView
import peugeot.platform.android.ui.MusicPageView
import peugeot.platform.android.ui.NavigationPageView
import peugeot.platform.android.ui.VehicleSettingsPageView
import peugeot.platform.android.ui.SwipeController

import peugeot.platform.android.vehicle.VehicleData
import peugeot.platform.android.vehicle.VehicleDataController


class MainActivity : Activity() {


    private lateinit var displayBootManager: DisplayBootManager

    private lateinit var swipeController: SwipeController

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


    private lateinit var voiceManager: VoiceManager

    private lateinit var speechManager: SpeechManager

    private lateinit var aiEngine: AIEngine


    private var pendingVoiceStart = false


    companion object {

        private const val AUDIO_PERMISSION_REQUEST = 1001

    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)


        requestWindowFeature(Window.FEATURE_NO_TITLE)


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY



        root = FrameLayout(this)



        dashboard = DashboardView(this).apply {

            setVehicleData(
                VehicleData.demo()
            )

            visibility = View.GONE

        }


        root.addView(
            dashboard,
            fullScreenParams()
        )



        homePageView = HomePageView(this).apply {

            setVehicleData(
                VehicleData.demo()
            )

            visibility = View.VISIBLE

        }


        root.addView(
            homePageView,
            fullScreenParams()
        )



        clockPageView = ClockPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            clockPageView,
            fullScreenParams()
        )
                carPageView = CarPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            carPageView,
            fullScreenParams()
        )



        musicPageView = MusicPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            musicPageView,
            fullScreenParams()
        )



        navigationPageView = NavigationPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            navigationPageView,
            fullScreenParams()
        )



        callPageView = CallPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            callPageView,
            fullScreenParams()
        )



        errorScannerPageView = ErrorScannerPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            errorScannerPageView,
            fullScreenParams()
        )



        vehicleSettingsPageView = VehicleSettingsPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            vehicleSettingsPageView,
            fullScreenParams()
        )



        mainMenuView = MainMenuView(this).apply {

            setPage(
                MainMenuPage.HOME
            )

            visibility = View.VISIBLE

        }


        root.addView(
            mainMenuView,
            fullScreenParams()
        )



        setContentView(root)



        displayBootManager = DisplayBootManager()

        displayBootManager.onWindowReady()



        vehicleDataController =
            VehicleDataController { data ->

                runOnUiThread {

                    dashboard.setVehicleData(data)

                    homePageView.setVehicleData(data)

                    carPageView.setVehicleData(data)

                    errorScannerPageView.setVehicleData(data)

                }

            }



        vehicleDataController.useDemoMode()



        mainMenuController =
            MainMenuController { page ->

                runOnUiThread {

                    showPage(page)

                }

            }



        mainMenuView.onPageSelected = { page ->

            mainMenuController.open(page)

        }



        aiEngine = AIEngine(this)



        speechManager = SpeechManager(

            context = this,

            onStateChanged = { state ->

                runOnUiThread {

                    dashboard.setAIState(state)

                    homePageView.setAIState(state)

                }

            }

        )



        voiceManager = VoiceManager(

            context = this,

            onResult = { text ->

                runOnUiThread {

                    dashboard.setAIState(
                        AIState.THINKING
                    )

                    homePageView.setAIState(
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

                                homePageView.setAIState(
                                    AIState.ERROR
                                )

                            }

                        }

                    )

                }

            },


            onStateChanged = { state ->

                runOnUiThread {

                    dashboard.setAIState(state)

                    homePageView.setAIState(state)

                }

            }

        )



        val startVoice = {

            if (
                checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                voiceManager.startListening()

            } else {

                pendingVoiceStart = true

                requestMicrophonePermission()

            }

        }



        dashboard.onAIOrbClick = startVoice

        homePageView.onAIOrbClick = startVoice



        requestMicrophonePermission()

    }
    private fun fullScreenParams(): FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }


    private fun showPage(page: MainMenuPage) {

        dashboard.visibility = View.GONE
        homePageView.visibility = View.GONE
        clockPageView.visibility = View.GONE
        carPageView.visibility = View.GONE
        musicPageView.visibility = View.GONE
        navigationPageView.visibility = View.GONE
        callPageView.visibility = View.GONE
        errorScannerPageView.visibility = View.GONE
        vehicleSettingsPageView.visibility = View.GONE


        when(page) {

            MainMenuPage.HOME -> {

                homePageView.visibility = View.VISIBLE
                mainMenuView.visibility = View.VISIBLE

            }


            MainMenuPage.CLOCK -> {

                clockPageView.visibility = View.VISIBLE
                mainMenuView.visibility = View.GONE

            }


            MainMenuPage.CAR -> {

                carPageView.visibility = View.VISIBLE
                mainMenuView.visibility = View.GONE

            }


            MainMenuPage.MUSIC -> {

                musicPageView.visibility = View.VISIBLE
                mainMenuView.visibility = View.GONE

            }


            MainMenuPage.NAVIGATION -> {

                navigationPageView.visibility = View.VISIBLE
                navigationPageView.refresh()
                mainMenuView.visibility = View.GONE

            }


            MainMenuPage.CALL -> {

                callPageView.visibility = View.VISIBLE
                mainMenuView.visibility = View.GONE

            }


            MainMenuPage.SCAN -> {

                errorScannerPageView.visibility = View.VISIBLE
                mainMenuView.visibility = View.GONE

            }


            MainMenuPage.SETTINGS -> {

                vehicleSettingsPageView.visibility = View.VISIBLE
                mainMenuView.visibility = View.GONE

            }

        }

    }


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
            requestCode == AUDIO_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] ==
            PackageManager.PERMISSION_GRANTED &&
            pendingVoiceStart
        ) {

            pendingVoiceStart = false

            voiceManager.startListening()

        }

    }



        override fun onBackPressed() {

        when {

            clockPageView.visibility == View.VISIBLE ||
            carPageView.visibility == View.VISIBLE ||
            musicPageView.visibility == View.VISIBLE ||
            navigationPageView.visibility == View.VISIBLE ||
            callPageView.visibility == View.VISIBLE ||
            errorScannerPageView.visibility == View.VISIBLE ||
            vehicleSettingsPageView.visibility == View.VISIBLE -> {

                showPage(
                    MainMenuPage.HOME
                )

            }


            else -> {

                super.onBackPressed()

            }

        }

    }



    override fun onDestroy() {


        if (::voiceManager.isInitialized) {

            voiceManager.destroy()

        }


        if (::speechManager.isInitialized) {

            speechManager.destroy()

        }


        if (::displayBootManager.isInitialized) {

            displayBootManager.destroy()

        }


        super.onDestroy()

    }

}
