package com.khiem.musicmp3.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.khiem.musicmp3.R
import com.khiem.musicmp3.model.Music
import kotlinx.android.synthetic.main.item_music.view.*

class MusicAdapter(var listener: IMusic) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_music,

            parent, false
        )
        return MusicViewHolder(itemView)
    }

    private fun setImage(iv: ImageView, uri: Uri?) {
        Glide.with(iv.context)
            .load(uri)
            .apply(
                RequestOptions.placeholderOf(R.drawable.music_item)
                    .error(R.drawable.music_item)
            )
            .into(iv)
    }


    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val currentItem = listener.getData(position)
        setImage(holder.iv, currentItem.getUri())
        holder.tvMusicName.text = currentItem.title
        holder.tvMusicSinger.text = currentItem.author
    }

    override fun getItemCount() = listener.getCount()

    inner class MusicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val iv: ImageView = itemView.iv_music
        val tvMusicName: TextView = itemView.tv_MusicName
        val tvMusicSinger: TextView = itemView.tv_MusicSinger

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            listener.onItemClick(layoutPosition)
        }
    }

    interface IMusic {
        fun onItemClick(position: Int)
        fun getCount(): Int
        fun getData(position: Int): Music
    }
}