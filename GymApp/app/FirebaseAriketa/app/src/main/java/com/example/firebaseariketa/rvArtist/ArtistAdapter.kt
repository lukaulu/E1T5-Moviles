package com.example.firebaseariketa.rvArtist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseariketa.R
import com.example.firebaseariketa.model.Artist

class ArtistAdapter(var artist: List<Artist>):RecyclerView.Adapter<ArtistViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_artist,parent,false)
        return ArtistViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.i("ROI","${artist.size}")
        return artist.size
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        Log.i("ROI","va uno!!!")
        holder.render(artist[position])
    }
}