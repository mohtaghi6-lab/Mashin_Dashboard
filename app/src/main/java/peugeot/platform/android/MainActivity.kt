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
import peugeot.platform.android.ui.SwipeController

import peugeot.platform.android.vehicle.ErrorScannerEngine
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


        root = FrameLayout(this)


        /*
         * ---------------------------------------------------------
         * DASHBOARD
         * ---------------------------------------------------------
         */

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


        /*
         * ---------------------------------------------------------
         * HOME
         * ---------------------------------------------------------
         */

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


        /*
         * ---------------------------------------------------------
         * CLOCK
         * ---------------------------------------------------------
         */

        clockPageView = ClockPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            clockPageView,
            fullScreenParams()
        )


        /*
         * ---------------------------------------------------------
         * CAR
         * ---------------------------------------------------------
         */

        carPageView = CarPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            carPageView,
            fullScreenParams()
        )


        /*
         * ---------------------------------------------------------
         * MUSIC
         * ---------------------------------------------------------
         */

        musicPageView = MusicPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            musicPageView,
            fullScreenParams()
        )


        /*
         * ---------------------------------------------------------
         * NAVIGATION
         * ---------------------------------------------------------
         */

        navigationPageView =
            NavigationPageView(this).apply {

                visibility = View.GONE

            }


        root.addView(
            navigationPageView,
            fullScreenParams()
        )


        /*
         * ---------------------------------------------------------
         * CALL
         * ---------------------------------------------------------
         */

        callPageView = CallPageView(this).apply {

            visibility = View.GONE

        }


        root.addView(
            callPageView,
            fullScreenParams()
        )


        /*
         * ---------------------------------------------------------
         * ERROR SCANNER
         * ---------------------------------------------------------
         */

        errorScannerPageView =
            ErrorScannerPageView(this).apply {

                visibility = View.GONE

            }


        root.addView(
            errorScannerPageView,
            fullScreenParams()
        )


        /*
         * ---------------------------------------------------------
         * VEHICLE SETTINGS
         * ---------------------------------------------------------
         */

        vehicleSettingsPageView =
            VehicleSettingsPageView(this).apply {

                visibility = View.GONE

            }


        root.addView(
            vehicleSettingsPageView,
            fullScreenParams()
        )


        /*
         * ---------------------------------------------------------
         * MAIN MENU
         * ---------------------------------------------------------
         */

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


        /*
         * ---------------------------------------------------------
         * DISPLAY BOOT
         * ---------------------------------------------------------
         */

        displayBootManager =
            DisplayBootManager()


        displayBootManager.onWindowReady()


        /*
         * ---------------------------------------------------------
         * ERROR SCANNER ENGINE
         * ---------------------------------------------------------
         */

        errorScannerEngine =
            ErrorScannerEngine()


        /*
         * Connect the CAN receiver directly
         * to the ECU error scanner.
         *
         * CAN
         *  ↓
         * CANReceiver
         *  ↓
         * ErrorScannerEngine
         */

        CANReceiver.attachErrorScanner(
            errorScannerEngine
        )


        /*
         * ---------------------------------------------------------
         * VEHICLE DATA CONTROLLER
         * ---------------------------------------------------------
         */

        vehicleDataController =
            VehicleDataController { data ->

                runOnUiThread {

                    updateVehicleViews(
                        data
                    )

                }

            }


        /*
         * Until real CAN/GPCU hardware
         * is connected, keep demo data active.
         */

        vehicleDataController.useDemoMode()


        /*
         * ---------------------------------------------------------
         * MAIN MENU CONTROLLER
         * ---------------------------------------------------------
         */

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


        /*
         * ---------------------------------------------------------
         * AI ENGINE
         * ---------------------------------------------------------
         */

        aiEngine =
            AIEngine(this)


        /*
         * ---------------------------------------------------------
         * SPEECH MANAGER
         * ---------------------------------------------------------
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
         * ---------------------------------------------------------
         * VOICE MANAGER
         * ---------------------------------------------------------
         */

        voiceManager =
            VoiceManager(

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
         * ---------------------------------------------------------
         * AI ORB / VOICE START
         * ---------------------------------------------------------
         */

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


        dashboard.onAIOrbClick =
            startVoice


        homePageView.onAIOrbClick =
            startVoice


        /*
         * ---------------------------------------------------------
         * MICROPHONE PERMISSION
         * ---------------------------------------------------------
         */

        requestMicrophonePermission()

    }


    /**
     * Sends vehicle data to all vehicle-related pages.
     */
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


    /**
     * Full-screen layout parameters.
     */
    private fun fullScreenParams():
            FrameLayout.LayoutParams {

        return FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )

    }


    /**
     * Shows the selected application page.
     */
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


        when (page) {

            MainMenuPage.HOME -> {

                homePageView.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.VISIBLE

            }


            MainMenuPage.CLOCK -> {

                clockPageView.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.GONE

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

                /*
                 * Start ECU scan when the
                 * scanner page is opened.
                 */

                errorScannerPageView.startScan()

                mainMenuView.visibility =
                    View.GONE

            }


            MainMenuPage.SETTINGS -> {

                vehicleSettingsPageView.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.GONE

            }

        }

    }


    /**
     * Requests microphone permission.
     */
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
            PackageManager.PERMISSION_GRANTED &&

            pendingVoiceStart
        ) {

            pendingVoiceStart =
                false

            voiceManager.startListening()

        }

    }


    override fun onBackPressed() {

        when {

            clockPageView.visibility ==
                    View.VISIBLE ||

            carPageView.visibility ==
                    View.VISIBLE ||

            musicPageView.visibility ==
                    View.VISIBLE ||

            navigationPageView.visibility ==
                    View.VISIBLE ||

            callPageView.visibility ==
                    View.VISIBLE ||

            errorScannerPageView.visibility ==
                    View.VISIBLE ||

            vehicleSettingsPageView.visibility ==
                    View.VISIBLE -> {

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

        /*
         * Stop CAN reception before
         * destroying the Activity.
         */

        if (CANReceiver.isConnected()) {

            CANReceiver.stopReceiving()

            CANReceiver.disconnect()

        }


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
