package com.trishit.synotes.domain.repository

import com.trishit.synotes.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotes(userId: String): Flow<List<Note>>
    suspend fun getNoteById(noteId: String): Note?
    suspend fun insertNote(note: Note): String
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(noteId: String)
    fun getFavoriteNotes(userId: String): Flow<List<Note>>
}
