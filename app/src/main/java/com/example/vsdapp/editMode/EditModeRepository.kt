package com.example.vsdapp.editMode

import android.net.Uri
import com.example.vsdapp.core.BaseRepository
import com.example.vsdapp.models.GetIconsModel
import com.example.vsdapp.models.SceneDetails
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.tasks.await
import javax.xml.parsers.DocumentBuilder

class EditModeRepository: BaseRepository() {

    suspend fun getIcons(searchString: String): List<GetIconsModel> {
        val icons = try {
            api.getIconsForSearchString(searchString)
        } catch (e: Exception) {
            println("Empty response")
            listOf()
        }
        return icons
    }

    suspend fun saveBackgroundImage(fileName: String, fileUri: Uri): Uri {
        val imageRef = scenesImagesRef.child("${user!!.uid}/$fileName")
        val metadata = storageMetadata {
            contentType = "image/jpg"
        }
        return imageRef.putFile(fileUri, metadata).await().storage.downloadUrl.await()
    }

    fun saveSceneDetails(scene: SceneDetails): Pair<Task<Void>, String> {
        val docRef = firestoreDb.collection("scenes").document()
        val sceneToSave = scene.copy(
            userId = user!!.uid,
            id = docRef.id,
            imageLocation = "${user.uid}/${scene.imageLocation}"
        )
        return Pair(docRef.set(sceneToSave), docRef.id)
    }

    fun updateSceneDetails(sceneId: String, scene: SceneDetails): Task<Void> {
        return firestoreDb.collection("scenes").document(sceneId).set(scene)
    }
}