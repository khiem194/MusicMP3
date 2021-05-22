package com.khiem.musicmp3.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Music(
    var title: String? = null,
    var author: String? = null,
    var songUrl: String? = null,
    var albumID: Long? = null,
    var uriImage: String? = null,
    var duration: Long? = null
) : Parcelable {
    fun getUri() : Uri{
        return Uri.parse(uriImage)
    }
}