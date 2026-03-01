package com.trishit.synotes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun onApplicationStartPlatformSpecific()

@Composable
expect fun WindowDraggableArea(modifier: Modifier = Modifier, content: @Composable () -> Unit)
