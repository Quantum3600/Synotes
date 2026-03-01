package com.trishit.synotes.presentation.notes

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.trishit.synotes.WindowDraggableArea
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

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Custom Title Bar with Draggable Area
            WindowDraggableArea(
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Surface(
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Window Controls (MacOS Style)
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isHovered by interactionSource.collectIsHoveredAsState()

                            WindowControlButton(
                                color = Color(0xFFFF5F56), // Red
                                icon = Icons.Default.Close,
                                isHovered = isHovered,
                                onClick = onClose,
                                interactionSource = interactionSource
                            )
                            WindowControlButton(
                                color = Color(0xFFFFBD2E), // Yellow
                                icon = Icons.Default.HorizontalRule,
                                isHovered = isHovered,
                                onClick = onMinimize,
                                interactionSource = interactionSource
                            )
                            WindowControlButton(
                                color = Color(0xFF27C93F), // Green
                                icon = Icons.Default.Square,
                                isHovered = isHovered,
                                onClick = onMaximize,
                                interactionSource = interactionSource
                            )
                        }

                        // Window Title / Logo
                        Text(
                            "Synotes",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Tabs section
                        val tabsScrollState = rememberScrollState()
                        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                            Row(
                                modifier = Modifier.fillMaxHeight().horizontalScroll(tabsScrollState),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TabItem(
                                    title = "All Notes",
                                    isSelected = selectedTabId == null,
                                    onClick = { selectedTabId = null },
                                    onClose = null
                                )

                                openTabs.forEach { noteId ->
                                    val note = notes.find { it.id == noteId }
                                    TabItem(
                                        title = if (noteId.startsWith("new_note")) "New Note" else (note?.title?.ifEmpty { "Untitled" } ?: "Loading..."),
                                        isSelected = selectedTabId == noteId,
                                        onClick = { selectedTabId = noteId },
                                        onClose = {
                                            val newTabs = openTabs.toMutableList()
                                            newTabs.remove(noteId)
                                            openTabs = newTabs
                                            if (selectedTabId == noteId) {
                                                selectedTabId = null
                                            }
                                        }
                                    )
                                }

                                IconButton(onClick = { 
                                    val newId = "new_note_${Clock.System.now().toEpochMilliseconds()}"
                                    openTabs = openTabs + newId
                                    selectedTabId = newId
                                }) {
                                    Icon(Icons.Default.Add, contentDescription = "New Note")
                                }
                            }
                        }
                    }
                }
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
                        if (savedId != null && savedId.isNotEmpty() && selectedTabId!!.startsWith("new_note")) {
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

@Composable
fun WindowControlButton(
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isHovered: Boolean,
    onClick: (() -> Unit)?,
    interactionSource: MutableInteractionSource
) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(
                onClick = { onClick?.invoke() },
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isHovered) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(8.dp),
                tint = Color.Black.copy(alpha = 0.5f)
            )
        }
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
            style = MaterialTheme.typography.labelSmall,
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
