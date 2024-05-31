package com.ruki.tierbnb.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ruki.tierbnb.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> get() = _user

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        fetchUser()
    }

    private fun fetchUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    val uid = currentUser.uid
                    val document = db.collection("users").document(uid).get().await()
                    val user = document.toObject(User::class.java)
                    println("PEDER: ${document.toObject(User::class.java)}")
                    user?.let {
                        _user.value = it.copy(id = uid)
                    }
                } catch (e: Exception) {
                    // Handle the exception, for example, by logging it
                }
            }
        }
    }
}