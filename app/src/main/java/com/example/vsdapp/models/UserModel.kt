package com.example.vsdapp.models

data class UserModel(
    val userId: String,
    val mainName: String,
    val mainSurname: String,
    val childName: String? = null,
    val childSurname: String? = null,
    val emailAddress: String,
    val therapistAccount: Boolean,
    val password: String
)
