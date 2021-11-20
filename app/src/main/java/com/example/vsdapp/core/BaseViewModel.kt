package com.example.vsdapp.core

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel: ViewModel() {

    val events = MutableLiveData<Event<*>>()

    private val contentVisibilityMutableData = MutableLiveData(View.VISIBLE)
    val contentVisibilityData: LiveData<Int> = contentVisibilityMutableData

    private val progressVisibilityMutableData = MutableLiveData(View.INVISIBLE)
    val progressVisibilityData: LiveData<Int> = progressVisibilityMutableData

    fun sendEvent(payload: Any) {
        events.value = Event(payload)
    }

    open fun showContent() {
        contentVisibilityMutableData.value = View.VISIBLE
        progressVisibilityMutableData.value = View.INVISIBLE
    }

    open fun showProgress() {
        contentVisibilityMutableData.value = View.INVISIBLE
        progressVisibilityMutableData.value = View.VISIBLE
    }
}