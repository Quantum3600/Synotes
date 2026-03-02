package com.trishit.synotes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.trishit.synotes.data.repository.AuthRepositoryImpl
import com.trishit.synotes.data.repository.NoteRepositoryImpl
import com.trishit.synotes.presentation.auth.AuthViewModel
import com.trishit.synotes.presentation.auth.LoginScreen
import com.trishit.synotes.presentation.notes.*
import com.trishit.synotes.presentation.theme.SynotesTheme
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

sealed class Screen {
    data object Login : Screen()
    data object Notes : Screen()
    data class NoteDetail(val noteId: String? = null) : Screen()
}

@Composable
fun App(
    onMinimize: (() -> Unit)? = null,
    onMaximize: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    windowDraggableArea: @Composable (Modifier, @Composable () -> Unit) -> Unit = { m, c -> 
        androidx.compose.foundation.layout.Box(m) { c() } 
    }
) {
    val authRepository = remember { AuthRepositoryImpl(Firebase.auth) }
    val noteRepository = remember { NoteRepositoryImpl(Firebase.firestore) }
    val authViewModel = remember { AuthViewModel(authRepository) }
    
    val currentUser by authViewModel.currentUser.collectAsState(null)
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
    val platform = remember { getPlatform() }


    LaunchedEffect(currentUser) {
        currentScreen = if (currentUser != null) Screen.Notes else Screen.Login
    }

    SynotesTheme {
        CompositionLocalProvider(
            LocalWindowDraggableArea provides windowDraggableArea
        ) {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                when (val screen = currentScreen) {
                    is Screen.Login -> LoginScreen(viewModel = authViewModel)
                    is Screen.Notes -> {
                        currentUser?.let { user ->
                            val notesViewModel = remember(user.uid) { 
                                NotesViewModel(noteRepository, user.uid) 
                            }
                            
                            if (platform.isWideLayout) {
                                DesktopNotesLayout(
                                    notesViewModel = notesViewModel,
                                    user = user,
                                    noteRepository = noteRepository,
                                    onLogout = { authViewModel.signOut() },
                                    onMinimize = onMinimize,
                                    onMaximize = onMaximize,
                                    onClose = onClose
                                )
                            } else {
                                NotesScreen(
                                    viewModel = notesViewModel,
                                    user = user,
                                    onNoteClick = { noteId ->
                                        currentScreen = Screen.NoteDetail(noteId)
                                    },
                                    onLogout = { authViewModel.signOut() }
                                )
                            }
                        }
                    }
                    is Screen.NoteDetail -> {
                        currentUser?.let { user ->
                            val noteDetailViewModel = remember(screen.noteId) {
                                NoteDetailViewModel(noteRepository, user.uid, screen.noteId)
                            }
                            NoteDetailScreen(
                                viewModel = noteDetailViewModel,
                                onBack = { currentScreen = Screen.Notes }
                            )
                        }
                    }
                }
            }
        }
    }
}
