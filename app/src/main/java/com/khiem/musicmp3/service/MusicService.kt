package com.khiem.musicmp3.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.khiem.musicmp3.action.MyAction
import com.khiem.musicmp3.manager.MediaMusicManger
import com.khiem.musicmp3.model.Music

class MusicService : Service(){
    private val listSongs = mutableListOf<Music>()
    private val mMediaMusic = MediaMusicManger()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Mood Music MP3", "Music Service onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val myType = object : TypeToken<List<Music>>() {}.type
        val arr = Gson().fromJson<List<Music>>(intent?.getStringExtra(MyAction.ACTION_LIST_MUSIC), myType)
        if (arr != null){
            listSongs.clear()
            listSongs.addAll(arr)
        }

        val pos = intent?.getIntExtra(MyAction.ACTION_MUSIC, 0)
        if (pos!= null){
            playMusic(pos)
        }


        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Mood Music MP3", "Music Service onDestroy")
    }

    private fun playMusic(position: Int){
        mMediaMusic.release()
        listSongs[position].songUrl?.let { mMediaMusic.setData(it) }
        mMediaMusic.start()
    }
}