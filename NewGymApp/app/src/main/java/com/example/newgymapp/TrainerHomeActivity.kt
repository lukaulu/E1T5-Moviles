package com.example.newgymapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseariketa.rvArtist.HistoricalAdapter
import com.example.firebaseariketa.rvArtist.WorkoutAdapter
import com.example.newgymapp.model.Exercise
import com.example.newgymapp.model.HistoricalWorkout
import com.example.newgymapp.model.Workout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TrainerHomeActivity : AppCompatActivity() {

    val db = FirebaseSingleton.db
    var workouts :List<Workout> = emptyList()


    private lateinit var workoutAdapter: WorkoutAdapter;

    private lateinit var rvWorkout:RecyclerView
    private lateinit var wellcometv : TextView;
    private val auth = FirebaseSingleton.auth;
    private lateinit var itemCard : CardView;
    private lateinit var logout: ImageButton;
    private lateinit var filterRG : RadioGroup;
    private lateinit var profiletv : TextView
    private lateinit var togglecreateworkout : ImageButton;
    private lateinit var createworkoutcard : CardView
    private lateinit var addworkoutbtn : ImageButton;
    private lateinit var nametoaddetv : EditText;
    private lateinit var urltoadd : EditText;
    private lateinit var leveltoaddRG : RadioGroup;
    private lateinit var  profilechangercv : CardView;
    private lateinit var profilebtn : ImageButton;




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_trainer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Log.i("UCM", "TRAINER HOME ACTIVITY")

        initComponents()
        initListeners()
        initUI()

    }

    private fun initListeners() {
        logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        filterRG.setOnCheckedChangeListener { group, checkedId ->
            var filteredWorkouts: List<Workout> = when (checkedId) {
                R.id.begginerRB -> {
                    workouts.filter { it.level.equals("Begginer", ignoreCase = true) }
                }

                R.id.middleRB -> {
                    workouts.filter { it.level.equals("Middle", ignoreCase = true) }
                }

                R.id.advancedRB -> {
                    workouts.filter { it.level.equals("Advanced", ignoreCase = true) }
                }

                R.id.allRB -> {
                    workouts
                }

                else -> {
                    workouts
                }
            }

            workoutAdapter.workouts = filteredWorkouts
            workoutAdapter.notifyDataSetChanged()
        }

        togglecreateworkout.setOnClickListener {
            if (createworkoutcard.visibility == CardView.VISIBLE) {
                createworkoutcard.visibility = CardView.GONE
            } else {
                profilechangercv.visibility = CardView.GONE
                createworkoutcard.visibility = CardView.VISIBLE
            }

        }
        addworkoutbtn.setOnClickListener {
            val nameToAdd = nametoaddetv.text.toString()
            val urlToAdd = urltoadd.text.toString()
            val levelToAdd = when (leveltoaddRG.checkedRadioButtonId){
                R.id.newBegginerRB -> "Begginer"
                R.id.newMiddleRB -> "Middle"
                R.id.newAdvancedRB -> "Advanced"
                else -> "Begginer"
            }

            Log.i("UCM", "Adding workout: $nameToAdd, $urlToAdd, $levelToAdd")

            val newWorkout = Workout(
                name = nameToAdd,
                level = levelToAdd,
                exercises = listOf() // Empty list of exercises for now
            )

            CoroutineScope(Dispatchers.IO).launch {
                // Add the new workout to Firestore
                FirebaseSingleton.db.collection("workouts").add(
                    mapOf(
                        "name" to newWorkout.name,
                        "level" to newWorkout.level,
                        "url" to urlToAdd,

                    )
                ).await()

                // Refresh the workouts list
                workouts = dbChargeWorkouts()

                withContext(Dispatchers.Main) {
                    val workoutorder = mapOf(
                        "Begginer" to 1,
                        "Middle" to 2,
                        "Advanced" to 3
                    )
                    workoutAdapter.workouts = workouts.sortedBy { workoutorder[it.level] }
                    workoutAdapter.notifyDataSetChanged()
                }
            }
        }



        profilebtn.setOnClickListener {
            if (profilechangercv.visibility == CardView.VISIBLE) {
                profilechangercv.visibility = CardView.GONE
            } else {
                profilechangercv.visibility = CardView.VISIBLE
                createworkoutcard.visibility = CardView.GONE
            }
        }





    }

    private fun initComponents() {
        filterRG = findViewById(R.id.radioGroupLevels)
        filterRG.check(R.id.allRB)

        itemCard = findViewById(R.id.itemViewContainer)
        wellcometv = findViewById(R.id.wellcometv)

        profiletv = findViewById(R.id.profiletv)



        val currentUser = auth.currentUser
        wellcometv.text = "Hello " + currentUser?.email?.substringBefore("@") + "!"
        profiletv.text = currentUser?.email?.substringBefore("@")

        rvWorkout = findViewById(R.id.rvWorkout)
        logout = findViewById(R.id.logoutbtn)
        togglecreateworkout = findViewById(R.id.createWorkout)
        createworkoutcard = findViewById(R.id.createWorkoutCV)
        addworkoutbtn = findViewById(R.id.addWorkoutBtn)
        nametoaddetv = findViewById(R.id.newNameet)
        urltoadd = findViewById(R.id.newUrlet)
        leveltoaddRG = findViewById(R.id.levelRG)
        profilechangercv = findViewById(R.id.profilechangercv)
        profilebtn = findViewById(R.id.profilebtn)

    }

    private fun initUI() {
        val workoutorder = mapOf(
            "Begginer" to 1,
            "Middle" to 2,
            "Advanced" to 3
        )



        workoutAdapter = WorkoutAdapter(workouts)
        rvWorkout.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvWorkout.adapter = workoutAdapter

        CoroutineScope(Dispatchers.IO).launch {
            workouts = dbChargeWorkouts()

            withContext(Dispatchers.Main) {
                workoutAdapter.workouts = workouts.sortedBy { workoutorder[it.level] }
                Log.i("UCM", workouts.sortedBy { workoutorder[it.level] }.toString())
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


            val exercisesRaw = workoutDoc.get("exercises") as? List<Map<String, Any>>


            val exercisesList = exercisesRaw?.map { exerciseMap ->

                Exercise(
                    exName = exerciseMap["exName"] as? String ?: "",
                    reps = exerciseMap["repetitions"] as? Long ?: 0,
                    sets = exerciseMap["series"] as? Long ?: 0
                )
            } ?: emptyList()


            workouts.add(
                Workout(
                    name = name,
                    level = level,
                    exercises = exercisesList,
                )
            )
        }

        return workouts

    }


}