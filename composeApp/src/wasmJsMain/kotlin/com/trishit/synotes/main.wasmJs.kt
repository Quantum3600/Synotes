package com.trishit.synotes

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    Firebase.initialize(
        options = FirebaseOptions(
            apiKey = Constants.API_KEY,
            projectId = Constants.PROJECT_ID,
            applicationId = Constants.APP_ID,
            storageBucket = Constants.STORAGE_BUCKET,
            authDomain = Constants.AUTH_DOMAIN,
        )
    )
    AppInitializer.onApplicationStart()
    ComposeViewport(document.body!!) {
        App()
    }
}
