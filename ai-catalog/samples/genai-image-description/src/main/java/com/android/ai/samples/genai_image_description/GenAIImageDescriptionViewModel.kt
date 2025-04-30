/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.android.ai.samples.genai_image_description

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GenAIImageDescriptionViewModel @Inject constructor() : ViewModel() {
    private val _resultGenerated = MutableStateFlow("")
    val resultGenerated: StateFlow<String> = _resultGenerated

    fun getImageDescription(bitmap: Bitmap?, context: Context) {
        if (bitmap == null) {
            _resultGenerated.value = "No image selected"
            return
        }

        _resultGenerated.value = "TODO"
    }
}