package com.example.vsdapp.register

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.vsdapp.core.BaseRepository
import com.example.vsdapp.core.sha512
import com.example.vsdapp.models.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference

class RegisterRepository: BaseRepository() {

    fun addNewUser(user: UserModel): Task<Void> {
        val hashedPassword = BCrypt.withDefaults().hashToString(12, user.password.toCharArray())
        val preparedUser = user.copy(
            password = hashedPassword,
            childName = convertEmptyStringToNull(user.childName),
            childSurname = convertEmptyStringToNull(user.childSurname)
        )
        return firestoreDb.collection("users")
            .document(preparedUser.userId)
            .set(preparedUser)
    }

    private fun convertEmptyStringToNull(value: String?): String? {
        return if (value == "") null else value
    }
}