package peugeot.platform.android.ai

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class VoiceManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onStateChanged: (AIState) -> Unit
) {

    private var recognizer: SpeechRecognizer? = null

    private val handler =
        Handler(Looper.getMainLooper())

    private var continuousMode = false
    private var restarting = false

    fun startContinuousListening() {

        continuousMode = true
        startListeningInternal()
    }

    fun startListening() {

        continuousMode = false
        startListeningInternal()
    }

    private fun startListeningInternal() {

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onStateChanged(AIState.ERROR)
            return
        }

        if (restarting) {
            return
        }

        stopRecognizerOnly()

        recognizer =
            SpeechRecognizer.createSpeechRecognizer(context)

        recognizer?.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(
                    params: Bundle?
                ) {
                    onStateChanged(
                        AIState.LISTENING
                    )
                }

                override fun onBeginningOfSpeech() {
                    onStateChanged(
                        AIState.LISTENING
                    )
                }

                override fun onRmsChanged(
                    rmsdB: Float
                ) {
                }

                override fun onBufferReceived(
                    buffer: ByteArray?
                ) {
                }

                override fun onEndOfSpeech() {
                    onStateChanged(
                        AIState.THINKING
                    )
                }

                override fun onError(
                    error: Int
                ) {

                    if (continuousMode) {

                        scheduleRestart()

                    } else {

                        onStateChanged(
                            AIState.ERROR
                        )
                    }
                }

                override fun onResults(
                    results: Bundle?
                ) {

                    val matches =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    val text =
                        matches
                            ?.firstOrNull()
                            ?.trim()

                    if (!text.isNullOrEmpty()) {

                        onStateChanged(
                            AIState.THINKING
                        )

                        onResult(text)

                    } else {

                        if (continuousMode) {
                            scheduleRestart()
                        } else {
                            onStateChanged(
                                AIState.IDLE
                            )
                        }
                    }
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {
                }

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {
                }
            }
        )

        val intent =
            Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH
            ).apply {

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
                    3
                )

                putExtra(
                    RecognizerIntent.EXTRA_CALLING_PACKAGE,
                    context.packageName
                )
            }

        try {

            recognizer?.startListening(
                intent
            )

        } catch (e: Exception) {

            onStateChanged(
                AIState.ERROR
            )

            if (continuousMode) {
                scheduleRestart()
            }
        }
    }

    private fun scheduleRestart() {

        if (!continuousMode) {
            return
        }

        if (restarting) {
            return
        }

        restarting = true

        handler.postDelayed({

            restarting = false

            if (continuousMode) {
                startListeningInternal()
            }

        }, 700)
    }

    fun resumeContinuousListening() {

        if (!continuousMode) {
            return
        }

        handler.postDelayed({

            if (continuousMode) {
                startListeningInternal()
            }

        }, 500)
    }

    fun stopListening() {

        continuousMode = false

        handler.removeCallbacksAndMessages(
            null
        )

        stopRecognizerOnly()

        onStateChanged(
            AIState.IDLE
        )
    }

    private fun stopRecognizerOnly() {

        try {
            recognizer?.cancel()
        } catch (_: Exception) {
        }

        try {
            recognizer?.destroy()
        } catch (_: Exception) {
        }

        recognizer = null
    }

    fun destroy() {

        continuousMode = false

        handler.removeCallbacksAndMessages(
            null
        )

        stopRecognizerOnly()
    }
}

