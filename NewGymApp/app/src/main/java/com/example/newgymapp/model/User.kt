package com.example.newgymapp.model

import android.R

data class User (

    var name: String ="",
    var lastName: String="",
    var email: String = "",
    var password: String = "",
    var trainer: Boolean = false,
    //var workoutsList: MutableList<Workout>,
    var birthdate : String="",

){
}