package com.khiem.musicmp3.fragment

import com.khiem.musicmp3.R
import kotlinx.android.synthetic.main.play_music_fragment.*

class PlayMusicFragment : BaseFragment() {
    companion object {
        val TAG: String = PlayMusicFragment::class.java.name
    }

    override val layoutResId: Int
        get() = R.layout.play_music_fragment

    override fun inIView() {
        setOnClick()
    }

    private fun setOnClick() {
        iv_back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}