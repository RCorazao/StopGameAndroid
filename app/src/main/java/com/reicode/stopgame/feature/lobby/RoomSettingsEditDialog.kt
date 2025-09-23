package com.reicode.stopgame.feature.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reicode.stopgame.R
import com.reicode.stopgame.feature.lobby.data.RoomSettings
import com.reicode.stopgame.feature.lobby.data.RoomSettingsValidator
import com.reicode.stopgame.realtime.dto.TopicDto

/**
 * Room Settings Edit Dialog composable with basic structure and navigation.
 * Provides input fields for max players, max rounds, round duration, and voting duration
 * with real-time validation and error state display.
 * 
 * Requirements: 2.1, 3.1, 4.1, 5.1
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomSettingsEditDialog(
    currentSettings: RoomSettings,
    isLoading: Boolean = false,
    error: String? = null,
    onDismiss: () -> Unit,
    onSave: (RoomSettings) -> Unit
) {
    val context = LocalContext.current
    
    // Dialog state management with remember and mutableStateOf for edit mode
    var editableSettings by remember { mutableStateOf(currentSettings) }
    var validationErrors by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    
    // Topic management state
    var topicInputText by remember { mutableStateOf("") }
    var topicInputError by remember { mutableStateOf<String?>(null) }
    var showRemoveConfirmation by remember { mutableStateOf<TopicDto?>(null) }
    
    // Update validation errors whenever settings change
    LaunchedEffect(editableSettings) {
        validationErrors = RoomSettingsValidator.validateRoomSettings(editableSettings, context)
    }
    
    BasicAlertDialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Dialog Header
                Text(
                    text = stringResource(R.string.edit_room_settings),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Max Players Input Field
                SettingsInputField(
                    label = stringResource(R.string.max_players),
                    value = editableSettings.maxPlayers,
                    onValueChange = { newValue ->
                        editableSettings = editableSettings.copy(maxPlayers = newValue)
                    },
                    errorMessage = validationErrors["maxPlayers"],
                    minValue = 2,
                    maxValue = 10
                )
                
                // Max Rounds Input Field
                SettingsInputField(
                    label = stringResource(R.string.max_rounds),
                    value = editableSettings.maxRounds,
                    onValueChange = { newValue ->
                        editableSettings = editableSettings.copy(maxRounds = newValue)
                    },
                    errorMessage = validationErrors["maxRounds"],
                    minValue = 1,
                    maxValue = 5
                )
                
                // Round Duration Input Field
                SettingsInputField(
                    label = stringResource(R.string.round_duration),
                    value = editableSettings.roundDurationSeconds,
                    onValueChange = { newValue ->
                        editableSettings = editableSettings.copy(roundDurationSeconds = newValue)
                    },
                    errorMessage = validationErrors["roundDuration"],
                    minValue = 30,
                    maxValue = 300,
                    step = 15
                )
                
                // Voting Duration Input Field
                SettingsInputField(
                    label = stringResource(R.string.voting_duration),
                    value = editableSettings.votingDurationSeconds,
                    onValueChange = { newValue ->
                        editableSettings = editableSettings.copy(votingDurationSeconds = newValue)
                    },
                    errorMessage = validationErrors["votingDuration"],
                    minValue = 15,
                    maxValue = 120,
                    step = 5
                )
                
                // Topics Management Section
                TopicsManagementSection(
                    topics = editableSettings.topics,
                    topicInputText = topicInputText,
                    topicInputError = topicInputError,
                    onTopicInputChange = { newText ->
                        topicInputText = newText
                        // Clear error when user starts typing
                        if (topicInputError != null) {
                            topicInputError = null
                        }
                    },
                    onAddTopic = {
                        val validationError = RoomSettingsValidator.validateTopicName(topicInputText, editableSettings.topics, context)
                        if (validationError != null) {
                            topicInputError = validationError
                        } else {
                            // Create new topic with temporary ID
                            val newTopic = TopicDto(
                                id = "temp_${System.currentTimeMillis()}",
                                name = topicInputText.trim(),
                                isDefault = false,
                                createdByUserId = "host_user",
                                createdAt = System.currentTimeMillis().toString()
                            )
                            editableSettings = editableSettings.copy(
                                topics = editableSettings.topics + newTopic
                            )
                            topicInputText = ""
                            topicInputError = null
                        }
                    },
                    onRemoveTopic = { topic ->
                        val validationError = RoomSettingsValidator.validateTopicRemoval(
                            editableSettings.topics,
                            topic,
                            context
                        )

                        if (validationError == null) {
                            editableSettings = editableSettings.copy(
                                topics = editableSettings.topics.filter { it.id != topic.id }
                            )
                        }
                    }
                )
                
                // Error Display
                if (error != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 14.sp
                        )
                    }
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isLoading,
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            stringResource(R.string.cancel),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    // Save Button - disabled when validation errors exist or loading
                    Button(
                        onClick = { onSave(editableSettings) },
                        enabled = validationErrors.isEmpty() && !isLoading,
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            Text(
                                stringResource(R.string.saving),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        } else {
                            Text(
                                stringResource(R.string.save),
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Topic removal confirmation dialog
    showRemoveConfirmation?.let { topicToRemove ->
        AlertDialog(
            onDismissRequest = { showRemoveConfirmation = null },
            title = { Text(stringResource(R.string.remove_topic_title)) },
            text = { Text(stringResource(R.string.remove_topic_message, topicToRemove.name)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val validationError = RoomSettingsValidator.validateTopicRemoval(
                            editableSettings.topics, 
                            topicToRemove,
                            context
                        )
                        if (validationError == null) {
                            editableSettings = editableSettings.copy(
                                topics = editableSettings.topics.filter { it.id != topicToRemove.id }
                            )
                        }
                        showRemoveConfirmation = null
                    }
                ) {
                    Text(stringResource(R.string.remove))
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveConfirmation = null }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

/**
 * Reusable input field component for numeric settings with stepper controls.
 * Implements real-time validation with error state display.
 */
