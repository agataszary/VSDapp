package com.example.vsdapp.models

import com.example.vsdapp.views.PictogramDetails

data class SceneDetails(
    val id: String = "",
    val title: String = "",
    val imageLocation: String = "",
    val imageUrl: String = "",
    val pictograms: List<PictogramDetails> = listOf(),
    val userId: String = "",
    val favourite: Boolean = false,
    val markedByTherapist: Boolean = false
)
