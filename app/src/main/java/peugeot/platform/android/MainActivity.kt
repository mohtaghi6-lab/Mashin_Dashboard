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
import peugeot.platform.android.ui.CarPageView
import peugeot.platform.android.ui.MainMenuController
import peugeot.platform.android.ui.MainMenuPage
import peugeot.platform.android.ui.MainMenuView
import peugeot.platform.android.vehicle.VehicleData
import peugeot.platform.android.vehicle.VehicleDataController

class MainActivity : Activity() {

    private lateinit var displayBootManager: DisplayBootManager

    private lateinit var voiceManager: VoiceManager

    private lateinit var aiEngine: AIEngine

    private lateinit var speechManager: SpeechManager

    private lateinit var dashboard: DashboardView

    private lateinit var carPageView: CarPageView

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

        /*
         * Fullscreen
         */

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

        /*
         * Root
         */

        root = FrameLayout(this)

        /*
         * Dashboard
         */

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

        /*
         * Car Page
         */

        carPageView =
            CarPageView(this)

        carPageView.setVehicleData(
            VehicleData.demo()
        )

        carPageView.visibility =
            View.GONE

        root.addView(
            carPageView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        /*
         * Main Menu
         */

        mainMenuView =
            MainMenuView(this)

        root.addView(
            mainMenuView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )

        /*
         * Show root
         */

        setContentView(root)

        /*
         * Display
         */

        displayBootManager =
            DisplayBootManager()

        displayBootManager.onWindowReady()

        /*
         * Vehicle Data
         */

        vehicleDataController =
            VehicleDataController { data ->

                runOnUiThread {

                    dashboard.setVehicleData(
                        data
                    )

                    carPageView.setVehicleData(
                        data
                    )
                }
            }

        vehicleDataController.useDemoMode()

        /*
         * Main Menu Controller
         */

        mainMenuController =
            MainMenuController { page ->

                runOnUiThread {

                    showPage(page)
                }
            }

        /*
         * Menu Click
         */

        mainMenuView.onPageSelected = { page ->

            mainMenuController.open(
                page
            )
        }

        /*
         * AI Engine
         */

        aiEngine =
            AIEngine(this)

        /*
         * Persian Speech
         */

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

        /*
         * Voice Manager
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

        /*
         * AI Orb
         */

        dashboard.onAIOrbClick = {

            if (
                checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {

                voiceManager.startListening()

            } else {

                pendingVoiceStart = true

                requestMicrophonePermission()
            }
        }

        /*
         * Microphone Permission
         */

        requestMicrophonePermission()
    }

    /*
     * Page Navigation
     */

    private fun showPage(
        page: MainMenuPage
    ) {

        when (page) {

            MainMenuPage.HOME -> {

                dashboard.visibility =
                    View.VISIBLE

                carPageView.visibility =
                    View.GONE

                mainMenuView.visibility =
                    View.VISIBLE
            }

            MainMenuPage.CAR -> {

                dashboard.visibility =
                    View.GONE

                carPageView.visibility =
                    View.VISIBLE

                mainMenuView.visibility =
                    View.GONE
            }

            /*
             * فعلاً این صفحات
             * در مرحله بعد ساخته می‌شوند.
             *
             * تا آن زمان HOME نمایش داده می‌شود.
             */

            MainMenuPage.MUSIC -> {

                dashboard.visibility =
                    View.VISIBLE

                carPageView.visibility =
                    View.GONE

                mainMenuView.visibility =
                    View.VISIBLE
            }

            MainMenuPage.NAVIGATION -> {

                dashboard.visibility =
                    View.VISIBLE

                carPageView.visibility =
                    View.GONE

                mainMenuView.visibility =
                    View.VISIBLE
            }

            MainMenuPage.CALL -> {

                dashboard.visibility =
                    View.VISIBLE

                carPageView.visibility =
                    View.GONE

                mainMenuView.visibility =
                    View.VISIBLE
            }

            MainMenuPage.SCAN -> {

                dashboard.visibility =
                    View.VISIBLE

                carPageView.visibility =
                    View.GONE

                mainMenuView.visibility =
                    View.VISIBLE
            }
        }

        mainMenuView.setPage(
            page
        )
    }

    /*
     * Microphone Permission
     */

    private fun requestMicrophonePermission() {

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.M
        ) {

            if (
                checkSelfPermission(
                    Manifest.permission.RECORD_AUDIO
                ) !=
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
    }

    /*
     * Permission Result
     */

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

            if (pendingVoiceStart) {

                pendingVoiceStart =
                    false

                voiceManager.startListening()
            }
        }
    }

    /*
     * Back Button
     */

    override fun onBackPressed() {

        if (
            carPageView.visibility ==
            View.VISIBLE
        ) {

            mainMenuController.home()

            return
        }

        super.onBackPressed()
    }

    /*
     * Destroy
     */

    override fun onDestroy() {

        if (
            ::voiceManager.isInitialized
        ) {

            voiceManager.destroy()
        }

        if (
            ::speechManager.isInitialized
        ) {

            speechManager.destroy()
        }

        if (
            ::displayBootManager.isInitialized
        ) {

            displayBootManager.destroy()
        }

        super.onDestroy()
    }
}
