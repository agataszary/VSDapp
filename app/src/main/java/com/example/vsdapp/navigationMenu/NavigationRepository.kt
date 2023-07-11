package com.example.vsdapp.navigationMenu

import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.vsdapp.core.BaseRepository
import kotlinx.coroutines.tasks.await

class NavigationRepository: BaseRepository() {

    suspend fun checkPassword(passwordValue: String): Boolean {
        val pass = firestoreDb.collection("users").document(user!!.uid).get().await().get("password")
        return if (pass != null) {
            BCrypt.verifyer().verify(passwordValue.toCharArray(), pass.toString()).verified
        } else false
    }
}