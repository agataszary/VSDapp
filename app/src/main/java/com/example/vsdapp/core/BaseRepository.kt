package com.example.vsdapp.core

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

abstract class BaseRepository {
    val api = Api.createApi()
    val firestoreDb = Firebase.firestore
    val user = Firebase.auth.currentUser
    val scenesImagesRef = Firebase.storage.reference.child("scenes/")
}