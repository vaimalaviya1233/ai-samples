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

package com.android.ai.catalog.ui.domain

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.android.ai.catalog.R
import com.android.ai.samples.geminichatbot.GeminiChatbotScreen
import com.android.ai.samples.geminimultimodal.GeminiMultimodalScreen

class SampleCatalog(
    context: Context
) {

    val list = listOf(
        SampleCatalogItem(
            title = context.getString(R.string.gemini_multimodal_sample_title),
            description = context.getString(R.string.gemini_multimodal_sample_description),
            route = "GeminiMultimodalScreen",
            sampleEntryScreen = { GeminiMultimodalScreen() },
            tags = listOf(SampleTags.FIREBASE, SampleTags.GEMINI_1_5_FLASH)
        ),
        SampleCatalogItem(
            title = context.getString(R.string.gemini_chatbot_sample_title),
            description = context.getString(R.string.gemini_chatbot_sample_description),
            route = "GeminiChitchatScreen",
            sampleEntryScreen = { GeminiChatbotScreen() },
            tags = listOf(SampleTags.FIREBASE, SampleTags.GEMINI_1_5_PRO)
        )
        // To create a new sample entry, add a new SampleCatalogItem here.
    )

}

data class SampleCatalogItem(
    val title: String,
    val description: String,
    val route: String,
    val sampleEntryScreen: @Composable () -> Unit,
    val tags: List<SampleTags> = emptyList()
)

enum class SampleTags(
    val label: String,
    val backgroundColor: Color,
    val textColor: Color
) {
    FIREBASE("Firebase",Color(0xFFFF9100), Color.White),
    GEMINI_1_5_PRO("Gemini 1.5 Pro", Color(0xFF4285F4), Color.White),
    GEMINI_1_5_FLASH("Gemini 1.5 Flash", Color(0xFF4285F4), Color.White)
}

