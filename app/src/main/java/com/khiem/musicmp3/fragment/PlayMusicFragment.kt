package com.khiem.musicmp3.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.khiem.musicmp3.R
import com.khiem.musicmp3.Utils
import com.khiem.musicmp3.action.MyAction
import com.khiem.musicmp3.manager.MediaMusicManger
import com.khiem.musicmp3.model.Music
import kotlinx.android.synthetic.main.play_music_fragment.*

class PlayMusicFragment : BaseFragment() {
    private var isPlaying = false
    private var music = Music()
    private var handler = Handler(Looper.myLooper()!!)

    companion object {
        val TAG: String = PlayMusicFragment::class.java.name
    }

    override val layoutResId: Int
        get() = R.layout.play_music_fragment



    override fun onStart() {
        super.onStart()
        context?.let {
            LocalBroadcastManager.getInstance(it).registerReceiver(
                broadcastReceiver,
                IntentFilter(MyAction.ACTION_MUSIC_TO_FRAGMENT)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(broadcastReceiver) }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val bundle = intent?.extras
            music = bundle?.getParcelable(MyAction.ACTION_MUSIC)!!
            isPlaying = bundle.getBoolean(MyAction.ACTION_IS_PLAYING)
            val actionMusic = bundle.getInt(MyAction.ACTION, 0)
            setActionFromService(actionMusic)
        }

    }

    private fun setActionFromService(actionMusic: Int) {
        when (actionMusic){
            MyAction.ACTION_START ->{
                showInfoMusic()
                setStatusIsPlaying()
            }
            MyAction.ACTION_PREVIOUS ->{

            }
            MyAction.ACTION_PAUSE ->{
                setStatusIsPlaying()
            }
            MyAction.ACTION_RESUME ->{
                setStatusIsPlaying()
            }
            MyAction.ACTION_NEXT ->{

            }
            MyAction.ACTION_CLEAR ->{

            }

        }
    }

    private fun sendActionToService(actionMusic: Int){
        val intentService = Intent()
        intentService.action = MyAction.ACTION_MUSIC_TO_SERVICE
        intentService.putExtra(MyAction.ACTION_MUSIC_TO_SERVICE, actionMusic)
        context?.sendBroadcast(intentService)
    }

    private fun showInfoMusic() {
        civ_image_music.setImageBitmap(context?.let { Utils.createsImageMusic(music.getUri(), it) })
        tv_music_name.text = music.title
        tv_music_singer.text = music.author
        tv_total_music.text = music.duration?.let { Utils.formatDuration(it) }
        seekbar_music.max = MediaMusicManger.getDuration()!!

    }


    private fun setStatusIsPlaying(){
        if (isPlaying){
            tv_music_name.isSelected = true
            iv_PlayOrPause.setImageResource(R.drawable.ic_pasue)
            updateSeekbar()
        }
        else{
            tv_music_name.isSelected = false
            iv_PlayOrPause.setImageResource(R.drawable.ic_playy)
        }
    }

    override fun inIView() {
        setOnClick()
    }

    private fun setOnClick() {
        iv_back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        iv_PlayOrPause.setOnClickListener {
            if (isPlaying){
                sendActionToService(MyAction.ACTION_PAUSE)
            }else{
                sendActionToService(MyAction.ACTION_RESUME)
            }
        }
        iv_Previous.setOnClickListener {
            sendActionToService(MyAction.ACTION_PREVIOUS)
        }
        iv_Next.setOnClickListener {
            sendActionToService(MyAction.ACTION_NEXT)
        }
        seekbar_music.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    private fun updateSeekbar() {
        try {
            val currentPos: Int = MediaMusicManger.getCurrentPosition()!!
            seekbar_music.progress = currentPos
            tv_current_music.text = MediaMusicManger.getCurrentPosition()?.let {
                Utils.formatDuration(
                    it
                )
            }
            val runnable = { updateSeekbar() }
            handler.postDelayed(runnable, 1000)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d(TAG, "updateSeekbar: " + e.message)
        }
    }
}