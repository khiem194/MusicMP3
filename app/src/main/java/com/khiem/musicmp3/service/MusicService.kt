package com.khiem.musicmp3.service

import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.session.MediaSessionManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.khiem.musicmp3.MainActivity
import com.khiem.musicmp3.MyApplication
import com.khiem.musicmp3.R
import com.khiem.musicmp3.Utils
import com.khiem.musicmp3.action.MyAction
import com.khiem.musicmp3.broadcastReceiver.MusicReceiver
import com.khiem.musicmp3.manager.MediaMusicManger
import com.khiem.musicmp3.model.Music


@Suppress("DEPRECATION")
class MusicService : Service() {
    private val listSongs = mutableListOf<Music>()
    private val mMediaMusic = MediaMusicManger
    private var isPlaying : Boolean = false
    private var currentPosition = -1
    private var mediaSession: MediaSessionCompat? = null
    private var mediaSessionManager: MediaSessionManager? = null
    private var transportControls: MediaControllerCompat.TransportControls? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter()
        intentFilter.addAction(MyAction.ACTION_MUSIC_TO_SERVICE)
        registerReceiver(broadcastReceiver, intentFilter)
        Log.d("Mood Music MP3", "MusicService onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Mood Music MP3", "MusicService onDestroy")
        unregisterReceiver(broadcastReceiver)
        mMediaMusic.release()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val myType = object : TypeToken<List<Music>>() {}.type
        val arr = Gson().fromJson<List<Music>>(
            intent?.getStringExtra(MyAction.ACTION_LIST_MUSIC),
            myType
        )
        if (arr != null) {
            listSongs.clear()
            listSongs.addAll(arr)
        }

        val pos = intent?.getIntExtra(MyAction.ACTION_MUSIC, 0)
        if (pos != null) {
            currentPosition = pos
            playMusic(pos)
            pushNotification(pos)
        }
        return START_NOT_STICKY
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val actionMusic = intent?.getIntExtra(MyAction.ACTION_MUSIC_TO_SERVICE, 0)
            if (actionMusic != null) {
                when (actionMusic) {
                    MyAction.ACTION_PREVIOUS -> {
                        if (currentPosition <= 0){
                            currentPosition = listSongs.size -1
                        }
                        else{
                            currentPosition--
                        }
                        changeCurrentPositionMusic(currentPosition)
                    }
                    MyAction.ACTION_PAUSE -> {
                        pauseMusic()
                    }
                    MyAction.ACTION_RESUME -> {
                        resumeMusic()
                    }
                    MyAction.ACTION_NEXT -> {
                        if(currentPosition < listSongs.size -1){
                            currentPosition++
                        }
                        else{
                            currentPosition = 0
                        }
                        changeCurrentPositionMusic(currentPosition)
                    }
                    MyAction.ACTION_CLEAR -> {
                        stopSelf()
                        sendActionToPlayMusicFragment(MyAction.ACTION_CLEAR)
                    }
                }
            }
        }
    }

    private fun changeCurrentPositionMusic(position: Int) {
        playMusic(position)
        isPlaying = true
        pushNotification(position)
    }

    private fun resumeMusic() {
        if (!isPlaying){
            mMediaMusic.start()
            isPlaying = true
            pushNotification(currentPosition)
            sendActionToPlayMusicFragment(MyAction.ACTION_RESUME)
        }
    }

    private fun pauseMusic() {
        if (isPlaying){
            mMediaMusic.pause()
            isPlaying = false
            pushNotification(currentPosition)
            sendActionToPlayMusicFragment(MyAction.ACTION_PAUSE)
        }
    }

    private fun playMusic(position: Int) {
        mMediaMusic.release()
         listSongs[position].songUrl?.let { mMediaMusic.setData(it) }
        mMediaMusic.start()
        isPlaying = true
        sendActionToPlayMusicFragment(MyAction.ACTION_START)
    }

    private fun sendActionToPlayMusicFragment(action: Int){
        val intent = Intent(MyAction.ACTION_MUSIC_TO_FRAGMENT)
        val bundle = Bundle()
        bundle.putParcelable(MyAction.ACTION_MUSIC, listSongs[currentPosition])
        bundle.putInt(MyAction.ACTION, action)
        bundle.putBoolean(MyAction.ACTION_IS_PLAYING, isPlaying)

        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)


    }

    private fun pushNotification(pos: Int) {
        val musicName = listSongs[pos].title
        val musicSinger = listSongs[pos].author
        val uriImage = listSongs[pos].getUri()
        initMediaSession(pos)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 1,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, MyApplication.CHANEL_ID)

        notificationBuilder.setSmallIcon(R.drawable.ic_music)
            .setContentTitle(musicName)
            .setContentText(musicSinger)
            .setLargeIcon(Utils.createsImageMusic(uriImage, this))
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession!!.sessionToken)
                    .setShowActionsInCompactView(0, 1, 2)
            )
        if (isPlaying){
            notificationBuilder
                .addAction(R.drawable.ic_skip_previous, "Previous", getPendingIntent(this, MyAction.ACTION_PREVIOUS)) // #0
                .addAction(R.drawable.ic_pause_notification, "Pause", getPendingIntent(this, MyAction.ACTION_PAUSE)) // #1
                .addAction(R.drawable.ic_skip_next, "Next", getPendingIntent(this, MyAction.ACTION_NEXT)) //2
                .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(this, MyAction.ACTION_CLEAR)) //3
        }
        else{
            notificationBuilder
                .addAction(R.drawable.ic_skip_previous, "Previous", getPendingIntent(this, MyAction.ACTION_PREVIOUS)) // #0
                .addAction(R.drawable.ic_play_notification, "Play", getPendingIntent(this, MyAction.ACTION_RESUME)) // #1
                .addAction(R.drawable.ic_skip_next, "Next", getPendingIntent(this, MyAction.ACTION_NEXT)) //2
                .addAction(R.drawable.ic_clear, "Clear", getPendingIntent(this, MyAction.ACTION_CLEAR)) //3
        }

        val notification = notificationBuilder.build()
        startForeground(1, notification)

    }

    private fun getPendingIntent(context: Context, action: Int): PendingIntent {
        val intent = Intent(this, MusicReceiver::class.java)
        intent.putExtra(MyAction.ACTION_MUSIC, action)

        return PendingIntent.getBroadcast(
            context.applicationContext,
            action,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

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