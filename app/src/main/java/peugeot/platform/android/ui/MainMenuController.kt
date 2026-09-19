package peugeot.platform.android.ui


class MainMenuController(
    private val onPageChanged: (MainMenuPage) -> Unit
) {


    fun open(
        page: MainMenuPage
    ) {

        onPageChanged(page)

    }



    fun home() {

        open(
            MainMenuPage.HOME
        )

    }



    fun car() {

        open(
            MainMenuPage.CAR
        )

    }



    fun music() {

        open(
            MainMenuPage.MUSIC
        )

    }



    fun navigation() {

        open(
            MainMenuPage.NAVIGATION
        )

    }



    fun call() {

        open(
            MainMenuPage.CALL
        )

    }



    fun scan() {

        open(
            MainMenuPage.SCAN
        )

    }



    fun settings() {

        open(
            MainMenuPage.SETTINGS
        )

    }

}
