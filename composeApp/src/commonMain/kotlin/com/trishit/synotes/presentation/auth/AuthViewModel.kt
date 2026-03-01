package com.trishit.synotes.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trishit.synotes.domain.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    val currentUser = repository.currentUser.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    fun onGoogleSignInResult(idToken: String) {
        viewModelScope.launch {
            repository.signInWithGoogle(idToken)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }
}
