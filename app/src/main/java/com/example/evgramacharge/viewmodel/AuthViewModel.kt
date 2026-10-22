package com.example.evgramacharge.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    var user by mutableStateOf(auth.currentUser)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var loading by mutableStateOf(false)
        private set

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        run {
            auth.signInWithEmailAndPassword(email, password).await()
            onSuccess()
        }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        run {
            auth.createUserWithEmailAndPassword(email, password).await()
            onSuccess()
        }
    }

    fun signOut() {
        auth.signOut()
        user = null
    }

    private fun run(block: suspend () -> Unit) {
        viewModelScope.launch {
            loading = true
            error = null
            try {
                block()
                user = auth.currentUser
            } catch (e: Exception) {
                error = e.localizedMessage
            }
            loading = false
        }
    }
}
