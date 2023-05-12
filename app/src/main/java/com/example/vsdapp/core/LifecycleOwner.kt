package com.example.vsdapp.core

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

internal fun LifecycleOwner.runEventsCollector(
    viewModel: BaseViewModel,
    onEvent: (Event<Any>) -> Unit
) {
    lifecycleScope.launch {
        viewModel.eventsFlow.collect{ event ->
            if (!event.isHandled) onEvent(event)
        }
    }
}