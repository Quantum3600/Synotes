package com.trishit.synotes

import com.mmk.kmpauth.core.KMPAuthInternalApi
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.trishit.synotes.Constants.WEB_CLIENT_ID

object AppInitializer {
    fun onApplicationStart() {
        onApplicationStartPlatformSpecific()
        GoogleAuthProvider.create(credentials = GoogleAuthCredentials(
            serverId = WEB_CLIENT_ID,
        )
        )
    }
}