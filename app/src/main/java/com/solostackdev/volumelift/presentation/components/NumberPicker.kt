package com.solostackdev.volumelift.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solostackdev.volumelift.presentation.theme.Border
import com.solostackdev.volumelift.presentation.theme.Surface
import com.solostackdev.volumelift.presentation.theme.SurfaceVariant
import com.solostackdev.volumelift.presentation.theme.TextPrimary
import com.solostackdev.volumelift.presentation.theme.TextTertiary

private fun formatWeight(value: Double): String {
    if (value == 0.0) return ""
    return if (value == value.toLong().toDouble()) value.toLong().toString()
    else String.format("%.1f", value)
}

@Composable
fun WeightPicker(
    value: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    var localText by remember { mutableStateOf(formatWeight(value)) }

    if (!isFocused) {
        localText = formatWeight(value)
    }

    val hasValue = localText.isNotEmpty()
    val bgColor = if (hasValue) Surface else SurfaceVariant
    val textColor = if (hasValue) TextPrimary else TextTertiary

    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .then(
                if (!hasValue) Modifier.border(0.5.dp, Border, RoundedCornerShape(8.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = localText,
            onValueChange = { newText ->
                if (newText.isEmpty() || newText.matches(Regex("^\\d*\\.?\\d*$"))) {
                    localText = newText
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (isFocused && !focusState.isFocused) {
                        val parsed = localText.toDoubleOrNull() ?: 0.0
                        onValueChange(parsed)
                    }
                    isFocused = focusState.isFocused
                },
            textStyle = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                color = textColor,
                textAlign = TextAlign.Center
            ),
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
            singleLine = true,
            cursorBrush = SolidColor(TextPrimary),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    innerTextField()
                }
            }
        )
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

    val hasValue = localText.isNotEmpty()
    val bgColor = if (hasValue) Surface else SurfaceVariant
    val textColor = if (hasValue) TextPrimary else TextTertiary

    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .then(
                if (!hasValue) Modifier.border(0.5.dp, Border, RoundedCornerShape(8.dp))
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = localText,
            onValueChange = { newText ->
                if (newText.isEmpty() || newText.matches(Regex("^\\d+$"))) {
                    localText = newText
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (isFocused && !focusState.isFocused) {
                        val parsed = localText.toIntOrNull() ?: 0
                        onValueChange(parsed)
                    }
                    isFocused = focusState.isFocused
                },
            textStyle = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.W500,
                color = textColor,
                textAlign = TextAlign.Center
            ),
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
            singleLine = true,
            cursorBrush = SolidColor(TextPrimary),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    innerTextField()
                }
            }
        )
    }
}
