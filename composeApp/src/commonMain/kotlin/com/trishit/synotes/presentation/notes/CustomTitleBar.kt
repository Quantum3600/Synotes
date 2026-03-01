package com.trishit.synotes.presentation.notes

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Square
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.trishit.synotes.domain.model.Note
import kotlin.time.Clock

@Composable
fun CustomTitleBar(
    notes: List<Note>,
    openTabs: List<String>,
    selectedTabId: String?,
    onTabSelected: (String?) -> Unit,
    onTabClosed: (String) -> Unit,
    onNewNote: () -> Unit,
    onMinimize: (() -> Unit)?,
    onMaximize: (() -> Unit)?,
    onClose: (() -> Unit)?
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
                        color = Color(0xFF27C93F), // Green
                icon = Icons.Default.CropSquare,
                isHovered = isHovered,
                onClick = onMaximize,
                interactionSource = interactionSource
                )
                WindowControlButton(
                    color = Color(0xFFFFBD2E), // Yellow
                    icon = Icons.Default.HorizontalRule,
                    isHovered = isHovered,
                    onClick = onMinimize,
                    interactionSource = interactionSource
                )

            }

            // Window Title / Logo
            Text(
                "Synotes",
                modifier = Modifier.padding(horizontal = 12.dp),
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
                        onClick = { onTabSelected(null) },
                        onClose = null
                    )

                    openTabs.forEach { noteId ->
                        val note = notes.find { it.id == noteId }
                        TabItem(
                            title = if (noteId.startsWith("new_note")) "New Note" else (note?.title?.ifEmpty { "Untitled" } ?: "Loading..."),
                            isSelected = selectedTabId == noteId,
                            onClick = { onTabSelected(noteId) },
                            onClose = { onTabClosed(noteId) }
                        )
                    }

                    IconButton(onClick = onNewNote) {
                        Icon(Icons.Default.Add, contentDescription = "New Note")
                    }
                }
            }
        }
    }
}

@Composable
private fun WindowControlButton(
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isHovered: Boolean,
    onClick: (() -> Unit)?,
    interactionSource: MutableInteractionSource
) {
    Box(
        modifier = Modifier
            .size(14.dp)
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
                modifier = Modifier.size(12.dp),
                tint = Color.Black.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun TabItem(
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
