package com.example.vsdapp.settings

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.vsdapp.core.BaseRepository
import com.example.vsdapp.core.Failure
import com.example.vsdapp.core.QueryStatus
import com.example.vsdapp.core.Success
import com.example.vsdapp.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import org.junit.internal.runners.statements.Fail

class SettingsRepository: BaseRepository() {

    fun updateUserPassword(oldPassword: String, newPassword: String, userModel: UserModel?): QueryStatus {

        return if (BCrypt.verifyer().verify(oldPassword.toCharArray(), userModel?.password).verified) {
            val newBcryptPassword = (BCrypt.withDefaults().hashToString(12, newPassword.toCharArray()))
            user?.updatePassword(newPassword)
            Success(firestoreDb.collection("users").document(user!!.uid).update("password", newBcryptPassword))
        } else {
            Failure("Wprowadź ponownie hasło")
        }


//        firestoreDb.collection("users").whereEqualTo("userId", user?.uid).limit(1).get()
//            .addOnSuccessListener {
//                documentId = it.documents[0].id
//                val data = it.documents[0].toObject(UserModel::class.java)
//                passwordFromDb = data?.password ?: ""
//                queryStatus = QueryStatus.SUCCESS
//                println("Old password success")
//
//            }
//            .addOnFailureListener {
//                println("Old password fail")
//                queryStatus =  QueryStatus.FAIL
//            }
//
//        if(BCrypt.withDefaults().hashToString(12, oldPassword.toCharArray()) == passwordFromDb && queryStatus == QueryStatus.SUCCESS) {
//            user?.updatePassword(newPassword)
//            firestoreDb.collection("users").document(documentId).update("password", newPassword)
//                .addOnSuccessListener {
//                    queryStatus = QueryStatus.SUCCESS
//                }
//                .addOnFailureListener {
//                    println("Update fail")
//                    queryStatus =  QueryStatus.FAIL
//                }
//        } else {
//            println("Wrong password $passwordFromDb")
//            queryStatus = QueryStatus.FAIL
//        }
//
//        return queryStatus
    }

    suspend fun getUserData(): UserModel? {
       return firestoreDb.collection("users").document(user!!.uid).get().await().toObject(UserModel::class.java)
//        firestoreDb.collection("users").document(user!!.uid)
//            .addSnapshotListener { value, error ->
//                println("Id ${user.uid}")
//                println("Valueeeee ${value?.toObject(UserModel::class.java)}")
//                if (error != null) println("Errorrrrr $error")
//            }
//            .addOnSuccessListener { println("Success ${it.data}") }
//            .addOnFailureListener { println("Failure ${it.message}") }
//        return null
    }

    fun updateUserData(userModel: UserModel): Task<Void> {
        return firestoreDb.collection("users").document(user!!.uid).set(userModel)
    }
}