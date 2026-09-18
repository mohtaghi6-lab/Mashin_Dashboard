package peugeot.platform.android.ui

enum class MainMenuPage {
    HOME,
    CAR,
    MUSIC,
    NAVIGATION,
    CALL,
    SCAN
}

class MainMenuController(
    private val onPageChanged:
        (MainMenuPage) -> Unit
) {

    private var currentPage =
        MainMenuPage.HOME

    fun open(
        page: MainMenuPage
    ) {

        currentPage = page

        onPageChanged(page)
    }

    fun home() {
        open(MainMenuPage.HOME)
    }

    fun car() {
        open(MainMenuPage.CAR)
    }

    fun music() {
        open(MainMenuPage.MUSIC)
    }

    fun navigation() {
        open(MainMenuPage.NAVIGATION)
    }

    fun call() {
        open(MainMenuPage.CALL)
    }

    fun scan() {
        open(MainMenuPage.SCAN)
    }

    fun getCurrentPage():
        MainMenuPage {
        return currentPage
    }
}
