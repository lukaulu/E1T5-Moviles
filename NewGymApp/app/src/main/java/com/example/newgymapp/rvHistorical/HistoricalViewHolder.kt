package com.example.firebaseariketa.rvArtist

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.newgymapp.R
import com.example.newgymapp.model.HistoricalWorkout
import com.example.newgymapp.model.Workout

class HistoricalViewHolder (view: View): RecyclerView.ViewHolder(view){

    private val tvhistoricalName:TextView = view.findViewById(R.id.historicalName)
    private val tvhistoricalLevel:TextView = view.findViewById(R.id.historicalLevel)
    private val tvhistoricalTime:TextView = view.findViewById(R.id.historicalTime)
    private val tvhistoricalDate:TextView = view.findViewById(R.id.historicalDate)


    fun render(historical : HistoricalWorkout){
        Log.i("HISTORICAL_WORKOUT", historical.toString())
        tvhistoricalName.text = historical.name
        tvhistoricalLevel.text = historical.level
        tvhistoricalTime.text = "${historical.time} seconds"
        tvhistoricalDate.text = historical.date



    }

}
