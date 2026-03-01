package com.trishit.synotes.presentation.notes

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trishit.synotes.LocalWindowDraggableArea
import com.trishit.synotes.domain.model.User
import com.trishit.synotes.domain.repository.NoteRepository
import kotlin.time.Clock

@Composable
fun DesktopNotesLayout(
    notesViewModel: NotesViewModel,
    user: User,
    noteRepository: NoteRepository,
    onLogout: () -> Unit,
    onMinimize: (() -> Unit)? = null,
    onMaximize: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null
) {
    val notes by notesViewModel.notes.collectAsState()
    var openTabs by remember { mutableStateOf(listOf<String>()) } 
    var selectedTabId by remember { mutableStateOf<String?>(null) } 
    val draggableArea = LocalWindowDraggableArea.current

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom Title Bar with Draggable Area
            draggableArea(Modifier.fillMaxWidth().height(48.dp)) {
                CustomTitleBar(
                    notes = notes,
                    openTabs = openTabs,
                    selectedTabId = selectedTabId,
                    onTabSelected = { selectedTabId = it },
                    onTabClosed = { noteId ->
                        val newTabs = openTabs.toMutableList()
                        newTabs.remove(noteId)
                        openTabs = newTabs
                        if (selectedTabId == noteId) {
                            selectedTabId = null
                        }
                    },
                    onNewNote = {
                        val newId = "new_note_${Clock.System.now().toEpochMilliseconds()}"
                        openTabs = openTabs + newId
                        selectedTabId = newId
                    },
                    onMinimize = onMinimize,
                    onMaximize = onMaximize,
                    onClose = onClose
                )
            }

            // Screen Content
            Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                if (selectedTabId == null) {
                    NotesScreen(
                        viewModel = notesViewModel,
                        user = user,
                        onNoteClick = { noteId ->
                            if (noteId != null) {
                                if (noteId !in openTabs) {
                                    openTabs = openTabs + noteId
                                }
                                selectedTabId = noteId
                            } else {
                                // FAB clicked
                                val newId = "new_note_${Clock.System.now().toEpochMilliseconds()}"
                                openTabs = openTabs + newId
                                selectedTabId = newId
                            }
                        },
                        onLogout = onLogout
                    )
                } else {
                    val actualNoteId = if (selectedTabId!!.startsWith("new_note")) null else selectedTabId
                    val detailViewModel = remember(selectedTabId) {
                        NoteDetailViewModel(noteRepository, user.uid, actualNoteId)
                    }
                    
                    val currentNote by detailViewModel.note.collectAsState()
                    
                    // If a new note is saved, update the tab ID to the real Firestore ID
                    LaunchedEffect(currentNote?.id) {
                        val savedId = currentNote?.id
                        if (!savedId.isNullOrEmpty() && selectedTabId!!.startsWith("new_note")) {
                            val oldId = selectedTabId!!
                            openTabs = openTabs.map { if (it == oldId) savedId else it }
                            selectedTabId = savedId
                        }
                    }

                    NoteDetailScreen(
                        viewModel = detailViewModel,
                        onBack = { selectedTabId = null }
                    )
                }
            }
        }
    }
}
