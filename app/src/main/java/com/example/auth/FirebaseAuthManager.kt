package com.example.auth

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager(private val context: Context) {

    private val tag = "FirebaseAuthManager"

    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                // Initialize default FirebaseApp if needed
                val options = FirebaseOptions.Builder()
                    .setApplicationId("com.aistudio.landcheckai.vzkrqt")
                    .setApiKey("AIzaSyDummyKeyForFallbackLandCheck")
                    .setProjectId("landcheck-ai")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w(tag, "Firebase Auth initialization warning: ${e.message}")
            try {
                FirebaseAuth.getInstance()
            } catch (ex: Exception) {
                null
            }
        }
    }

    val currentUser: FirebaseUser?
        get() = try { auth?.currentUser } catch (_: Exception) { null }

    val isFirebaseAvailable: Boolean
        get() = auth != null

    fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val authInstance = auth
        if (authInstance == null) {
            trySend(null)
            awaitClose { }
            return@callbackFlow
        }

        val listener = FirebaseAuth.AuthStateListener { fbAuth ->
            trySend(fbAuth.currentUser)
        }
        authInstance.addAuthStateListener(listener)
        awaitClose { authInstance.removeAuthStateListener(listener) }
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUser> {
        val authInstance = auth ?: return Result.failure(Exception("Firebase Auth service unavailable"))
        return try {
            val result = authInstance.signInWithEmailAndPassword(email.trim(), pass).await()
            val user = result.user ?: throw Exception("User is null after sign in")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String): Result<FirebaseUser> {
        val authInstance = auth ?: return Result.failure(Exception("Firebase Auth service unavailable"))
        return try {
            val result = authInstance.createUserWithEmailAndPassword(email.trim(), pass).await()
            val user = result.user ?: throw Exception("User is null after account creation")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<FirebaseUser> {
        val authInstance = auth ?: return Result.failure(Exception("Firebase Auth service unavailable"))
        return try {
            val result = authInstance.signInAnonymously().await()
            val user = result.user ?: throw Exception("Anonymous user is null")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.e(tag, "Error signing out: ${e.message}")
        }
    }
}
