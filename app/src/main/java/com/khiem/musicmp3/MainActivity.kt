package com.khiem.musicmp3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.khiem.musicmp3.fragment.ListMusicFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        changeFragment()
    }

    private fun changeFragment(){
        val manager = supportFragmentManager
        val tran = manager.beginTransaction()
        val fr = ListMusicFragment()
        tran.replace(R.id.fl_music, fr)
        tran.commit()
    }
}