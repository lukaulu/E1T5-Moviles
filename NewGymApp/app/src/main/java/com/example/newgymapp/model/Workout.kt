package com.example.newgymapp.model

import android.content.ClipDescription

data class Workout (

    val name: String = "",
    val level: String ="",
    val exercises : List<Exercise>,
    val imglink : String = ""
){

}