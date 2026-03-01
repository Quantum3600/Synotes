package com.trishit.synotes

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun onApplicationStartPlatformSpecific() {
}

@Composable
actual fun WindowDraggableArea(modifier: Modifier, content: @Composable () -> Unit) {
    WindowDraggableArea(modifier = modifier, content = content)
}
