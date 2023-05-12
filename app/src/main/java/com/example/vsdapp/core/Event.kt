package com.example.vsdapp.core


open class Event<out T>(private val content: T?) {

    companion object {
        fun <T> empty(): Event<T> = Event(null)
    }

    var isHandled = false
        private set

    fun getContent(): T? {
        return if (isHandled) {
            null
        } else {
            isHandled = true
            content
        }
    }
}