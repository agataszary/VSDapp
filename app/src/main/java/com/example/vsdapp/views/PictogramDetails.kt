package com.example.vsdapp.views

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PictogramDetails(
    val imageUrl: String,
    var x: Int,
    var y: Int,
    var label: String
): Parcelable
