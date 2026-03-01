package com.trishit.synotes.presentation.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.trishit.synotes.domain.model.Note
import com.trishit.synotes.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    viewModel: NotesViewModel,
    user: User,
    onNoteClick: (String?) -> Unit,
    onLogout: () -> Unit
) {
    val notes by viewModel.notes.collectAsState()
    var showProfileModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("My Notes") },
                actions = {
                    IconButton(onClick = { showProfileModal = true }) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNoteClick(null) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = padding,
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(notes) { note ->
                NoteItem(
                    note = note,
                    onClick = { onNoteClick(note.id) },
                    onFavoriteClick = { viewModel.toggleFavorite(note) }
                )
            }
        }

        if (showProfileModal) {
            AlertDialog(
                onDismissRequest = { showProfileModal = false },
                confirmButton = {
                    TextButton(onClick = onLogout) {
                        Text("Log Out", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showProfileModal = false }) {
                        Text("Close")
                    }
                },
                title = { Text("Profile") },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // In a real app, use a library like Coil/Kamel for images
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(user.displayName ?: "No Name", style = MaterialTheme.typography.titleMedium)
                        Text(user.email ?: "", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            )
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (note.color != 0xFFFFFFFF) Color(note.color) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = note.title.ifEmpty { "Untitled" },
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        if (note.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (note.isFavorite) Color.Red else Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            // Simplified preview: strip HTML tags roughly
            val previewText = note.content.replace(Regex("<[^>]*>"), "")
            Text(
                text = previewText,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )
        }
    }
}

// Add simple background for icon if image is not available

