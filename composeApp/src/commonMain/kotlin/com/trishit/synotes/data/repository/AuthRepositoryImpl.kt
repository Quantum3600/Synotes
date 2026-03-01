package com.trishit.synotes.data.repository

import com.trishit.synotes.domain.model.User
import com.trishit.synotes.domain.repository.AuthRepository
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: Flow<User?> = firebaseAuth.authStateChanged.map { it.toUser() }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            // Note: This throws NotImplementedError on JVM/Desktop in GitLive 2.4.0
            val credential = GoogleAuthProvider.credential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential)
            val user = result.user?.toUser() ?: throw Exception("Sign in failed")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        } catch (e: Throwable) {
            // Catching NotImplementedError (which is a Throwable) to prevent crashing on Desktop
            Result.failure(e)
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password)
            val user = result.user?.toUser() ?: throw Exception("Sign in failed")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password)
            val user = result.user?.toUser() ?: throw Exception("Sign up failed")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInAnonymously(): Result<User> {
        return try {
            val result = firebaseAuth.signInAnonymously()
            val user = result.user?.toUser() ?: throw Exception("Anonymous sign in failed")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser?.toUser(): User? {
        return this?.let {
            User(
                uid = it.uid,
                email = it.email,
                displayName = if (it.isAnonymous) "Guest" else it.displayName,
                photoUrl = it.photoURL
            )
        }
    }
}
