package com.example.demo.screens

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AuthViewModel : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()

    //exposing to UI
    val authState : LiveData<AuthState> = _authState

    init{
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if(auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }


    fun login(email:String, password:String) {

        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if(task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }


    fun signup(email:String, password:String) {

        if(email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{task->
                if(task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Error(task.exception?.message?:"Something went wrong")
                }
            }
    }

//    fun signup(email: String, password: String) {
//
//        if (email.isEmpty() || password.isEmpty()) {
//            _authState.value = AuthState.Error("Email and password cannot be empty")
//            return
//        }
//
//        _authState.value = AuthState.Loading
//
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val uid = auth.currentUser!!.uid
//
//                    val userData = mapOf(
//                        "uid" to uid,
//                        "email" to email,
//                        "profileImageUrl" to ""
//                    )
//
//                    FirebaseFirestore.getInstance()
//                        .collection("users")
//                        .document(uid)
//                        .set(userData)
//                        .addOnSuccessListener {
//                            _authState.value = AuthState.Authenticated
//                        }
//                        .addOnFailureListener {
//                            _authState.value = AuthState.Error("Failed to create user")
//                        }
//                } else {
//                    _authState.value = AuthState.Error(task.exception?.message ?: "Signup failed")
//                }
//            }
//    }


    fun logout() {
        Firebase.auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }


    fun onLoginSuccess() {
        _authState.value = AuthState.Authenticated
    }

    fun onLoginError(message: String?) {
        _authState.value = AuthState.Error(message ?: "Authentication failed")
    }

}






sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()
}