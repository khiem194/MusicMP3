package com.khiem.musicmp3.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MusicService : Service(){
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        return START_NOT_STICKY
    }
}