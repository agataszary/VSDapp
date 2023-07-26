package com.example.vsdapp.views

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PictogramDetails(
    val imageUrl: String = "",
    var x: Int = 0,
    var y: Int = 0,
    var label: String = "",
    var xRead: Int? = null,
    var yRead: Int? = null,
    var imageSize: Int = 0,
    var viewWidth: Int = 0,
    var viewHeight: Int = 0
): Parcelable
