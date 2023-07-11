package com.example.vsdapp.models

data class UserModel(
    var userId: String = "",
    var mainName: String = "",
    var mainSurname: String = "",
    var childName: String? = null,
    var childSurname: String? = null,
    var emailAddress: String = "",
    var therapistAccount: Boolean = false,
    var password: String = ""
)
