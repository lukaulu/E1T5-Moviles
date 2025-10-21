package com.example.firebaseariketa.rvArtist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.newgymapp.R
import com.example.newgymapp.model.Workout

class WorkoutViewHolder (view: View): RecyclerView.ViewHolder(view){

    private val tvName:TextView = view.findViewById(R.id.tvName)
    private val ivWorkout:ImageView = view.findViewById(R.id.imgviewitem)

    fun render(workout: Workout){
        tvName.text = workout.name
    }
}