package com.khiem.musicmp3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.FileNotFoundException
import java.io.InputStream

object Utils {
    fun createsImageMusic(uri : Uri, context: Context): Bitmap {
        var getBitmap: Bitmap? = null
        try {
            val imageStream: InputStream
            try {
                imageStream = context.contentResolver.openInputStream(uri)!!
                getBitmap = BitmapFactory.decodeStream(imageStream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getBitmap ?: getLargeIcon(context)
    }

    private fun getLargeIcon(context: Context): Bitmap {
        return BitmapFactory.decodeResource(context.resources, R.drawable.music_item)
    }
}