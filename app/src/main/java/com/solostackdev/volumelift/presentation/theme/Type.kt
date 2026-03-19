package com.solostackdev.volumelift.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    // Screen titles — 24.sp / W500
    displayLarge = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    // Display numbers — 22.sp / W500
    displayMedium = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    // Section headers — 20.sp / W500
    headlineLarge = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    // Title large — screen titles
    titleLarge = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    // Title medium — card titles, section headers
    titleMedium = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    // Title small — exercise names, card content
    titleSmall = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Body large — card titles, exercise names — 14.sp / W500
    bodyLarge = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Body medium — subtitles, descriptions — 12.sp / W400
    bodyMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    // Body small — small labels
    bodySmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 11.sp,
        lineHeight = 14.sp
    ),
    // Label large — overline style
    labelLarge = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    ),
    // Label medium — tags
    labelMedium = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 10.sp,
        lineHeight = 14.sp
    ),
    // Label small — tiny labels
    labelSmall = TextStyle(
        fontWeight = FontWeight.W400,
        fontSize = 10.sp,
        lineHeight = 14.sp
    )
)
