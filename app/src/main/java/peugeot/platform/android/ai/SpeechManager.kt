package peugeot.platform.android.ai

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale


class SpeechManager(
    context: Context,
    private val onStateChanged: (AIState) -> Unit
) : TextToSpeech.OnInitListener {


    private var tts: TextToSpeech? = null

    private var ready = false


    init {

        tts =
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
            tts?.setLanguage(
                Locale(
                    "fa",
                    "IR"
                )
            )



        if (
            languageResult ==
            TextToSpeech.LANG_MISSING_DATA
            ||
            languageResult ==
            TextToSpeech.LANG_NOT_SUPPORTED
        ) {

            ready = false

            onStateChanged(
                AIState.ERROR
            )

            return

        }



        findPersianVoice()



        tts?.setSpeechRate(
            0.92f
        )


        tts?.setPitch(
            1.05f
        )



        tts?.setOnUtteranceProgressListener(

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



        // تست اولیه صدا
        tts?.speak(
            "سلام، سیستم هوشمند خودرو آماده است",
            TextToSpeech.QUEUE_FLUSH,
            Bundle(),
            "startup"
        )

    }




    private fun findPersianVoice() {


        val voices =
            tts?.voices
                ?: return



        val female =
            voices.firstOrNull {

                val name =
                    it.name.lowercase(
                        Locale.ROOT
                    )


                it.locale.language == "fa"
                &&
                (
                    name.contains("female")
                    ||
                    name.contains("woman")
                    ||
                    name.contains("zira")
                    ||
                    name.contains("girl")
                )

            }



        if (
            female != null
        ) {

            tts?.voice =
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

            tts?.voice =
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



        tts?.speak(

            text.trim(),

            TextToSpeech.QUEUE_FLUSH,

            Bundle(),

            "mrt_answer"

        )

    }





    fun stop() {

        tts?.stop()


        onStateChanged(
            AIState.IDLE
        )

    }





    fun destroy() {

        ready = false


        tts?.stop()

        tts?.shutdown()

        tts = null

    }

}
