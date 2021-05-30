package com.khiem.musicmp3.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.khiem.musicmp3.action.MyAction
import com.khiem.musicmp3.service.MusicService

class MusicReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionMusic = intent?.getIntExtra(MyAction.ACTION_MUSIC, 0)

        val intentService = Intent()
        intentService.action = MyAction.ACTION_MUSIC_TO_SERVICE
        intentService.putExtra(MyAction.ACTION_MUSIC_TO_SERVICE, actionMusic)
        context?.sendBroadcast(intentService)
    }
}