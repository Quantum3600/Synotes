package com.trishit.synotes.presentation.notes

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    onBack: () -> Unit
) {
    val title by viewModel.titleState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = title,
                        onValueChange = { viewModel.titleState.value = it },
                        placeholder = { Text("Title") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.saveAndExit(onBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.saveAndExit(onBack)
                    }) {
                        Icon(Icons.Default.Done, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            EditorControls(
                state = viewModel.richTextState,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)

            RichTextEditor(
                state = viewModel.richTextState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                colors = RichTextEditorDefaults.richTextEditorColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
fun EditorControls(
    state: RichTextState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ControlIcon(
            icon = Icons.Default.FormatBold,
            isSelected = state.currentSpanStyle.fontWeight == FontWeight.Bold,
            onClick = { state.toggleSpanStyle(SpanStyle(fontWeight = FontWeight.Bold)) }
        )
        ControlIcon(
            icon = Icons.Default.FormatItalic,
            isSelected = state.currentSpanStyle.fontStyle == FontStyle.Italic,
            onClick = { state.toggleSpanStyle(SpanStyle(fontStyle = FontStyle.Italic)) }
        )
        ControlIcon(
            icon = Icons.Default.FormatUnderlined,
            isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.Underline) == true,
            onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.Underline)) }
        )
        ControlIcon(
            icon = Icons.Default.StrikethroughS,
            isSelected = state.currentSpanStyle.textDecoration?.contains(TextDecoration.LineThrough) == true,
            onClick = { state.toggleSpanStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) }
        )
        
        VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp))

        ControlIcon(
            icon = Icons.Default.FormatSize,
            isSelected = state.currentSpanStyle.fontSize == 22.sp,
            onClick = { 
                if (state.currentSpanStyle.fontSize == 22.sp) {
                    state.toggleSpanStyle(SpanStyle(fontSize = 16.sp))
                } else {
                    state.toggleSpanStyle(SpanStyle(fontSize = 22.sp))
                }
            }
        )

        VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp))

        ControlIcon(
            icon = Icons.AutoMirrored.Filled.FormatAlignLeft,
            isSelected = state.currentParagraphStyle.textAlign == TextAlign.Left,
            onClick = { state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Left)) }
        )
        ControlIcon(
            icon = Icons.Default.FormatAlignCenter,
            isSelected = state.currentParagraphStyle.textAlign == TextAlign.Center,
            onClick = { state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Center)) }
        )
        ControlIcon(
            icon = Icons.AutoMirrored.Filled.FormatAlignRight,
            isSelected = state.currentParagraphStyle.textAlign == TextAlign.Right,
            onClick = { state.toggleParagraphStyle(ParagraphStyle(textAlign = TextAlign.Right)) }
        )

        VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp))

        ControlIcon(
            icon = Icons.AutoMirrored.Filled.FormatListBulleted,
            isSelected = state.isUnorderedList,
            onClick = { state.toggleUnorderedList() }
        )
        ControlIcon(
            icon = Icons.Default.FormatListNumbered,
            isSelected = state.isOrderedList,
            onClick = { state.toggleOrderedList() }
        )
    }
}

@Composable
fun ControlIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
        )
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
    }
}
