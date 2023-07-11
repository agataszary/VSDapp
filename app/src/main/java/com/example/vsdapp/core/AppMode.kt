package com.example.vsdapp.core

enum class AppMode {
    PARENTAL_MODE, CHILD_MODE, THERAPIST_MODE, NONE;

    companion object {
        fun fromValue(value: Int): AppMode {
            return when(value) {
                0 -> PARENTAL_MODE
                1 -> CHILD_MODE
                2 -> THERAPIST_MODE
                else -> NONE
            }
        }

        fun toValue(appMode: AppMode): Int {
            return when(appMode) {
                PARENTAL_MODE -> 0
                CHILD_MODE -> 1
                THERAPIST_MODE -> 2
                NONE -> -1
            }
        }
    }

    fun toInt(): Int {
        return AppMode.toValue(this)
    }
}