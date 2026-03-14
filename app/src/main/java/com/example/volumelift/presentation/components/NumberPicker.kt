package com.example.volumelift.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private fun formatWeight(value: Double): String {
    if (value == 0.0) return ""
    return if (value == value.toLong().toDouble()) value.toLong().toString()
    else String.format("%.1f", value)
}

@Composable
fun WeightPicker(
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    step: Double = 2.5
) {
    val focusManager = LocalFocusManager.current
    // Local text state - only synced FROM parent when not focused
    var isFocused by remember { mutableStateOf(false) }
    var localText by remember { mutableStateOf(formatWeight(value)) }

    // Update local text from parent only when NOT focused
    if (!isFocused) {
        localText = formatWeight(value)
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = { if (value >= step) onValueChange(value - step) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease weight")
        }
        OutlinedTextField(
            value = localText,
            onValueChange = { newText ->
                // Allow empty, digits, and one decimal point
                if (newText.isEmpty() || newText.matches(Regex("^\\d*\\.?\\d*$"))) {
                    localText = newText
                }
            },
            modifier = Modifier
                .widthIn(min = 64.dp, max = 88.dp)
                .onFocusChanged { focusState ->
                    if (isFocused && !focusState.isFocused) {
                        // Lost focus - commit to parent
                        val parsed = localText.toDoubleOrNull() ?: 0.0
                        onValueChange(parsed)
                    }
                    isFocused = focusState.isFocused
                },
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val parsed = localText.toDoubleOrNull() ?: 0.0
                    onValueChange(parsed)
                    focusManager.clearFocus()
                }
            ),
            singleLine = true
        )
        IconButton(
            onClick = { onValueChange(value + step) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase weight")
        }
    }
}

@Composable
fun RepsPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    var localText by remember { mutableStateOf(if (value == 0) "" else value.toString()) }

    if (!isFocused) {
        localText = if (value == 0) "" else value.toString()
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        IconButton(
            onClick = { if (value > 0) onValueChange(value - 1) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease reps")
        }
        OutlinedTextField(
            value = localText,
            onValueChange = { newText ->
                if (newText.isEmpty() || newText.matches(Regex("^\\d+$"))) {
                    localText = newText
                }
            },
            modifier = Modifier
                .widthIn(min = 56.dp, max = 72.dp)
                .onFocusChanged { focusState ->
                    if (isFocused && !focusState.isFocused) {
                        val parsed = localText.toIntOrNull() ?: 0
                        onValueChange(parsed)
                    }
                    isFocused = focusState.isFocused
                },
            textStyle = MaterialTheme.typography.bodyMedium.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val parsed = localText.toIntOrNull() ?: 0
                    onValueChange(parsed)
                    focusManager.clearFocus()
                }
            ),
            singleLine = true
        )
        IconButton(
            onClick = { onValueChange(value + 1) },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase reps")
        }
    }
}
