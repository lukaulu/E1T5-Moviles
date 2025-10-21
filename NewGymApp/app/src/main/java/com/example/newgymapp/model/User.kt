package com.example.newgymapp.model

import android.R
import com.google.firebase.firestore.Exclude

data class User (
    @get:Exclude var id : String = "",
    var name: String ="",
    var lastName: String="",
    var email: String = "",
    var password: String = "",
    var trainer: Boolean = false,
    @get:Exclude var workoutsList: MutableList<Workout>,
    var birthdate : String="",


){
    constructor(email: String,password: String, name: String,lastName: String, birthdate: String) : this
        ( "", name, lastName, email, password, false, mutableListOf<Workout>(), birthdate

        ) {
            this.email = email
            this.password = password
            this.name = name
            this.lastName = lastName
            this.birthdate = birthdate
            this.workoutsList = mutableListOf<Workout>()
        }
}