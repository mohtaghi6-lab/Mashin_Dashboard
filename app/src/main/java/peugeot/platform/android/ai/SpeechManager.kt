package peugeot.platform.android.ai

import android.content.Context
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


        if (
            status != TextToSpeech.SUCCESS
        ) {

            ready = false

            onStateChanged(
                AIState.ERROR
            )

            return
        }



        val languageResult =
            textToSpeech?.setLanguage(
                Locale(
                    "fa",
                    "IR"
                )
            )



        if (
            languageResult ==
            TextToSpeech.LANG_MISSING_DATA ||

            languageResult ==
            TextToSpeech.LANG_NOT_SUPPORTED
        ) {


            ready = false

            onStateChanged(
                AIState.ERROR
            )

            return
        }



        selectFemaleVoice()



        textToSpeech?.setSpeechRate(
            0.92f
        )


        textToSpeech?.setPitch(
            1.05f
        )



        textToSpeech?.setOnUtteranceProgressListener(

            object :
                UtteranceProgressListener() {


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

    }





    private fun selectFemaleVoice() {


        val voices =
            textToSpeech?.voices
                ?: return



        val female =
            voices.firstOrNull {

                val name =
                    it.name.lowercase(
                        Locale.ROOT
                    )


                it.locale.language == "fa" &&

                (
                    name.contains(
                        "female"
                    )

                    ||

                    name.contains(
                        "woman"
                    )

                    ||

                    name.contains(
                        "zira"
                    )
                )

            }



        if (
            female != null
        ) {

            textToSpeech?.voice =
                female

            return

        }



        val persian =
            voices.firstOrNull {

                it.locale.language ==
                    "fa"

            }



        if (
            persian != null
        ) {

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
