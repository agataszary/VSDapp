package com.example.vsdapp.database

import android.net.Uri
import androidx.core.net.toUri
import com.example.vsdapp.core.BaseRepository
import com.example.vsdapp.models.SceneDetails
import com.example.vsdapp.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FileDownloadTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class StorageRepository: BaseRepository() {

    // STORAGE
     fun getImage(imageLocation: String): Pair<FileDownloadTask, Uri> {
        val tmpFile = File.createTempFile("tmpImageFile", "jpg")
        return Pair(scenesImagesRef.child(imageLocation).getFile(tmpFile), tmpFile.toUri())
    }

    fun getImageForUserId(imageLocation: String, userId: String): Pair<FileDownloadTask, Uri> {
        val tmpFile = File.createTempFile("tmpImageFile", "jpg")
        return Pair(scenesImagesRef.child("$userId/$imageLocation").getFile(tmpFile), tmpFile.toUri())
    }

    // FIRESTORE SCENES

    suspend fun getSceneDetails(sceneId: String): SceneDetails? {
        return firestoreDb.collection("scenes").document(sceneId).get().await().toObject(SceneDetails::class.java)
    }

    suspend fun getAllScenesDetails(): List<SceneDetails> {
        return firestoreDb.collection("scenes").whereEqualTo("userId", user!!.uid).get().await().toObjects(SceneDetails::class.java)
    }

    fun deleteScene(sceneId: String, imageLocation: String) {
        scenesImagesRef.child("${user!!.uid}/$imageLocation").delete()
        firestoreDb.collection("scenes").document(sceneId).delete()
    }

    fun updateSceneBookmarkedField(sceneId: String, value: Boolean): Task<Void> {
        return firestoreDb.collection("scenes").document(sceneId).update("markedByTherapist", value)
    }

    fun updateSceneFavouriteField(sceneId: String, value: Boolean): Task<Void> {
        return firestoreDb.collection("scenes").document(sceneId).update("favourite", value)
    }

    suspend fun getScenesForUserId(userId: String): List<SceneDetails> {
        return firestoreDb.collection("scenes").whereEqualTo("userId", userId).get().await().toObjects(SceneDetails::class.java)
    }

    //FIRESTORE USERS

    suspend fun getUserDataForId(userId: String): UserModel? {
        return firestoreDb.collection("users").document(userId).get().await().toObject(UserModel::class.java)
    }

}