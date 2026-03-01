package com.trishit.synotes.data.repository

import com.trishit.synotes.domain.model.Note
import com.trishit.synotes.domain.repository.NoteRepository
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestoreSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(
    private val firestore: FirebaseFirestore
) : NoteRepository {

    init {
        // Enable offline persistence using the modern SettingsBuilder
        firestore.settings = firestoreSettings { }
    }

    private val notesCollection = firestore.collection("notes")

    override fun getNotes(userId: String): Flow<List<Note>> {
        return notesCollection
            .where { "userId" equalTo userId }
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.map { documentSnapshot ->
                    documentSnapshot.data<Note>().copy(id = documentSnapshot.id)
                }
            }
    }

    override suspend fun getNoteById(noteId: String): Note? {
        return try {
            val document = notesCollection.document(noteId).get()
            document.data<Note>().copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun insertNote(note: Note): String {
        val document = notesCollection.add(note)
        return document.id
    }

    override suspend fun updateNote(note: Note) {
        notesCollection.document(note.id).set(note)
    }

    override suspend fun deleteNote(noteId: String) {
        notesCollection.document(noteId).delete()
    }

    override fun getFavoriteNotes(userId: String): Flow<List<Note>> {
        return notesCollection
            .where { 
                all(
                    "userId" equalTo userId,
                    "isFavorite" equalTo true
                )
            }
            .snapshots
            .map { querySnapshot ->
                querySnapshot.documents.map { documentSnapshot ->
                    documentSnapshot.data<Note>().copy(id = documentSnapshot.id)
                }
            }
    }
}
