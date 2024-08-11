package com.medbytes.smartwatch.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading


    init {
        _user.value = auth.currentUser
        _loading.value = false
    }

    fun signInWithEmail(email: String, password: String) {
        try {

        _loading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _loading.value = false
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    println("error signing done");
                } else {
                    _user.value = null
                }
            }
        }
        catch (e:Exception){
            println("error signing ${e.message}");
        }


    }
    fun signOut() {
        auth.signOut()
        _user.value = null
    }

    fun getCurrentUser() = auth.currentUser
}
