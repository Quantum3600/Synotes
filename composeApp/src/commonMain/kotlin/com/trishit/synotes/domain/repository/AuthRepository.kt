package com.trishit.synotes.domain.repository

import com.trishit.synotes.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun signUpWithEmailAndPassword(email: String, password: String): Result<User>
    suspend fun signInAnonymously(): Result<User>
    suspend fun signOut()
}
