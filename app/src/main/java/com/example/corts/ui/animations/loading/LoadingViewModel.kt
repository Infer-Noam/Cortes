package com.example.corts.ui.animations.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class LoadingViewModel : ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> get() = _loading

    fun startLoading() {
        _loading.value = true
    }

    fun stopLoading() {
        viewModelScope.launch {
            _loading.value = false
        }
    }
}
