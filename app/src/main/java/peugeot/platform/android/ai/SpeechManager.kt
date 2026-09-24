package peugeot.platform.android.ai

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class SpeechManager(
    context: Context,
    private val onStateChanged: (AIState) -> Unit,
    private val onFinished: () -> Unit
) : TextToSpeech.OnInitListener {

    private val mainHandler = Handler(Looper.getMainLooper())

    private var tts: TextToSpeech? = null
    private var ready = false
    private var destroyed = false

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        mainHandler.post {
            if (destroyed) {
                return@post
            }

            if (status != TextToSpeech.SUCCESS) {
                ready = false
                onStateChanged(AIState.ERROR)
                return@post
            }

            val languageResult = tts?.setLanguage(
                Locale("fa", "IR")
            )

            if (
                languageResult == TextToSpeech.LANG_MISSING_DATA ||
                languageResult == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                ready = false
                onStateChanged(AIState.ERROR)
                return@post
            }

            findPersianVoice()

            tts?.setSpeechRate(0.92f)
            tts?.setPitch(1.05f)

            tts?.setOnUtteranceProgressListener(
                object : UtteranceProgressListener() {

                    override fun onStart(utteranceId: String?) {
                        mainHandler.post {
                            if (!destroyed) {
                                onStateChanged(AIState.SPEAKING)
                            }
                        }
                    }

                    override fun onDone(utteranceId: String?) {
                        mainHandler.post {
                            if (!destroyed) {
                                onStateChanged(AIState.IDLE)
                                onFinished()
                            }
                        }
                    }

                    override fun onError(utteranceId: String?) {
                        mainHandler.post {
                            if (!destroyed) {
                                onStateChanged(AIState.ERROR)
                                onFinished()
                            }
                        }
                    }
                }
            )

            ready = true
        }
    }

    private fun findPersianVoice() {
        val voices = tts?.voices ?: return

        val female = voices.firstOrNull {
            val name = it.name.lowercase(Locale.ROOT)

            it.locale.language == "fa" &&
                    (
                        name.contains("female") ||
                        name.contains("woman") ||
                        name.contains("zira") ||
                        name.contains("girl")
                    )
        }

        if (female != null) {
            tts?.voice = female
            return
        }

        val persian = voices.firstOrNull {
            it.locale.language == "fa"
        }

        if (persian != null) {
            tts?.voice = persian
        }
    }

    fun speak(text: String) {
        mainHandler.post {
            if (destroyed) {
                return@post
            }

            if (!ready || text.isBlank()) {
                onStateChanged(AIState.ERROR)
                onFinished()
                return@post
            }

            tts?.speak(
                text.trim(),
                TextToSpeech.QUEUE_FLUSH,
                Bundle(),
                "mrt_answer"
            )
        }
    }

    fun stop() {
        mainHandler.post {
            tts?.stop()

            if (!destroyed) {
                onStateChanged(AIState.IDLE)
            }
        }
    }

    fun destroy() {
        destroyed = true
        ready = false

        mainHandler.removeCallbacksAndMessages(null)

        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
