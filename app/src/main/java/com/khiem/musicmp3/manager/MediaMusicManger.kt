package com.khiem.musicmp3.manager

import android.media.MediaPlayer
import android.util.Log

class MediaMusicManger() {
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
        mp?.start()
    }

    fun pause() {
        mp?.pause()
    }

    fun stop() {
        mp?.stop()
    }

    fun release(){
        mp?.release()
    }
}