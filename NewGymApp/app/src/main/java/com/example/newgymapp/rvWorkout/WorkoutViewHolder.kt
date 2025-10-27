package com.example.firebaseariketa.rvArtist

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.newgymapp.R
import com.example.newgymapp.model.Workout

class WorkoutViewHolder (view: View): RecyclerView.ViewHolder(view){

    private val tvName:TextView = view.findViewById(R.id.tvName)
    private val ivWorkout:ImageView = view.findViewById(R.id.imgviewitem)
    private val ivLevel:ImageView = view.findViewById(R.id.imglevelitem)

    fun render(workout: Workout){
        tvName.text = workout.name
        var link = "@drawable/" + workout.name.lowercase().trim() + ".png"
        val ctx = itemView.context

        //sacar el id desde la carpeta drawable
        val afterSlash = link.substringAfterLast('/', link)
        val resName = afterSlash.substringBeforeLast('.', afterSlash)
        val resId = ctx.resources.getIdentifier(resName, "drawable", ctx.packageName)

        if (resId != 0) {
            ivWorkout.setImageResource(resId)
        } else {
            // fallback
            ivWorkout.setImageResource(android.R.drawable.ic_menu_report_image)
        }

        var levellink = ""
        Log.i("WORKOUT_LEVEL", workout.level)
        if (workout.level == "Begginer"){
            levellink = "levelbegginer"
        } else if (workout.level == "Middle"){
            levellink = "levelmiddle"
        } else if (workout.level == "Advanced"){
            levellink = "leveladvanced"
        }

        levellink = "@drawable/" + levellink
        val levelctx = itemView.context

        //sacar el id desde la carpeta drawable
        val afterSlashlevel = levellink.substringAfterLast('/', levellink)
        val resNameLevel = afterSlashlevel.substringBeforeLast('.', afterSlashlevel)
        val resIdLevel = levelctx.resources.getIdentifier(resNameLevel, "drawable", levelctx.packageName)

        if (resIdLevel != 0) {
            ivLevel.setImageResource(resIdLevel)
        } else {
            // fallback
            ivLevel.setImageResource(android.R.drawable.ic_menu_report_image)
        }


    }

}
