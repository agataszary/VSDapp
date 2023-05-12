package com.example.vsdapp.core

sealed class ViewState {
    object Content : ViewState()
    object Progress: ViewState()
    object Empty: ViewState()
}