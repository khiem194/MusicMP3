package com.khiem.musicmp3.manager

import android.media.MediaPlayer
import android.util.Log

object MediaMusicManger {
    private var mp: MediaPlayer? = null

    fun setData(path: String) {
        mp?.release()
        mp = MediaPlayer()
        mp?.setOnErrorListener { _, _, extra ->
            mp = null
            Log.d("MediaManagerOffline", "error: $extra")
            true
        }
        mp?.setDataSource(path)
        mp?.prepare()
    }

    fun start() {
        if (mp != null) {
            mp?.start()
        }
    }

    fun pause() {
        if (mp != null) {
            mp?.pause()
        }
    }

    fun stop() {
        mp?.stop()
    }

    fun release() {
        mp?.release()
    }

    fun getCurrentPosition() : Int?{
        return mp?.currentPosition
    }

    fun getDuration() : Int? {
        return mp?.duration
    }
}