package com.example.vsdapp.core

import com.google.android.gms.tasks.Task


//enum class QueryStatus {
//    SUCCESS, FAIL
//}

sealed class QueryStatus()

class Success(val task: Task<Void>): QueryStatus()
class Failure(val message: String? = null): QueryStatus()