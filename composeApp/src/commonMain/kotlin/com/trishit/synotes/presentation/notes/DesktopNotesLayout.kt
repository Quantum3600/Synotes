package com.trishit.synotes.presentation.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.trishit.synotes.domain.model.Note
import com.trishit.synotes.domain.model.User
import com.trishit.synotes.domain.repository.NoteRepository

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

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Title Bar + Tabs
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tabs section (Scrollable)
                    LazyRow(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item {
                            TabItem(
                                title = "All Notes",
                                isSelected = selectedTabId == null,
                                onClick = { selectedTabId = null },
                                onClose = null
                            )
                        }

                        items(openTabs) { noteId ->
                            val note = notes.find { it.id == noteId }
                            TabItem(
                                title = note?.title?.ifEmpty { "Untitled" } ?: "Loading...",
                                isSelected = selectedTabId == noteId,
                                onClick = { selectedTabId = noteId },
                                onClose = {
                                    openTabs = openTabs - noteId
                                    if (selectedTabId == noteId) {
                                        selectedTabId = null
                                    }
                                }
                            )
                        }

                        item {
                            IconButton(onClick = { /* New note logic */ }) {
                                Icon(Icons.Default.Add, contentDescription = "New Note")
                            }
                        }
                    }

                    // Window Controls
                    Row(
                        modifier = Modifier.fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WindowControlButton(Icons.Default.Remove, "Minimize", onMinimize)
                        WindowControlButton(Icons.Default.CropSquare, "Maximize", onMaximize)
                        WindowControlButton(Icons.Default.Close, "Close", onClose, isClose = true)
                    }
                }
            }

            // Screen Content
            Box(modifier = Modifier.fillMaxSize()) {
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
                            }
                        },
                        onLogout = onLogout
                    )
                } else {
                    val detailViewModel = remember(selectedTabId) {
                        NoteDetailViewModel(noteRepository, user.uid, selectedTabId)
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

@Composable
fun WindowControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: (() -> Unit)?,
    isClose: Boolean = false
) {
    IconButton(
        onClick = { onClick?.invoke() },
        modifier = Modifier.size(48.dp),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(18.dp))
    }
}

@Composable
fun TabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    onClose: (() -> Unit)?
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(min = 100.dp, max = 200.dp)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        if (onClose != null) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(16.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close Tab",
                    tint = contentColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}
