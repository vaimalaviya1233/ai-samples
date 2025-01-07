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

package com.android.ai.samples.geminimultimodal

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun GeminiMultimodalScreen(
    viewModel: GeminiMultimodalViewModel = hiltViewModel(),
) {
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val textResponse by viewModel.textGenerated.collectAsState()
    val isGenerating by viewModel.isGenerating.observeAsState(false)
    var pictureAvailable by remember { mutableStateOf(false) }

    val promptPlaceHolder = stringResource(id = R.string.geminimultimodal_prompt_placeholder)
    var editTextValue by remember {
        mutableStateOf(promptPlaceHolder)
    }

    // Get the picture taken by the camera
    val resultLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                if (result.data != null) {
                    bitmap = result.data?.extras?.get("data") as Bitmap
                    pictureAvailable = true
                }
            }
        }

    Scaffold {
        Column (
            Modifier
                .padding(12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(35.dp))
            Text(
                text = "Gemini Multimodal example",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(4.dp)
            )
            Card(
                border = BorderStroke(
                    2.dp,
                    MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .size(
                        width = 450.dp,
                        height = 450.dp
                    )
            ) {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Picture",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Row {
                Button (
                    onClick = {
                        resultLauncher.launch(cameraIntent)
                    },
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                }
            }
            Spacer(modifier = Modifier
                .height(30.dp)
                .padding(12.dp))

            TextField(
                value = editTextValue,
                onValueChange = { editTextValue = it },
                label = { Text("Prompt") }
            )
            Button (
                onClick = {
                    if (bitmap!=null) {
                        viewModel.generate(bitmap!!, editTextValue)
                    }
                },
                enabled = !isGenerating && pictureAvailable
            ) {
                Icon(Icons.Default.SmartToy, contentDescription = "Robot")
                Text(modifier = Modifier.padding(start = 8.dp), text = "Generate")
            }

            Spacer(modifier = Modifier
                .height(30.dp)
                .padding(12.dp))

            if (isGenerating){
                Text(
                    text = stringResource(R.string.geminimultimodal_generating)
                )
            } else {
                Text(
                    text = textResponse
                )
            }

    }

    }
}