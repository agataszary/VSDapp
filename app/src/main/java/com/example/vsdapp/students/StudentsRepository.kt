package com.example.vsdapp.students

import com.example.vsdapp.core.BaseRepository
import com.example.vsdapp.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Filter
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
}