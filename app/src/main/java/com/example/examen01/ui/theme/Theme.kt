package com.example.examen01.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color.Blue,
    secondary = androidx.compose.ui.graphics.Color.LightGray,
    // Puedes personalizar más colores aquí
)

@Composable
fun ExamenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}