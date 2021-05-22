package com.khiem.musicmp3.fragment

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothGattCharacteristic
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.khiem.musicmp3.R
import com.khiem.musicmp3.adapter.MusicAdapter
import com.khiem.musicmp3.model.Music
import kotlinx.android.synthetic.main.fragment_list_music.*

class ListMusicFragment() : BaseFragment(), MusicAdapter.IMusic {
    private var listSongs = mutableListOf<Music>()

    override val layoutResId: Int
        get() = R.layout.fragment_list_music

    override fun inIView() {
        checkPermission()
    }

    override fun onItemClick(position: Int) {


    }

    private fun checkPermission() {
        val READ_EXTERNAL_PERMISSION =
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        if (READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                BluetoothGattCharacteristic.PERMISSION_READ
            )
        } else loadMusicFromLocal()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            BluetoothGattCharacteristic.PERMISSION_READ -> {
                if (grantResults.isNotEmpty() && permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(
                            context,
                            "Please allow storage permission",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        loadMusicFromLocal()
                    }
                }
            }
        }
    }

    private fun loadMusicFromLocal() {
        listSongs.clear()
        val allSongUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor? =
            requireContext().contentResolver.query(allSongUri, null, "is_music != 0", null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val songUrl: String =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val songAuthor: String? =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val songName: String =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val albumId: Long =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    val uriImage = "content://media/external/audio/albumart/$albumId"

                    val duration: Long =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    listSongs.add(Music(songName, songAuthor, songUrl, albumId, uriImage, duration))
                } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        rcv_listMusic.layoutManager = LinearLayoutManager(context)
        rcv_listMusic.adapter = MusicAdapter(listSongs, this)
    }
}