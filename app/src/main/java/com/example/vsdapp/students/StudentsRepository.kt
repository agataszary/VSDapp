package com.example.vsdapp.students

import com.example.vsdapp.core.BaseRepository
import com.example.vsdapp.core.Failure
import com.example.vsdapp.core.QueryStatus
import com.example.vsdapp.core.Success
import com.example.vsdapp.core.SuccessEmpty
import com.example.vsdapp.models.SceneDetails
import com.example.vsdapp.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class StudentsRepository: BaseRepository() {

    suspend fun getSavedUsers(): List<UserModel> {
        val therapist = firestoreDb.collection("users").document(user!!.uid).get().await().toObject(UserModel::class.java)
        val savedStudentsIds = therapist?.savedStudents
        val usersList = mutableListOf<UserModel>()
        savedStudentsIds?.forEach { firestoreDb.collection("users").document(it).get().await().toObject(UserModel::class.java)
            ?.let { user -> usersList.add(user) } }
        return usersList.toList()
    }

    suspend fun getSavedStudentsIds(): List<String> {
        val therapist = firestoreDb.collection("users").document(user!!.uid).get().await().toObject(UserModel::class.java)
        val savedStudentsIds = therapist?.savedStudents
        return savedStudentsIds ?: listOf()
    }

    suspend fun getAllAvailableUsers(): List<UserModel> {
        return firestoreDb.collection("users").get().await().toObjects(UserModel::class.java)
    }

    fun updateSavedUsers(savedUsersList: List<String>): Task<Void> {
        return firestoreDb.collection("users").document(user!!.uid).update("savedStudents", savedUsersList)
    }

    fun saveSharedSceneForUsers(userIdsList: List<String>, scene: SceneDetails){
         for (userId in userIdsList) {
            val docRef = firestoreDb.collection("scenes").document()
            val sceneToSave = scene.copy(
                userId = userId,
                id = docRef.id,
                favourite = false,
                markedByTherapist = false
            )
            docRef.set(sceneToSave)
        }
    }
}