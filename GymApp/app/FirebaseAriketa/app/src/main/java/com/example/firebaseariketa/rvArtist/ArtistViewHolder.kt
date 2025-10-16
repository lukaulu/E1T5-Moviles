package com.example.firebaseariketa.rvArtist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseariketa.R
import com.example.firebaseariketa.model.Artist

class ArtistViewHolder (view: View): RecyclerView.ViewHolder(view){

    private val tvName:TextView = view.findViewById(R.id.tvName)
    private val ivArtist:ImageView = view.findViewById(R.id.ivArtist)

    fun render(artist: Artist){
        tvName.text = artist.name
    }
}