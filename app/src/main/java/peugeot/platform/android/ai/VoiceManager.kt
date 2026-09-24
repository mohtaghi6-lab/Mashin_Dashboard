package peugeot.platform.android.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class VoiceManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onStateChanged: (AIState) -> Unit
) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val handler = Handler(Looper.getMainLooper())

    private var listening = false
    private var recognitionActive = false
    private var destroyed = false

    private val restartRunnable = Runnable {
        if (listening && !recognitionActive && !destroyed) {
            startListeningInternal()
        }
    }

    private val listener = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {
            recognitionActive = true
            onStateChanged(AIState.LISTENING)
        }

        override fun onBeginningOfSpeech() {
            onStateChanged(AIState.LISTENING)
        }

        override fun onRmsChanged(rmsdB: Float) {
        }

        override fun onBufferReceived(buffer: ByteArray?) {
        }

        override fun onEndOfSpeech() {
            recognitionActive = false
            onStateChanged(AIState.THINKING)
        }

        override fun onError(error: Int) {
            recognitionActive = false

            if (!listening || destroyed) {
                onStateChanged(AIState.IDLE)
                return
            }

            scheduleRestart(900L)
        }

        override fun onResults(results: Bundle?) {
            recognitionActive = false

            val text = results
                ?.getStringArrayList(
                    SpeechRecognizer.RESULTS_RECOGNITION
                )
                ?.firstOrNull()
                ?.trim()
                .orEmpty()

            if (text.isNotBlank()) {
                onStateChanged(AIState.THINKING)
                onResult(text)
            } else {
                scheduleRestart(500L)
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {
        }

        override fun onEvent(
            eventType: Int,
            params: Bundle?
        ) {
        }
    }

    init {
        if (
            SpeechRecognizer.isRecognitionAvailable(context)
        ) {
            speechRecognizer =
                SpeechRecognizer.createSpeechRecognizer(context)

            speechRecognizer?.setRecognitionListener(listener)
        } else {
            onStateChanged(AIState.ERROR)
        }
    }

    fun startContinuousListening() {
        if (destroyed) {
            return
        }

        listening = true
        handler.removeCallbacks(restartRunnable)

        if (!recognitionActive) {
            startListeningInternal()
        }
    }

    private fun startListeningInternal() {
        if (
            !listening ||
            destroyed ||
            recognitionActive
        ) {
            return
        }

        val recognizer = speechRecognizer ?: run {
            onStateChanged(AIState.ERROR)
            return
        }

        val intent = Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "fa-IR"
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "fa-IR"
            )

            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
            )

            putExtra(
                RecognizerIntent.EXTRA_MAX_RESULTS,
                1
            )

            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                1200L
            )

            putExtra(
                RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
                800L
            )
        }

        try {
            recognizer.startListening(intent)
        } catch (e: Exception) {
            recognitionActive = false
            scheduleRestart(1200L)
        }
    }

    private fun scheduleRestart(delayMs: Long) {
        if (!listening || destroyed) {
            return
        }

        handler.removeCallbacks(restartRunnable)
        handler.postDelayed(
            restartRunnable,
            delayMs
        )
    }

    fun resumeContinuousListening() {
        if (destroyed) {
            return
        }

        listening = true
        handler.removeCallbacks(restartRunnable)

        if (!recognitionActive) {
            startListeningInternal()
        }
    }

    fun stopListening() {
        listening = false
        recognitionActive = false
        handler.removeCallbacks(restartRunnable)

        try {
            speechRecognizer?.cancel()
        } catch (_: Exception) {
        }

        onStateChanged(AIState.IDLE)
    }

    fun destroy() {
        destroyed = true
        listening = false
        recognitionActive = false

        handler.removeCallbacksAndMessages(null)

        try {
            speechRecognizer?.cancel()
        } catch (_: Exception) {
        }

        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
