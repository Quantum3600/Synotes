package com.trishit.synotes

import android.app.Application
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.google.firebase.FirebasePlatform
import com.trishit.synotes.Constants.API_KEY
import com.trishit.synotes.Constants.APP_ID
import com.trishit.synotes.Constants.AUTH_DOMAIN
import com.trishit.synotes.Constants.PROJECT_ID
import com.trishit.synotes.Constants.STORAGE_BUCKET
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

fun main() {
    FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
        val storage = mutableMapOf<String, String>()
        override fun store(key: String, value: String) {
            storage.set(key, value)
        }
        override fun retrieve(key: String): String? {
            return storage[key]
        }

        override fun clear(key: String) {
            storage.remove(key)
        }

        override fun log(msg: String) {
            println(msg)
        }
    })
    val options = FirebaseOptions(
        apiKey = API_KEY,
        projectId = PROJECT_ID,
        applicationId = APP_ID,
        storageBucket = STORAGE_BUCKET,
        authDomain = AUTH_DOMAIN,
    )
    Firebase.initialize(context = Application(), options = options)
    application {
        val windowState = rememberWindowState()
        AppInitializer.onApplicationStart()
        Window(
            onCloseRequest = ::exitApplication,
            title = "Synotes",
            state = windowState,
            undecorated = true // Use custom title bar
        ) {
            App(
                onMinimize = { windowState.isMinimized = true },
                onMaximize = { 
                    windowState.placement = if (windowState.placement == WindowPlacement.Maximized) {
                        WindowPlacement.Floating
                    } else {
                        WindowPlacement.Maximized
                    }
                },
                onClose = { exitApplication() },
                windowDraggableArea = { modifier, content ->
                    WindowDraggableArea(modifier, content)
                }
            )
        }
    }
}
