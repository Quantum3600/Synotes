package com.trishit.synotes

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier

interface Platform {
    val name: String
    val isDesktop: Boolean get() = name.contains("Java") || name.contains("Desktop")
    val isWideLayout: Boolean get() = isDesktop || name.contains("Wasm")
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
