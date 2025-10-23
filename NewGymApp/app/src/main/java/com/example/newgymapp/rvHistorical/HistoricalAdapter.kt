package com.example.firebaseariketa.rvArtist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newgymapp.R
import com.example.newgymapp.model.HistoricalWorkout
import com.example.newgymapp.model.Workout

class HistoricalAdapter(var historical: List<HistoricalWorkout>):RecyclerView.Adapter<HistoricalViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout,parent,false)
        return HistoricalViewHolder(view)
        
    }

    override fun getItemCount(): Int {

        return historical.size
    }

    override fun onBindViewHolder(holder: HistoricalViewHolder, position: Int) {
        holder.render(historical[position])
    }
}