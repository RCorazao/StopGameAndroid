package com.reicode.stopgame.shared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.reicode.stopgame.realtime.SignalRService
import com.reicode.stopgame.realtime.dto.ChatMessageDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val signalRService: SignalRService
) : ViewModel() {

    // Expose chat state from SignalR service
    val chatMessages: StateFlow<List<ChatMessageDto>> = signalRService.chatMessages
    val unreadMessageCount: StateFlow<Int> = signalRService.unreadMessageCount

    fun sendMessage(message: String) {
        viewModelScope.launch {
            try {
                signalRService.sendChat(message)
            } catch (e: Exception) {
                // Error handling could be added here if needed
            }
        }
    }

    fun markMessagesAsRead() {
        signalRService.markMessagesAsRead()
    }

    fun setChatOpen(isOpen: Boolean) {
        signalRService.setChatOpen(isOpen)
    }
}