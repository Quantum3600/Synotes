package com.trishit.synotes.data.repository

import com.trishit.synotes.domain.model.User
import com.trishit.synotes.domain.repository.AuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Flow<User?> = firebaseAuth.authStateChanged.map { firebaseUser ->
        firebaseUser?.let {
            User(
                uid = it.uid,
                email = it.email,
                displayName = it.displayName,
                photoUrl = it.photoURL // GitLive uses photoURL
            )
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.credential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential)
            val user = result.user?.let {
                User(
                    uid = it.uid,
                    email = it.email,
                    displayName = it.displayName,
                    photoUrl = it.photoURL
                )
            } ?: throw Exception("Sign in failed")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}
