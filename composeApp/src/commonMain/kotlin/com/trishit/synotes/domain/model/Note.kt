package com.trishit.synotes.domain.model

import kotlinx.serialization.Serializable
import kotlin.time.Clock

@Serializable
data class Note(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val userId: String = "",
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    val updatedAt: Long = Clock.System.now().toEpochMilliseconds(),
    val isFavorite: Boolean = false,
    val color: Long = 0xFFFFFFFF // Default white
)