@Composable
private fun SettingsInputField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    errorMessage: String?,
    minValue: Int,
    maxValue: Int,
    step: Int = 1
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Decrease Button
            IconButton(
                onClick = { 
                    val newValue = (value - step).coerceAtLeast(minValue)
                    onValueChange(newValue)
                },
                enabled = value > minValue
            ) {
                Text(
                    text = "−",
                    fontSize = 18.sp,
                    color = if (value > minValue) Color(0xFF6B7280) else Color(0xFFD1D5DB)
                )
            }
            
            // Value Display/Input
            OutlinedTextField(
                value = value.toString(),
                onValueChange = { newValueStr ->
                    newValueStr.toIntOrNull()?.let { newValue ->
                        val clampedValue = newValue.coerceIn(minValue, maxValue)
                        onValueChange(clampedValue)
                    }
                },
                modifier = Modifier.fillMaxWidth().weight(1f),
                textStyle = LocalTextStyle.current.copy(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                isError = errorMessage != null,
                shape = RoundedCornerShape(12.dp)
            )
            
            // Increase Button
            IconButton(
                onClick = { 
                    val newValue = (value + step).coerceAtMost(maxValue)
                    onValueChange(newValue)
                },
                enabled = value < maxValue
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.increase_label, label),
                    tint = if (value < maxValue) Color(0xFF6B7280) else Color(0xFFD1D5DB)
                )
            }
        }
        
        // Error Message Display
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Helper text showing valid range
        if (errorMessage == null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.range_label, minValue, maxValue),
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Topics management section for adding and removing topics.
 * Implements topic input field with character limit validation and topic removal interface.
 * Requirements: 6.1, 6.2, 6.3, 6.4, 6.5, 7.1, 7.2, 7.3, 7.4
 */
@Composable
private fun TopicsManagementSection(
    topics: List<TopicDto>,
    topicInputText: String,
    topicInputError: String?,
    onTopicInputChange: (String) -> Unit,
    onAddTopic: () -> Unit,
    onRemoveTopic: (TopicDto) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.topics),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Add topic input field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = topicInputText,
                onValueChange = onTopicInputChange,
                modifier = Modifier.fillMaxWidth().weight(1f),
                placeholder = { Text(stringResource(R.string.enter_topic_name)) },
                isError = topicInputError != null,
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default
            )
            
            IconButton(
                onClick = onAddTopic,
                enabled = topicInputText.trim().isNotEmpty()
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_topic),
                    tint = if (topicInputText.trim().isNotEmpty()) Color(0xFF10B981) else Color(0xFFD1D5DB)
                )
            }
        }
        
        // Error message for topic input
        if (topicInputError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = topicInputError,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Helper text showing character limit
        if (topicInputError == null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.max_characters),
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Current topics display with remove functionality
        if (topics.isNotEmpty()) {
            TopicsFlowLayout(
                topics = topics,
                isEditMode = true,
                onRemoveTopic = onRemoveTopic
            )
        } else {
            Text(
                text = stringResource(R.string.no_topics_added),
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoomSettingsEditDialogPreview() {
    val sampleTopics = listOf(
        TopicDto(
            id = "1",
            name = "Animals",
            isDefault = true,
            createdByUserId = "host1",
            createdAt = "2024-01-01T10:00:00Z"
        ),
        TopicDto(
            id = "2",
            name = "Countries",
            isDefault = true,
            createdByUserId = "host1",
            createdAt = "2024-01-01T10:00:00Z"
        )
    )
    
    val sampleSettings = RoomSettings(
        maxPlayers = 6,
        maxRounds = 5,
        roundDurationSeconds = 60,
        votingDurationSeconds = 30,
        topics = sampleTopics
    )
    
    MaterialTheme {
        RoomSettingsEditDialog(
            currentSettings = sampleSettings,
            isLoading = false,
            onDismiss = {},
            onSave = {}
        )
    }
}