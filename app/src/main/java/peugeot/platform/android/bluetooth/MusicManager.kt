package peugeot.platform.android.bluetooth

enum class PlaybackState {
    STOPPED,
    PLAYING,
    PAUSED
}

data class MusicInfo(
    val title: String = "",
    val artist: String = "",
    val state: PlaybackState = PlaybackState.STOPPED
)

object MusicManager {

    private var currentMusic =
        MusicInfo()

    fun play(
        title: String,
        artist: String
    ) {

        currentMusic =
            MusicInfo(
                title = title,
                artist = artist,
                state = PlaybackState.PLAYING
            )
    }

    fun pause() {

        currentMusic =
            currentMusic.copy(
                state = PlaybackState.PAUSED
            )
    }

    fun resume() {

        currentMusic =
            currentMusic.copy(
                state = PlaybackState.PLAYING
            )
    }

    fun stop() {

        currentMusic =
            currentMusic.copy(
                state = PlaybackState.STOPPED
            )
    }

    fun next() {
        // آماده برای اتصال به MediaSession
    }

    fun previous() {
        // آماده برای اتصال به MediaSession
    }

    fun getCurrentMusic(): MusicInfo {
        return currentMusic
    }

    fun reset() {
        currentMusic = MusicInfo()
    }
}
