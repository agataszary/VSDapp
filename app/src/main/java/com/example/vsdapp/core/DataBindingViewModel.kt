package com.example.vsdapp.core

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class DataBindingViewModel: BaseViewModel() {
    private val contentVisibilityMutableData = MutableLiveData(View.VISIBLE)
    val contentVisibilityData: LiveData<Int> = contentVisibilityMutableData

    private val progressVisibilityMutableData = MutableLiveData(View.INVISIBLE)
    val progressVisibilityData: LiveData<Int> = progressVisibilityMutableData

    open fun showProgress() {
        println("Show progress")
        progressVisibilityMutableData.value = View.VISIBLE
        contentVisibilityMutableData.value = View.INVISIBLE
    }

    open fun showContent() {
        println("Show content")
        progressVisibilityMutableData.value = View.INVISIBLE
        contentVisibilityMutableData.value = View.VISIBLE
    }
}