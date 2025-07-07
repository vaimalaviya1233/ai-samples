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

package com.android.ai.samples.geminilivetodo.ui

import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.ai.samples.geminilivetodo.data.TodoRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionCallPart
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.LiveSession
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.SpeechConfig
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.Voice
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.liveGenerationConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

@OptIn(PublicPreviewAPI::class)
@HiltViewModel
class TodoScreenViewModel @Inject constructor(private val todoRepository: TodoRepository) : ViewModel() {
    private val TAG = "TodoScreenViewModel"
    
    private var session: LiveSession? = null

    private val _uiState = MutableStateFlow<TodoScreenUiState>(TodoScreenUiState.Initial)
    val uiState: StateFlow<TodoScreenUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            todoRepository.todos.collect { todos ->
                _uiState.update {
                    if (it is TodoScreenUiState.Success) {
                        it.copy(todos = todos)
                    } else {
                        TodoScreenUiState.Success(todos = todos)
                    }
                }
            }
        }
    }

    fun addTodo(taskDescription: String) {
        todoRepository.addTodo(taskDescription)
    }

    fun removeTodo(todoId: Long) {
        todoRepository.removeTodo(todoId)
    }

    fun toggleTodoStatus(todoId: Long) {
        todoRepository.toggleTodoStatus(todoId)
    }

    @RequiresPermission(android.Manifest.permission.RECORD_AUDIO)
    fun toggleLiveSession() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState !is TodoScreenUiState.Success) return@launch

            session?.let {
                if (currentState.isLiveSessionRunning) {
                    it.startAudioConversation(::handleFunctionCall)
                    _uiState.update {
                        if (it is TodoScreenUiState.Success) {
                            it.copy(isLiveSessionRunning = true)
                        } else {
                            it
                        }
                    }
                } else {
                    it.stopAudioConversation()
                    _uiState.update {
                        if (it is TodoScreenUiState.Success) {
                            it.copy(isLiveSessionRunning = false)
                        } else {
                            it
                        }
                    }
                }
            }
        }
    }

    fun initializeGeminiLive() {
        viewModelScope.launch {
            Log.d(TAG, "Start Gemini Live initialization")
            val liveGenerationConfig = liveGenerationConfig {
                speechConfig = SpeechConfig(voice = Voice("FENRIR"))
                responseModality = ResponseModality.AUDIO
            }

            val systemInstruction = content {
                text(
                    """
                **Your Role:** You are a friendly and helpful voice assistant in this app. 
                Your main job is to change update the tasks in the todo list based on user requests.
    
                **Interaction Steps:**
                **Get the task id to remove or toggle a task:** If you need to remove or check/uncheck a task,
                    you'll need to retrieve the list of items in the list first to get the task id. Don't share 
                    the id with the user, just identify the id of the task mentioned and directly pass this id to the 
                    tool.
          
                **Never share the id with the user:** you don't need to share the id with the user. It is 
                    just here to help you perform the check/uncheck and remove operations to the list.
    
                **If Unsure:** If you can't determine the update from the request, politely ask the user to rephrase or try something else.
                    """.trimIndent(),
                )
            }

            val addTodo = FunctionDeclaration(
                "addTodo",
                "Add a task to the todo list",
                mapOf("taskDescription" to Schema.string("A succinct string describing the task")),
            )

            val removeTodo = FunctionDeclaration(
                "removeTodo",
                "Remove a task from the todo list",
                mapOf("todoId" to Schema.string("The id of the task to remove from the todo list")),
            )

            val toggleTodoStatus = FunctionDeclaration(
                "toggleTodoStatus",
                "Change the status of the task",
                mapOf("todoId" to Schema.string("The id of the task to remove from the todo list")),
            )

            val getTodoList = FunctionDeclaration(
                "getTodoList",
                "Get the list of all the tasks in the todo list",
                emptyMap(),
            )

            val generativeModel = Firebase.ai(backend = GenerativeBackend.vertexAI()).liveModel(
                "gemini-2.0-flash-live-preview-04-09",
                generationConfig = liveGenerationConfig,
                systemInstruction = systemInstruction,
                tools = listOf(
                    Tool.functionDeclarations(
                        listOf(getTodoList, addTodo, removeTodo, toggleTodoStatus),
                    ),
                ),
            )

            session = generativeModel.connect()
            Log.d(TAG, "Gemini Live session initialized")
            _uiState.update {
                if (it is TodoScreenUiState.Success) {
                    it.copy(isLiveSessionReady = true)
                } else {
                    it
                }
            }
        }
    }

    private fun handleFunctionCall(functionCall: FunctionCallPart): FunctionResponsePart {
        return when (functionCall.name) {
            "getTodoList" -> {
                val todoList = todoRepository.getTodoList().reversed()
                val response = JsonObject(
                    mapOf(
                        "success" to JsonPrimitive(true),
                        "message" to JsonPrimitive("List of tasks in the todo list: $todoList"),
                    ),
                )
                FunctionResponsePart(functionCall.name, response)
            }
            "addTodo" -> {
                val taskDescription = functionCall.args["taskDescription"]!!.jsonPrimitive.content
                todoRepository.addTodo(taskDescription)
                val response = JsonObject(
                    mapOf(
                        "success" to JsonPrimitive(true),
                        "message" to JsonPrimitive("Task $taskDescription added to the todo list"),
                    ),
                )
                FunctionResponsePart(functionCall.name, response)
            }
            "removeTodo" -> {
                val taskId = functionCall.args["todoId"]!!.jsonPrimitive.long
                todoRepository.removeTodo(taskId)
                val response = JsonObject(
                    mapOf(
                        "success" to JsonPrimitive(true),
                        "message" to JsonPrimitive("Task was removed from the todo list"),
                    ),
                )
                FunctionResponsePart(functionCall.name, response)
            }
            "toggleTodoStatus" -> {
                val taskId = functionCall.args["todoId"]!!.jsonPrimitive.long
                todoRepository.toggleTodoStatus(taskId)
                val response = JsonObject(
                    mapOf(
                        "success" to JsonPrimitive(true),
                        "message" to JsonPrimitive("Task was toggled in the todo list"),
                    ),
                )
                FunctionResponsePart(functionCall.name, response)
            }
            else -> {
                val response = JsonObject(
                    mapOf("error" to JsonPrimitive("Unknown function: ${functionCall.name}")),
                )
                FunctionResponsePart(functionCall.name, response)
            }
        }
    }
}
