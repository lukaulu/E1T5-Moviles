package com.example.firebaseariketa.model

data class Artist
    (
            val name: String = "",
            val description:String = "",
            val image:String = "",
            val songs:List<Song>
            ){
}