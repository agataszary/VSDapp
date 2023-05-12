package com.example.vsdapp.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class ComposeViewModel: BaseViewModel() {

    private val viewStateMutableFlow = MutableStateFlow<ViewState>(ViewState.Empty)
    val viewStateFlow: StateFlow<ViewState> = viewStateMutableFlow

    private fun setViewState(viewState: ViewState) {
        viewStateMutableFlow.value = viewState
    }

    open fun showProgress() {
        if (viewStateMutableFlow.value !is ViewState.Progress) setViewState(ViewState.Progress)
    }

    open fun showContent() {
        if (viewStateMutableFlow.value !is ViewState.Content) setViewState(ViewState.Content)
    }

}