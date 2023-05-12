package com.example.vsdapp.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class BaseViewModel: ViewModel() {

    private val mutableEventsFlow = MutableStateFlow<Event<Any>>(Event.empty())
    val eventsFlow : StateFlow<Event<Any>> = mutableEventsFlow

    fun sendEvent(payload: Any) {
        mutableEventsFlow.value = Event(payload)
    }
}