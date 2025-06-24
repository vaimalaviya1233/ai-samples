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
package com.android.ai.samples.imagen

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImagenScreen(viewModel: ImagenViewModel = hiltViewModel()) {
    val uiState: ImagenUIState by viewModel.uiState.collectAsState()

    ImagenScreen(
        uiState = uiState,
        onGenerateClick = viewModel::generateImage,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ImagenScreen(
    uiState: ImagenUIState,
    onGenerateClick: (String) -> Unit
) {
    val isGenerating = uiState is ImagenUIState.Loading

    Scaffold(
        modifier = Modifier,
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = stringResource(R.string.title_image_generation_screen))
                },
                actions = {
                    SeeCodeButton()
                },
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(innerPadding),
        ) {
            GeneratedContent(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            GenerationInput(
                onGenerateClick = onGenerateClick,
                enabled = !isGenerating,
                modifier = Modifier.fillMaxSize(),
            )

            // Ensure the screen scrolls when the keyboard appears
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
        }
    }
}

@Composable
private fun GeneratedContent(uiState: ImagenUIState, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
    ) {
        when (uiState) {
            ImagenUIState.Initial -> {
                // no-op
            }

            ImagenUIState.Loading -> {
                Text(
                    text = stringResource(R.string.generating_label),
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }

            is ImagenUIState.ImageGenerated -> {
                Image(
                    bitmap = uiState.bitmap.asImageBitmap(),
                    contentDescription = uiState.contentDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is ImagenUIState.Error -> {
                Text(
                    text = uiState.message,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun GenerationInput(onGenerateClick: (String) -> Unit, enabled: Boolean, modifier: Modifier = Modifier) {
    val placeholder = stringResource(R.string.placeholder_prompt)
    var textFieldValue by rememberSaveable { mutableStateOf(placeholder) }

    Column(
        verticalArrangement = spacedBy(8.dp),
        modifier = modifier,
    ) {
        TextField(
            value = textFieldValue,
            onValueChange = { textFieldValue = it },
            label = { Text(stringResource(R.string.prompt_label)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    onGenerateClick(textFieldValue)
                },
            ),
        )
        Button(
            onClick = {
                onGenerateClick(textFieldValue)
            },
            enabled = enabled,
            contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
            modifier = modifier.fillMaxWidth(),
        ) {
            Icon(
                Icons.Default.SmartToy,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize),
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(R.string.generate_button))
        }
    }
}

@Composable
private fun SeeCodeButton() {
    val context = LocalContext.current
    val githubLink = "https://github.com/android/ai-samples/tree/main/ai-catalog/samples/imagen"
    Button(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, githubLink.toUri())
            context.startActivity(intent)
        },
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
    ) {
        Icon(Icons.Filled.Code, contentDescription = null)
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Text(
            text = stringResource(R.string.see_code),
        )
    }
}

@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ImagenScreenPreview() {
    ImagenScreen(
        uiState = ImagenUIState.Initial,
        onGenerateClick = {},
    )
}

@Preview
@Composable
private fun GeneratedContentPreview() {
    GeneratedContent(
        uiState = ImagenUIState.Initial,
        modifier = Modifier.size(400.dp),
    )
}

@Preview
@Composable
private fun GeneratedContentLoadingPreview() {
    GeneratedContent(
        uiState = ImagenUIState.Loading,
        modifier = Modifier.size(400.dp),
    )
}

@Preview
@Composable
private fun GenerationInputPreview() {
    GenerationInput(
        onGenerateClick = {},
        enabled = true,
    )
}
