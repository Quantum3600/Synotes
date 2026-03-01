package com.trishit.synotes

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun onApplicationStartPlatformSpecific()

/**
 * CompositionLocal to provide window dragging functionality.
 * On Desktop, this should be provided using WindowScope.WindowDraggableArea.
 */
val LocalWindowDraggableArea = staticCompositionLocalOf<@Composable (Modifier, @Composable () -> Unit) -> Unit> {
    { modifier, content ->
        Box(modifier) {
            content()
        }
    }
}
