package com.example.newgymapp.model

import android.content.ClipDescription

data class Workout (

    var name: String = "",
    var level: String ="",
    var exercises : List<Exercise>,
    var url: String = ""

){
    constructor(name: String,level: String) : this(name,level, emptyList(),""){
        this.name = name
        this.level = level
    }

}