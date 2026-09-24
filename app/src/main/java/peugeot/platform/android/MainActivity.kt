package peugeot.platform.android

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout

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
import peugeot.platform.android.ui.MainMenuPage
import peugeot.platform.android.ui.MainMenuView
import peugeot.platform.android.ui.MusicPageView
import peugeot.platform.android.ui.NavigationPageView
import peugeot.platform.android.ui.StartupView
import peugeot.platform.android.ui.VehicleSettingsPageView

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

        root = FrameLayout(this)

        /*
         * =========================================================
         * BMW 3D DASHBOARD
         * =========================================================
         */

        dashboard = Dashboard3DView(this).apply {

            visibility = View.VISIBLE

            onAIOrbClick = {
                startVoiceFromUser()
            }

            onCarClick = {
                showPage(
                    MainMenuPage.CAR
                )
            }

            onMusicClick = {
                showPage(
                    MainMenuPage.MUSIC
                )
            }

            onNavigationClick = {
                showPage(
                    MainMenuPage.NAVIGATION
                )
            }

            onPhoneClick = {
                showPage(
                    MainMenuPage.CALL
                )
            }

            onScanClick = {
                showPage(
                    MainMenuPage.SCAN
                )
            }
        }

        root.addView(
            dashboard,
            fullScreenParams()
        )

        /*
         * =========================================================
         * BMW iDRIVE MENU
         * =========================================================
         */

        bmwMenu = BMWMenu(this).apply {

            visibility = View.VISIBLE

            isClickable = true
            isFocusable = true

            /*
             * HOME -> CLOCK
             */

            onSwipeRight = {

                runOnUiThread {
                    showPage(
                        MainMenuPage.CLOCK
                    )
                }
            }

            /*
             * CLOCK -> HOME
             */

            onSwipeLeft = {

                runOnUiThread {
                    showPage(
                        MainMenuPage.HOME
                    )
                }
            }

            /*
             * BMW ORBIT MENU
             */

            onMenuClick = { item ->

                runOnUiThread {

                    when (item) {

                        "CAR" -> {
                            showPage(
                                MainMenuPage.CAR
                            )
                        }

                        "MUSIC" -> {
                            showPage(
                                MainMenuPage.MUSIC
                            )
                        }

                        "NAVI" -> {
                            showPage(
                                MainMenuPage.NAVIGATION
                            )
                        }

                        "PHONE" -> {
                            showPage(
                                MainMenuPage.CALL
                            )
                        }

                        "SCAN" -> {
                            showPage(
                                MainMenuPage.SCAN
                            )
                        }

                        "AI" -> {
                            startVoiceFromUser()
                        }
                    }
                }
            }
        }

        root.addView(
            bmwMenu,
            fullScreenParams()
        )

        /*
         * =========================================================
         * HOME PAGE
         * =========================================================
         */

        homePageView =
            HomePageView(this).apply {

                visibility = View.GONE

                setVehicleData(
                    VehicleData.demo()
                )

                onAIOrbClick = {
                    startVoiceFromUser()
                }

                onCarClick = {
                    showPage(
                        MainMenuPage.CAR
                    )
                }

                onMusicClick = {
                    showPage(
                        MainMenuPage.MUSIC
                    )
                }

                onNavigationClick = {
                    showPage(
                        MainMenuPage.NAVIGATION
                    )
                }

                onPhoneClick = {
                    showPage(
                        MainMenuPage.CALL
                    )
                }

                onScannerClick = {
                    showPage(
                        MainMenuPage.SCAN
                    )
                }
            }

        root.addView(
            homePageView,
            fullScreenParams()
        )

        /*
         * =========================================================
         * CLOCK
         * =========================================================
         */

        clockPageView =
            ClockPageView(this).apply {

                visibility = View.GONE
            }

        root.addView(
            clockPageView,
            fullScreenParams()
        )

        /*
         * =========================================================
         * CAR
         * =========================================================
         */

        carPageView =
            CarPageView(this).apply {

                visibility = View.GONE
            }

        root.addView(
            carPageView,
            fullScreenParams()
        )

        /*
         * =========================================================
         * MUSIC
         * =========================================================
         */

        musicPageView =
            MusicPageView(this).apply {

                visibility = View.GONE

                onBackClick = {
                    showPage(
                        MainMenuPage.HOME
                    )
                }

                onPlayPauseClick = {
                    sendMediaKey(
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    )
                }

                onPreviousClick = {
                    sendMediaKey(
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS
                    )
                }

                onNextClick = {
                    sendMediaKey(
                        KeyEvent.KEYCODE_MEDIA_NEXT
                    )
                }
            }

        root.addView(
            musicPageView,
            fullScreenParams()
        )

        /*
         * =========================================================
         * NAVIGATION
         * =========================================================
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
         * =========================================================
         * PHONE
         * =========================================================
         */

        callPageView =
            CallPageView(this).apply {

                visibility = View.GONE
            }

        root.addView(
            callPageView,
            fullScreenParams()
        )

        /*
         * =========================================================
         * ERROR SCANNER
         * =========================================================
         */

        errorScannerPageView =
            ErrorScannerPageView(this).apply {

                visibility = View.GONE

                onBackClick = {
                    showPage(
                        MainMenuPage.HOME
                    )
                }

                onRescanClick = {
                    clearErrors()
                    startScan()
                }
            }

        root.addView(
            errorScannerPageView,
            fullScreenParams()
        )

        /*
         * =========================================================
         * SETTINGS
         * =========================================================
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
         * =========================================================
         * MAIN MENU VIEW
         * =========================================================
         */

        mainMenuView =
            MainMenuView(this).apply {

                visibility = View.GONE

                setPage(
                    MainMenuPage.HOME
                )
            }

        root.addView(
            mainMenuView,
            fullScreenParams()
        )

        /*
         * =========================================================
         * SET CONTENT
         * =========================================================
         */

        setContentView(root)

        /*
         * =========================================================
         * STARTUP SCREEN
         * =========================================================
         */

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

        /*
         * =========================================================
         * DISPLAY BOOT
         * =========================================================
         */

        displayBootManager =
            DisplayBootManager()

        displayBootManager.onWindowReady()

        /*
         * =========================================================
         * ERROR SCANNER ENGINE
         * =========================================================
         */

        errorScannerEngine =
            ErrorScannerEngine()

        CANReceiver.attachErrorScanner(
            errorScannerEngine
        )

        errorScannerPageView.bindEngine(
            errorScannerEngine
        )

        /*
         * =========================================================
         * VEHICLE DATA
         * =========================================================
         */

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
         * =========================================================
         * MAIN MENU CONTROLLER
         * =========================================================
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
         * =========================================================
         * AI ENGINE
         * =========================================================
         */

        aiEngine =
            AIEngine(this)

        /*
         * =========================================================
         * SPEECH MANAGER
         * =========================================================
         */

        speechManager =
            SpeechManager(

                context = this,

                onStateChanged = { state ->

                    runOnUiThread {

                        updateAIState(
                            state
                        )
                    }
                },

                onFinished = {
                    runOnUiThread {
                        if (::voiceManager.isInitialized) {
                            voiceManager.resumeContinuousListening()
                        }
                    }
                }
            )

        /*
         * =========================================================
         * VOICE MANAGER
         * =========================================================
         */

        voiceManager =
            VoiceManager(

                context = this,

                onResult = { text ->

                    runOnUiThread {

                        updateAIState(
                            AIState.THINKING
                        )

                        aiEngine.process(

                            text = text,

                            onResponse = { answer ->

                                runOnUiThread {

                                    updateAIState(
                                        AIState.SPEAKING
                                    )

                                    speechManager.speak(
                                        answer
                                    )
                                }
                            },

                            onError = {

                                runOnUiThread {

                                    updateAIState(
                                        AIState.ERROR
                                    )

                                    speechManager.speak(
                                        "خطا در پردازش درخواست"
                                    )
                                }
                            }
                        )
                    }
                },

                onStateChanged = { state ->

                    runOnUiThread {

                        updateAIState(
                            state
                        )
                    }
                }
            )

        /*
         * =========================================================
         * AUTO VOICE
         * =========================================================
         */

        startVoiceSystem()
    }

    /*
     * =============================================================
     * START VOICE
     * =============================================================
     */

    private fun startVoiceFromUser() {

        if (
            ::voiceManager.isInitialized
        ) {

            voiceManager
                .startContinuousListening()
        }
    }

    /*
     * =============================================================
     * AI STATE
     * =============================================================
     */

    private fun updateAIState(
        state: AIState
    ) {

        dashboard.setAIState(
            state
        )

        homePageView.setAIState(
            state
        )
    }

    /*
     * =============================================================
     * VEHICLE DATA
     * =============================================================
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

    /*
     * =============================================================
     * FULL SCREEN PARAMETERS
     * =============================================================
     */

    private fun fullScreenParams():
            FrameLayout.LayoutParams {

        return FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
    }

    /*
     * =============================================================
     * VOICE SYSTEM
     * =============================================================
     */

    private fun startVoiceSystem() {

        if (
            checkSelfPermission(
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            if (
                ::voiceManager.isInitialized
            ) {

                voiceManager
                    .startContinuousListening()
            }

        } else {

            pendingVoiceStart = true

            requestPermissions(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO
                ),
                AUDIO_PERMISSION_REQUEST
            )
        }
    }

    /*
     * =============================================================
     * PERMISSION RESULT
     * =============================================================
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
            AUDIO_PERMISSION_REQUEST
        ) {

            if (
                grantResults.isNotEmpty() &&
                grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {

                if (
                    ::voiceManager.isInitialized &&
                    pendingVoiceStart
                ) {

                    pendingVoiceStart = false

                    voiceManager
                        .startContinuousListening()
                }
            }
        }
    }

    private fun sendMediaKey(
        keyCode: Int
    ) {

        val audioManager =
            getSystemService(
                AUDIO_SERVICE
            ) as AudioManager

        try {
            audioManager.dispatchMediaKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    keyCode
                )
            )

            audioManager.dispatchMediaKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_UP,
                    keyCode
                )
            )
        } catch (_: Exception) {
        }
    }

    /*
     * =============================================================
     * PAGE MANAGEMENT
     * =============================================================
     */

    private fun showPage(
        page: MainMenuPage
    ) {

        when (page) {

            /*
             * HOME
             */

            MainMenuPage.HOME -> {

                hideAllMainLayers()

                dashboard.visibility =
                    View.VISIBLE

                bmwMenu.visibility =
                    View.GONE

                dashboard.translationX = 0f
                dashboard.alpha = 1f

                bmwMenu.translationX = 0f
                bmwMenu.alpha = 1f
            }

            /*
             * CLOCK
             */

            MainMenuPage.CLOCK -> {

                hideAllMainLayers()

                dashboard.visibility =
                    View.VISIBLE

                clockPageView.visibility =
                    View.VISIBLE

                bmwMenu.visibility =
                    View.VISIBLE

                animateToPage(
                    dashboard,
                    clockPageView,
                    1
                )
            }

            /*
             * CAR
             */

            MainMenuPage.CAR -> {

                hideAllMainLayers()

                carPageView.visibility =
                    View.VISIBLE
            }

            /*
             * MUSIC
             */

            MainMenuPage.MUSIC -> {

                hideAllMainLayers()

                musicPageView.visibility =
                    View.VISIBLE
            }

            /*
             * NAVIGATION
             */

            MainMenuPage.NAVIGATION -> {

                hideAllMainLayers()

                navigationPageView.visibility =
                    View.VISIBLE

                navigationPageView.refresh()
            }

            /*
             * CALL
             */

            MainMenuPage.CALL -> {

                hideAllMainLayers()

                callPageView.visibility =
                    View.VISIBLE
            }

            /*
             * SCAN
             */

            MainMenuPage.SCAN -> {

                hideAllMainLayers()

                errorScannerPageView.visibility =
                    View.VISIBLE

                errorScannerPageView.startScan()
            }

            /*
             * SETTINGS
             */

            MainMenuPage.SETTINGS -> {

                hideAllMainLayers()

                vehicleSettingsPageView.visibility =
                    View.VISIBLE
            }
        }
    }

    /*
     * =============================================================
     * HIDE EVERYTHING
     * =============================================================
     */

    private fun hideAllMainLayers() {

        dashboard.visibility =
            View.GONE

        bmwMenu.visibility =
            View.GONE

        clockPageView.visibility =
            View.GONE

        homePageView.visibility =
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

        mainMenuView.visibility =
            View.GONE
    }

    /*
     * =============================================================
     * PAGE ANIMATION
     * =============================================================
     */

    private fun animateToPage(
        from: View,
        to: View,
        direction: Int
    ) {

        val screenWidth =
            if (root.width > 0) {

                root.width.toFloat()

            } else {

                resources.displayMetrics
                    .widthPixels
                    .toFloat()
            }

        to.visibility =
            View.VISIBLE

        to.alpha =
            1f

        to.translationX =
            if (direction > 0) {
                screenWidth
            } else {
                -screenWidth
            }

        if (
            from.visibility ==
            View.VISIBLE
        ) {

            from.animate()
                .translationX(
                    if (direction > 0) {
                        -screenWidth
                    } else {
                        screenWidth
                    }
                )
                .alpha(0f)
                .setDuration(450L)
                .start()
        }

        to.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(450L)
            .withEndAction {

                if (
                    from.visibility ==
                    View.VISIBLE
                ) {

                    from.visibility =
                        View.GONE

                    from.translationX =
                        0f

                    from.alpha =
                        1f
                }

                to.translationX =
                    0f

                to.alpha =
                    1f
            }
            .start()
    }

    /*
     * =============================================================
     * BACK BUTTON
     * =============================================================
     */

    override fun onBackPressed() {

        showPage(
            MainMenuPage.HOME
        )
    }

    /*
     * =============================================================
     * DESTROY
     * =============================================================
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

        if (
            CANReceiver.isConnected()
        ) {

            CANReceiver.stopReceiving()

            CANReceiver.disconnect()
        }

        super.onDestroy()
    }
}
