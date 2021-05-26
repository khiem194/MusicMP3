package com.khiem.musicmp3.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.khiem.musicmp3.MyApplication
import com.khiem.musicmp3.R
import com.khiem.musicmp3.Utils
import com.khiem.musicmp3.action.MyAction
import com.khiem.musicmp3.manager.MediaMusicManger
import com.khiem.musicmp3.model.Music


@Suppress("DEPRECATION")
class MusicService : Service() {
    private val listSongs = mutableListOf<Music>()
    private val mMediaMusic = MediaMusicManger()
    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionManager: MediaSessionManager? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("Mood Music MP3", "MusicService onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Mood Music MP3", "MusicService onDestroy")
        mMediaMusic.release()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val myType = object : TypeToken<List<Music>>() {}.type
        val arr =
            Gson().fromJson<List<Music>>(intent?.getStringExtra(MyAction.ACTION_LIST_MUSIC), myType)
        if (arr != null) {
            listSongs.clear()
            listSongs.addAll(arr)
        }

        val pos = intent?.getIntExtra(MyAction.ACTION_MUSIC, 0)
        if (pos != null) {
            playMusic(pos)
            pushNotification(pos)
        }
        return START_NOT_STICKY
    }

    private fun playMusic(position: Int) {
        mMediaMusic.release()
        listSongs[position].songUrl?.let { mMediaMusic.setData(it) }
        mMediaMusic.start()
    }

    private fun pushNotification(pos: Int) {
        val musicName = listSongs[pos].title
        val musicSinger = listSongs[pos].author
        val uriImage = listSongs[pos].getUri()
        initMediaSession(pos)

        val notification = NotificationCompat.Builder(this, MyApplication.CHANEL_ID)

        notification.setSmallIcon(R.drawable.ic_music)
            .setContentTitle(musicName)
            .setContentText(musicSinger)
            .setLargeIcon(Utils.createsImageMusic(uriImage, this))
            .addAction(R.drawable.ic_skip_previous, "Previous", null) // #0
            .addAction(R.drawable.ic_pasue, "Pause", null) // #1
            .addAction(R.drawable.ic_skip_next, "Next", null) //2
            .addAction(R.drawable.ic_clear, "Clear", null) //3
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession!!.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )

        startForeground(1, notification.build())

    }

    private fun initMediaSession(position: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager =
                this.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        }
        mediaSession = MediaSessionCompat(this, "Mood Music")
        transportControls = mediaSession!!.controller.transportControls
        mediaSession!!.isActive = true
        mediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        updateMetaData(position)
    }

    private fun updateMetaData(position: Int) {
        mediaSession!!.setMetadata(
            MediaMetadataCompat.Builder()
                .putBitmap(
                    MediaMetadataCompat.METADATA_KEY_ALBUM_ART,
                    Utils.createsImageMusic(listSongs[position].getUri(), this)
                )
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, listSongs[position].author)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, listSongs[position].title)
                .build()
        )
    }
}