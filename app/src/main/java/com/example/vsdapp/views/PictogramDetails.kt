package com.example.vsdapp.views

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PictogramDetails(
    val imageUrl: String,
    var x: Int,
    var y: Int,
    var label: String,
    var xRead: Int? = null,
    var yRead: Int? = null
): Parcelable
