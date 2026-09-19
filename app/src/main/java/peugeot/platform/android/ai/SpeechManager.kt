package peugeot.platform.android.ai

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale


class SpeechManager(
    context: Context,
    private val onStateChanged: (AIState) -> Unit
) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    private var ready = false

    init {
        textToSpeech =
            TextToSpeech(
                context,
                this
            )
    }

    override fun onInit(
        status: Int
    ) {

        if (status != TextToSpeech.SUCCESS) {
            ready = false
            onStateChanged(AIState.ERROR)
            return
        }

        val result =
            textToSpeech?.setLanguage(
                Locale("fa", "IR")
            )

        if (
            result == TextToSpeech.LANG_MISSING_DATA ||
            result == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            ready = false
            onStateChanged(AIState.ERROR)
            return
        }

        selectFemaleVoice()

        textToSpeech?.setSpeechRate(
            0.95f
        )

        textToSpeech?.setPitch(
            1.04f
        )

        textToSpeech?.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {

                override fun onStart(
                    utteranceId: String?
                ) {
                    onStateChanged(
                        AIState.SPEAKING
                    )
                }

                override fun onDone(
                    utteranceId: String?
                ) {
                    onStateChanged(
                        AIState.IDLE
                    )
                }

                override fun onError(
                    utteranceId: String?
                ) {
                    onStateChanged(
                        AIState.ERROR
                    )
                }
            }
        )

        ready = true

        Handler(
            Looper.getMainLooper()
        ).postDelayed({

            if (ready) {
                textToSpeech?.speak(
                    "سلام MRT",
                    TextToSpeech.QUEUE_FLUSH,
                    null,
                    "mrt_startup"
                )
            }

        }, 1500)
    }

    private fun selectFemaleVoice() {

        val voices =
            textToSpeech?.voices
                ?: return

        val preferred =
            voices.firstOrNull { voice ->

                val name =
                    voice.name.lowercase(
                        Locale.ROOT
                    )

                voice.locale.language == "fa" &&
                    (
                        name.contains("female") ||
                        name.contains("woman") ||
                        name.contains("girl")
                    )
            }

        if (preferred != null) {
            textToSpeech?.voice =
                preferred
            return
        }

        val persian =
            voices.firstOrNull {
                it.locale.language == "fa"
            }

        if (persian != null) {
            textToSpeech?.voice =
                persian
        }
    }

    fun speak(
        text: String
    ) {

        if (
            !ready ||
            text.isBlank()
        ) {
            onStateChanged(
                AIState.ERROR
            )
            return
        }

        textToSpeech?.speak(
            text.trim(),
            TextToSpeech.QUEUE_FLUSH,
            null,
            "peugeot_ai_response"
        )
    }

    fun stop() {

        textToSpeech?.stop()

        onStateChanged(
            AIState.IDLE
        )
    }

    fun destroy() {

        ready = false

        textToSpeech?.stop()
        textToSpeech?.shutdown()

        textToSpeech = null
    }
}
