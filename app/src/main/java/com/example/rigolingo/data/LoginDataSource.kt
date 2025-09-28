package com.example.rigolingo.data

import com.example.rigolingo.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.tasks.await
import java.io.IOException

class LoginDataSource {

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(username, password).await()
            val firebaseUser = authResult.user
            
            return if (firebaseUser != null) {
                val loggedInUser = LoggedInUser(
                    userId = firebaseUser.uid,
                    displayName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User"
                )
                Result.Success(loggedInUser)
            } else {
                Result.Error(IOException("Authentication failed: User is null"))
            }
        } catch (e: FirebaseAuthInvalidUserException) {
            return Result.Error(IOException("No account found with this email address"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return Result.Error(IOException("Invalid email or password"))
        } catch (e: Exception) {
            return Result.Error(IOException("Login failed: ${e.message}", e))
        }
    }

    suspend fun register(email: String, password: String, displayName: String): Result<LoggedInUser> {
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            return if (firebaseUser != null) {
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                
                firebaseUser.updateProfile(profileUpdates).await()
                
                val loggedInUser = LoggedInUser(
                    userId = firebaseUser.uid,
                    displayName = displayName
                )
                Result.Success(loggedInUser)
            } else {
                Result.Error(IOException("Registration failed: User is null"))
            }
        } catch (e: FirebaseAuthUserCollisionException) {
            return Result.Error(IOException("An account with this email already exists"))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return Result.Error(IOException("Invalid email format"))
        } catch (e: Exception) {
            return Result.Error(IOException("Registration failed: ${e.message}", e))
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    fun getCurrentUser(): LoggedInUser? {
        val firebaseUser = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            LoggedInUser(
                userId = firebaseUser.uid,
                displayName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "User"
            )
        } else null
    }
}