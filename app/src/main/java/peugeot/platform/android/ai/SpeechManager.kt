package peugeot.platform.android.ai

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
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


        if (status == TextToSpeech.SUCCESS) {


            val language =
                Locale(
                    "fa",
                    "IR"
                )


            val result =
                textToSpeech?.setLanguage(
                    language
                )



            if (
                result != TextToSpeech.LANG_MISSING_DATA &&
                result != TextToSpeech.LANG_NOT_SUPPORTED
            ) {


                selectFemaleVoice()


                // طبیعی‌تر برای مکالمه خودرو

                textToSpeech?.setSpeechRate(
                    0.95f
                )


                textToSpeech?.setPitch(
                    1.05f
                )


                ready = true
textToSpeech?.speak(
    "سلام MRT",
    TextToSpeech.QUEUE_FLUSH,
    null,
    "mrt_greeting"
)

onStateChanged(
    AIState.SPEAKING
)

            } else {

                ready = false

                onStateChanged(
                    AIState.ERROR
                )
            }


        } else {


            ready = false

            onStateChanged(
                AIState.ERROR
            )
        }
    }



    private fun selectFemaleVoice() {


        val voices =
            textToSpeech?.voices



        voices?.forEach { voice ->


            val name =
                voice.name.lowercase()



            if (
                name.contains("female") ||
                name.contains("woman") ||
                name.contains("fa")
            ) {


                textToSpeech?.voice =
                    voice


                return

            }
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



        onStateChanged(
            AIState.SPEAKING
        )



        textToSpeech?.speak(

            text,

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


        textToSpeech?.stop()

        textToSpeech?.shutdown()


        textToSpeech = null

        ready = false
    }

}
