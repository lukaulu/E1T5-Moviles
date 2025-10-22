package com.example.firebaseariketa.rvArtist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.newgymapp.R
import com.example.newgymapp.model.Workout

class WorkoutAdapter(var workouts: List<Workout>):RecyclerView.Adapter<WorkoutViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout,parent,false)
        return WorkoutViewHolder(view)
    }

    override fun getItemCount(): Int {

        return workouts.size
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.render(workouts[position])
    }
}