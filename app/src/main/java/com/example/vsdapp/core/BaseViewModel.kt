package com.example.vsdapp.core

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class BaseViewModel: ViewModel(), KoinComponent {

    private val mutableEventsFlow = MutableStateFlow<Event<Any>>(Event.empty())
    val eventsFlow : StateFlow<Event<Any>> = mutableEventsFlow

    internal val dataStore: PreferencesDataStore by inject()

    fun sendEvent(payload: Any) {
        mutableEventsFlow.value = Event(payload)
    }
}