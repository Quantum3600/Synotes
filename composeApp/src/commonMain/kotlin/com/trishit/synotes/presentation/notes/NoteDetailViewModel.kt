package com.trishit.synotes.presentation.notes

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamedrejeb.richeditor.model.RichTextState
import com.trishit.synotes.domain.model.Note
import com.trishit.synotes.domain.repository.NoteRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.time.Clock

class NoteDetailViewModel(
    private val noteRepository: NoteRepository,
    private val userId: String,
    private val noteId: String?
) : ViewModel() {

    private val _note = MutableStateFlow<Note?>(null)
    val note: StateFlow<Note?> = _note.asStateFlow()

    val richTextState = RichTextState()
    val titleState = MutableStateFlow("")

    init {
        if (noteId != null) {
            loadNote(noteId)
        } else {
            _note.value = Note(userId = userId)
        }

        // Auto-save logic
        viewModelScope.launch {
            @OptIn(FlowPreview::class)
            combine(
                titleState,
                snapshotFlow { richTextState.toHtml() }
            ) { title, html ->
                title to html
            }
            .debounce(1000L) // Wait for 1 second of inactivity
            .collect { (title, html) ->
                saveNoteInternal(title, html)
            }
        }
    }

    private fun loadNote(id: String) {
        viewModelScope.launch {
            val loadedNote = noteRepository.getNoteById(id)
            if (loadedNote != null) {
                _note.value = loadedNote
                titleState.value = loadedNote.title
                richTextState.setHtml(loadedNote.content)
            }
        }
    }

    private suspend fun saveNoteInternal(title: String, html: String) {
        val currentNote = _note.value ?: return
        
        // Don't save if it's a new empty note
        if (currentNote.id.isEmpty() && title.isEmpty() && html == "<p></p>") {
            return
        }

        // Don't save if nothing changed
        if (currentNote.title == title && currentNote.content == html && currentNote.id.isNotEmpty()) {
            return
        }

        val updatedNote = currentNote.copy(
            title = title,
            content = html,
            updatedAt = Clock.System.now().toEpochMilliseconds()
        )

        try {
            if (updatedNote.id.isEmpty()) {
                val newId = noteRepository.insertNote(updatedNote)
                _note.value = updatedNote.copy(id = newId)
            } else {
                noteRepository.updateNote(updatedNote)
                _note.value = updatedNote
            }
        } catch (e: Exception) {
            // Handle error (e.g., log it)
        }
    }

    fun saveAndExit(onComplete: () -> Unit) {
        viewModelScope.launch {
            saveNoteInternal(titleState.value, richTextState.toHtml())
            onComplete()
        }
    }
}
