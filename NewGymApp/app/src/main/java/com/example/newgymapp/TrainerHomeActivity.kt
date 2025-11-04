package com.example.newgymapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebaseariketa.rvArtist.WorkoutAdapter
import com.example.newgymapp.model.Exercise
import com.example.newgymapp.model.User
import com.example.newgymapp.model.Workout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TrainerHomeActivity : AppCompatActivity() {

    val db = FirebaseSingleton.db
    var workouts: List<Workout> = emptyList()


    private lateinit var workoutAdapter: WorkoutAdapter;

    private lateinit var rvWorkout: RecyclerView
    private lateinit var wellcometv: TextView;
    private val auth = FirebaseSingleton.auth;
    private lateinit var itemCard: CardView;
    private lateinit var logout: ImageButton;
    private lateinit var filterRG: RadioGroup;
    private lateinit var profiletv: TextView
    private lateinit var togglecreateworkout: ImageButton;
    private lateinit var createworkoutcard: CardView
    private lateinit var addworkoutbtn: ImageButton;
    private lateinit var nametoaddetv: EditText;
    private lateinit var urltoadd: EditText;
    private lateinit var leveltoaddRG: RadioGroup;
    private lateinit var profilechangercv: CardView;
    private lateinit var profilebtn: ImageButton;
    private lateinit var profilepic: ImageView;
    private lateinit var profilepiceditor: ImageView

    private lateinit var profilename: EditText;
    private lateinit var profilelastname: EditText;
    private lateinit var profileemail: EditText;
    private lateinit var profilebirthday: EditText;
    private lateinit var profileurl: EditText;
    private lateinit var verifyimagebtn: ImageButton;
    private lateinit var applybtn: Button;
    private lateinit var lvl1tv: TextView
    private lateinit var lvl2tv: TextView
    private lateinit var lvl3tv: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_trainer)
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
        logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        filterRG.setOnCheckedChangeListener { group, checkedId ->

            val workoutorder = mapOf(
                "Beginner" to 1,
                "Middle" to 2,
                "Advanced" to 3
            )

            var filteredWorkouts: List<Workout> = when (checkedId) {
                R.id.beginnerRB -> {
                    workouts.filter { it.level.equals("Beginner", ignoreCase = true) }
                }

                R.id.middleRB -> {
                    workouts.filter { it.level.equals("Middle", ignoreCase = true) }
                }

                R.id.advancedRB -> {
                    workouts.filter { it.level.equals("Advanced", ignoreCase = true) }
                }

                R.id.allRB -> {

                    workouts.sortedBy { workoutorder[it.level] }
                }

                else -> {
                    workouts.sortedBy { workoutorder[it.level] }
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
        addworkoutbtn.setOnClickListener { //añadir workout a la bd
            val nameToAdd = nametoaddetv.text.toString()
            val urlToAdd = urltoadd.text.toString()
            val levelToAdd = when (leveltoaddRG.checkedRadioButtonId) {
                R.id.newBeginnerRB -> "Beginner"
                R.id.newMiddleRB -> "Middle"
                R.id.newAdvancedRB -> "Advanced"
                else -> "Beginner"
            }

            Log.i("UCM", "Adding workout: $nameToAdd, $urlToAdd, $levelToAdd")

            val newWorkout = Workout(
                name = nameToAdd,
                level = levelToAdd,
                exercises = listOf(),
                url = urlToAdd
            )

            CoroutineScope(Dispatchers.IO).launch {

                FirebaseSingleton.db.collection("workouts").add(
                    mapOf(
                        "name" to newWorkout.name,
                        "level" to newWorkout.level,
                        "url" to newWorkout.url,

                        )
                ).await()


                workouts = dbChargeWorkouts()

                withContext(Dispatchers.Main) {
                    val workoutorder = mapOf(
                        "Beginner" to 1,
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
        applybtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                dbUpdateUser()
            }

            val imageUrl = profileurl.text.toString()
            if (imageUrl.isNotBlank()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(profilepic)
            }
            wellcometv.text = "Hello " + profilename.text.toString().ifEmpty {
                auth.currentUser?.email?.substringBefore("@")
            } + "!"

            profiletv.text = profilename.text.toString().ifEmpty {
                auth.currentUser?.email?.substringBefore("@")
            }


        }

        verifyimagebtn.setOnClickListener {
            val imageUrl = profileurl.text.toString()
            if (imageUrl.isNotBlank()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(profilepiceditor)

            } else {
                profilepiceditor.setImageResource(R.drawable.heavyspace_bg)
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
        profilechangercv = findViewById(R.id.profilechangercvtrainer)
        profilebtn = findViewById(R.id.profilebtntrainer)

        profilepiceditor = findViewById(R.id.profileImagetrainer)
        profilename = findViewById(R.id.profileNameettrainer)
        profilelastname = findViewById(R.id.profileLastNameettrainer)
        profileemail = findViewById(R.id.profileEmailettrainer)
        profilebirthday = findViewById(R.id.profileBirthettrainer)
        profileurl = findViewById(R.id.profileUrlettrainer)
        profilepic = findViewById(R.id.porfilepic)
        applybtn = findViewById(R.id.applybtntrainer)
        verifyimagebtn = findViewById(R.id.verifyimagebtntrainer)
        lvl1tv = findViewById(R.id.lvl1txt)
        lvl2tv = findViewById(R.id.lvl2txt)
        lvl3tv = findViewById(R.id.lvl3txt)

    }

    private fun initUI() {
        val workoutorder = mapOf(
            "Beginner" to 1,
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

        lifecycleScope.launch(Dispatchers.IO) {
            val pfpurl = dbPFP()
            withContext(Dispatchers.Main) {
                Log.i("UCM", "PFP URL: $pfpurl")
                if (pfpurl.isNotBlank()) {
                    Glide.with(this@TrainerHomeActivity)
                        .load(pfpurl)
                        .into(profilepiceditor)

                    Glide.with(this@TrainerHomeActivity)
                        .load(pfpurl)
                        .into(profilepic)

                } else {
                    profilepic.setImageResource(R.drawable.heavyspace_bg)
                    profilepiceditor.setImageResource(R.drawable.heavyspace_bg)
                }
            }
        }


    }


    suspend fun dbChargeWorkouts(): List<Workout> {

        var workouts = mutableListOf<Workout>()
        val workoutSnapshot = FirebaseSingleton.db.collection("workouts").get().await()

        var contbeginner = 0
        var contmiddle = 0
        var contadvanced = 0

        for (workoutDoc in workoutSnapshot.documents) {
            val name = workoutDoc.getString("name") ?: ""
            val level = workoutDoc.getString("level") ?: ""
            val url = workoutDoc.getString("url") ?: ""

            when (level) {
                "Beginner" -> contbeginner += 1
                "Middle" -> contmiddle += 1
                "Advanced" -> contadvanced += 1
            }


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
                    url = url
                )
            )
        }
        withContext(Dispatchers.Main) {
            lvl1tv.text = contbeginner.toString()
            lvl2tv.text = contmiddle.toString()
            lvl3tv.text = contadvanced.toString()
        }


        return workouts

    }

    suspend fun dbUpdateUser() {
        var userSnapshot = FirebaseSingleton.db.collection("users").get().await()
        var user = User("", "", "", "", "", false)

        for (userDoc in userSnapshot.documents) {
            if (userDoc.getString("email") == auth.currentUser?.email.toString()) {
                user = User(
                    userDoc.getString("email") ?: "",
                    userDoc.getString("password") ?: "",
                    profilename.text.toString().trim().ifEmpty { userDoc.getString("name") ?: "" },
                    profilelastname.text.toString().trim()
                        .ifEmpty { userDoc.getString("lastName") ?: "" },
                    profilebirthday.text.toString().trim()
                        .ifEmpty { userDoc.getString("birthdate") ?: "" },
                    userDoc.getBoolean("trainer") ?: false,
                    profileurl.text.toString().trim()
                        .ifEmpty { userDoc.getString("profilepicurl") ?: "" }
                )

                db.collection("users").document(userDoc.id).set(user)
            }


        }
    }

    suspend fun dbPFP(): String {
        var userSnapshot = FirebaseSingleton.db.collection("users").get().await()
        var user = User("", "", "", "", "", false)

        for (userDoc in userSnapshot.documents) {
            if (userDoc.getString("email") == auth.currentUser?.email.toString()) {


                Log.i("UCM", "PFP URL found " + userDoc.getString("profilepicurl"))

                val url = userDoc.getString("profilepicurl") ?: ""
                return url

            }

        }
        return ""
    }


}