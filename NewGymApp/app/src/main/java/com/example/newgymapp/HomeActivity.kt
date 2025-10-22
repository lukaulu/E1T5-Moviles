package com.example.newgymapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseariketa.rvArtist.WorkoutAdapter
import com.example.newgymapp.model.Exercise
import com.example.newgymapp.model.Workout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    val db = FirebaseSingleton.db
    var workouts :List<Workout> = emptyList()

    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var rvWorkout:RecyclerView
    private lateinit var wellcometv : TextView;
    private val auth = FirebaseSingleton.auth
    private lateinit var itemCard : CardView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        initComponents()
        initListeners()
        initUI()

    }

    private fun initListeners() {



    }

    private fun initComponents() {
        itemCard = findViewById(R.id.itemViewContainer)
        wellcometv = findViewById(R.id.wellcometv)


        val currentUser = auth.currentUser
        wellcometv.text = "Hello " + currentUser?.email?.substringBefore("@")

        rvWorkout = findViewById(R.id.rvWorkout)

    }

    private fun initUI() {
        workoutAdapter = WorkoutAdapter(workouts)
        rvWorkout.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvWorkout.adapter = workoutAdapter

        CoroutineScope(Dispatchers.IO).launch {
            workouts = dbChargeWorkouts()

            withContext(Dispatchers.Main) {
                workoutAdapter.workouts = workouts
                workoutAdapter.notifyDataSetChanged()

            }
        }
    }






    suspend fun dbChargeWorkouts() :  List<Workout>{

        var workouts = mutableListOf<Workout>()
        val workoutSnapshot = FirebaseSingleton.db.collection("workouts").get().await()

        for (workoutDoc in workoutSnapshot.documents) {
            val name = workoutDoc.getString("name") ?: ""
            val level = workoutDoc.getString("level") ?: ""
            val image = workoutDoc.getString("image") ?: ""

            val exercisesRaw = workoutDoc.get("exercises") as? List<Map<String, Any>>


            val exercisesList = exercisesRaw?.map { exerciseMap ->

                Exercise(
                    exName = exerciseMap["exName"] as? String ?: "",
                    reps = exerciseMap["repetitions"] as? Long ?: 0,
                    sets = exerciseMap["series"] as? Long ?: 0
                )
            } ?: emptyList()
            Log.i("UCM","exercisesList: $exercisesList")

            workouts.add(
                Workout(
                    name = name,
                    level = level,
                    exercises = exercisesList,
                    imglink = image
                )
            )
        }

        return workouts

    }



}