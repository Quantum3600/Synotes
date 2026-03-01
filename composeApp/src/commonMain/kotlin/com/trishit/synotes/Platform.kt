package com.trishit.synotes

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun onApplicationStartPlatformSpecific()