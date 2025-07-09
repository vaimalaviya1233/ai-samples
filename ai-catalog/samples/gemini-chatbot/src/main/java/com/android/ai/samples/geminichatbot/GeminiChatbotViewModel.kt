/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.ai.samples.geminichatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GeminiChatbotUiState {
    data object Initial : GeminiChatbotUiState()
    data class Generating(val messages: List<ChatMessage>) : GeminiChatbotUiState()
    data class Success(val messages: List<ChatMessage>) : GeminiChatbotUiState()
    data class Error(
        val errorMessage: String,
        val messages: List<ChatMessage>,
    ) : GeminiChatbotUiState()
}

class GeminiChatbotViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<GeminiChatbotUiState>(GeminiChatbotUiState.Initial)
    val uiState: StateFlow<GeminiChatbotUiState> = _uiState.asStateFlow()

    private val generativeModel by lazy {
        Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            "gemini-2.0-flash",
            generationConfig = generationConfig {
                temperature = 0.9f
                topK = 32
                topP = 1f
                maxOutputTokens = 4096
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
            ),
            systemInstruction = content {
                text("""You are a friendly assistant. Keep your response short.""")
            },
        )
    }

    private val chat = generativeModel.startChat()

    fun sendMessage(message: String) {
        val currentMessages =
            when (val state = _uiState.value) {
                is GeminiChatbotUiState.Success -> state.messages
                is GeminiChatbotUiState.Error -> state.messages
                else -> emptyList()
            }

        val newMessages =
            currentMessages.toMutableList().apply {
                add(ChatMessage(message, System.currentTimeMillis(), false, null))
            }
        viewModelScope.launch {
            try {
                _uiState.value = GeminiChatbotUiState.Generating(newMessages)

                val response = chat.sendMessage(message)
                response.text?.let {
                    newMessages.add(
                        ChatMessage(
                            it.trim(),
                            System.currentTimeMillis(),
                            true,
                            null,
                        ),
                    )
                }
                _uiState.value = GeminiChatbotUiState.Success(newMessages)
            } catch (e: Exception) {
                _uiState.value =
                    GeminiChatbotUiState.Error(
                        e.localizedMessage ?: "Something went wrong, try again",
                        newMessages,
                    )
            }
        }
    }

    fun dismissError() {
        val errorState = _uiState.value as? GeminiChatbotUiState.Error ?: return
        _uiState.value = GeminiChatbotUiState.Success(errorState.messages)
    }
}
