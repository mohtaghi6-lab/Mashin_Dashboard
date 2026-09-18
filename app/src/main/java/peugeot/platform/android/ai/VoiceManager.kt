package peugeot.platform.android.ai

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.RecognitionListener
import android.os.Bundle
import java.util.Locale

class VoiceManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onStateChanged: (AIState) -> Unit
) {

    private var recognizer: SpeechRecognizer? = null

    fun startListening() {

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onStateChanged(AIState.ERROR)
            return
        }

        stopListening()

        recognizer = SpeechRecognizer.createSpeechRecognizer(context)

        recognizer?.setRecognitionListener(
            object : RecognitionListener {

                override fun onReadyForSpeech(params: Bundle?) {
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
                    onStateChanged(AIState.THINKING)
                }

                override fun onError(error: Int) {
                    onStateChanged(AIState.ERROR)
                }

                override fun onResults(results: Bundle?) {

                    val matches =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    val text =
                        matches?.firstOrNull()?.trim()

                    if (!text.isNullOrEmpty()) {
                        onResult(text)
                    }

                    onStateChanged(AIState.IDLE)
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

        val intent = Intent(
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
                RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE,
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
        }

        recognizer?.startListening(intent)
    }

    fun stopListening() {
        recognizer?.stopListening()
        recognizer?.destroy()
        recognizer = null
    }

    fun destroy() {
        stopListening()
    }
}
