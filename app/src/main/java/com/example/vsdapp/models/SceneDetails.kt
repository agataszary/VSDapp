package com.example.vsdapp.models

import com.example.vsdapp.views.PictogramDetails
import com.google.firebase.Timestamp

data class SceneDetails(
    val id: String = "",
    val title: String = "",
    val imageLocation: String = "",
    val imageUrl: String = "",
    val pictograms: List<PictogramDetails> = listOf(),
    val userId: String = "",
    val favourite: Boolean = false,
    val markedByTherapist: Boolean = false,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
