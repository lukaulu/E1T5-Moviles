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
import com.example.firebaseariketa.rvArtist.HistoricalAdapter
import com.example.firebaseariketa.rvArtist.WorkoutAdapter
import com.example.newgymapp.model.Exercise
import com.example.newgymapp.model.HistoricalWorkout
import com.example.newgymapp.model.User
import com.example.newgymapp.model.Workout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.bumptech.glide.Glide


class ClientHomeActivity : AppCompatActivity() {

    private val db = FirebaseSingleton.db
    private var workouts: List<Workout> = emptyList()
    private var historical: List<HistoricalWorkout> = emptyList()
    private lateinit var workoutAdapter: WorkoutAdapter;
    private lateinit var historicalAdapter: HistoricalAdapter;
    private lateinit var rvWorkout: RecyclerView
    private lateinit var rvHistorical: RecyclerView
    private lateinit var wellcometv: TextView;
    private val auth = FirebaseSingleton.auth;
    private lateinit var itemCard: CardView;
    private lateinit var logout: ImageButton;
    private lateinit var filterRG: RadioGroup;
    private lateinit var profiletv: TextView
    private lateinit var historicalButton: ImageButton;
    private lateinit var historicalCard: CardView
    // profile comps
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

    private lateinit var settingsbtn: ImageView
    private lateinit var languagebtn: ImageView
    private lateinit var themebtn: ImageView
    private lateinit var settingscv: CardView





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_client)
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
                    workouts
                }

                else -> {
                    workouts
                }
            }

            workoutAdapter.workouts = filteredWorkouts
            workoutAdapter.notifyDataSetChanged()
        }

        historicalButton.setOnClickListener {
            if (historicalCard.visibility == CardView.VISIBLE) {
                historicalCard.visibility = CardView.GONE
            } else {
                historicalCard.visibility = CardView.VISIBLE
                profilechangercv.visibility = CardView.GONE
            }

        }

        profilebtn.setOnClickListener {
            if (profilechangercv.visibility == CardView.VISIBLE) {
                profilechangercv.visibility = CardView.GONE
            } else {
                profilechangercv.visibility = CardView.VISIBLE
                historicalCard.visibility = CardView.GONE
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

        settingsbtn.setOnClickListener {
            if (themebtn.visibility == ImageButton.VISIBLE) {
                themebtn.visibility = ImageButton.GONE
                languagebtn.visibility = ImageButton.GONE
            } else {
                themebtn.visibility = ImageButton.VISIBLE
                languagebtn.visibility = ImageButton.VISIBLE
            }
        }


    }

    private fun initComponents() {
        filterRG = findViewById(R.id.radioGroupLevels)
        filterRG.check(R.id.allRB)
        wellcometv = findViewById(R.id.wellcometv)
        profiletv = findViewById(R.id.profiletv)
        val currentUser = auth.currentUser
        wellcometv.text = "Hello " + currentUser?.email?.substringBefore("@") + "!"
        profiletv.text = currentUser?.email?.substringBefore("@")

        rvWorkout = findViewById(R.id.rvWorkout)
        logout = findViewById(R.id.logoutbtn)
        rvHistorical = findViewById(R.id.rvHistorical)
        historicalButton = findViewById(R.id.historicalWorkout)
        historicalCard = findViewById(R.id.historicalCV)
        profilechangercv = findViewById(R.id.profilechangercv)
        profilebtn = findViewById(R.id.profilebtn)

        profilepiceditor = findViewById(R.id.profileImage)
        profilename = findViewById(R.id.profileNameet)
        profilelastname = findViewById(R.id.profileLastNameet)
        profileemail = findViewById(R.id.profileEmailet)
        profilebirthday = findViewById(R.id.profileBirthet)
        profileurl = findViewById(R.id.profileUrlet)
        profilepic = findViewById(R.id.porfilepic)
        applybtn = findViewById(R.id.applybtn)
        verifyimagebtn = findViewById(R.id.verifyimagebtn)

        settingsbtn = findViewById(R.id.settingsbtn)
        languagebtn = findViewById(R.id.languagebtn)
        themebtn = findViewById(R.id.themebtn)
        settingscv = findViewById(R.id.settingscv)



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
                workoutAdapter.notifyDataSetChanged()

            }
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val pfpurl = dbPFP()
            withContext(Dispatchers.Main) {
                Log.i("UCM", "PFP URL: $pfpurl")
                if (pfpurl.isNotBlank()) {
                    Glide.with(this@ClientHomeActivity)
                        .load(pfpurl)
                        .into(profilepiceditor)

                    Glide.with(this@ClientHomeActivity)
                        .load(pfpurl)
                        .into(profilepic)

                } else {
                    profilepic.setImageResource(R.drawable.heavyspace_bg)
                    profilepiceditor.setImageResource(R.drawable.heavyspace_bg)
                }
            }
        }






        historicalAdapter = HistoricalAdapter(historical)
        rvHistorical.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvHistorical.adapter = historicalAdapter

        CoroutineScope(Dispatchers.IO).launch {
            historical = dbChargeHistorical()

            withContext(Dispatchers.Main) {
                historicalAdapter.historical = historical
                historicalAdapter.notifyDataSetChanged()

            }
        }


    }


    suspend fun dbChargeWorkouts(): List<Workout> {

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

    suspend fun dbChargeHistorical(): List<HistoricalWorkout> {
        Log.i("UCM", "llega 2")

        var historicalworkouts = mutableListOf<HistoricalWorkout>()
        val usersSnapshot = FirebaseSingleton.db.collection("users").get().await()

// 2. Iterar sobre cada documento (usuario)
        for (userDocument in usersSnapshot.documents) {
            val userId = userDocument.id
            Log.i("UCM", "Procesando usuario ID: $userId")

            // 3. Obtener la subcolección de este documento de usuario
            val subcollectionSnapshot = userDocument.reference
                .collection("historicalWorkouts")
                .get()
                .await()
            Log.i("UCM", subcollectionSnapshot.documents.size.toString())
            for (subDoc in subcollectionSnapshot.documents) {

                val name = subDoc.getString("workoutName") ?: ""
                val level = subDoc.getString("level") ?: ""
                val time = subDoc.getLong("time") ?: 0
                val date = subDoc.getString("date") ?: ""
                val percentage = subDoc.getLong("percentage") ?: 0

                historicalworkouts.add(
                    HistoricalWorkout(
                        name = name,
                        level = level,
                        time = time,
                        date = date,
                        percentage = percentage
                    )
                )


            }
        }

        return historicalworkouts

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