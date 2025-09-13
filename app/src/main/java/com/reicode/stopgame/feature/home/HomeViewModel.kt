package com.reicode.stopgame.feature.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _showCreateDialog = MutableStateFlow(false)
    val showCreateDialog: StateFlow<Boolean> = _showCreateDialog.asStateFlow()

    private val _showJoinDialog = MutableStateFlow(false)
    val showJoinDialog: StateFlow<Boolean> = _showJoinDialog.asStateFlow()

    fun onCreateRoomClicked() {
        _showCreateDialog.value = true
    }

    fun onJoinRoomClicked() {
        _showJoinDialog.value = true
    }

    fun dismissDialogs() {
        _showCreateDialog.value = false
        _showJoinDialog.value = false
    }
}
