package com.example.firebaseariketa

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore


object FirebaseSingleton {

    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore
}