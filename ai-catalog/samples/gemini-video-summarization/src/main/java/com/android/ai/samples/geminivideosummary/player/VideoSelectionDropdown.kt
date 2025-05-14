package com.android.ai.samples.geminivideosummary.player

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A composable function that displays a dropdown menu for selecting a video from a list of options.
 */
@Composable
fun VideoSelectionDropdown(
    selectedVideoUri: Uri?,
    isDropdownExpanded: Boolean,
    videoOptions: List<Pair<String, Uri>>,
    onVideoUriSelected: (Uri) -> Unit,
    onNewVideoUrlChanged: (String) -> Unit,
    onDropdownExpanded: (Boolean) -> Unit
) {
    Box {
        OutlinedTextField(value = selectedVideoUri?.let {
            videoOptions.firstOrNull { it.second == selectedVideoUri }?.first
        } ?: "Select Video",
            onValueChange = { },
            readOnly = true,
            trailingIcon = {
                Icon(imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable { onDropdownExpanded(!isDropdownExpanded) })
            },
            modifier = Modifier.fillMaxWidth()
                .clickable { onDropdownExpanded(!isDropdownExpanded) })

        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { onDropdownExpanded(false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            videoOptions.forEach { (label, uri) ->
                DropdownMenuItem(text = { Text(label) }, onClick = {
                    onVideoUriSelected(uri)
                    onDropdownExpanded(false)
                    onNewVideoUrlChanged("")
                })
            }
        }
    }
}