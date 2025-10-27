package com.example.newgymapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newgymapp.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private lateinit var btnSignUp : Button;
private lateinit var btnLogin : Button;

private val auth = FirebaseSingleton.auth


class MainActivity : AppCompatActivity() {


    override fun onStart() {
        super.onStart()
        var currentUser = auth.currentUser
        if(currentUser != null){

            CoroutineScope(Dispatchers.IO).launch {
                val trainer = isTrainer()
                    if (trainer) {
                        Log.i("MAIN_ACTIVITY", "User is a trainer")
                        val intent = Intent(this@MainActivity, TrainerHomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.i("MAIN_ACTIVITY", "User is not a trainer")
                        val intent = Intent(this@MainActivity, ClientHomeActivity::class.java)
                        startActivity(intent)
                }
            }


            val toast = Toast.makeText(
                applicationContext,
                "Wellcome back " + currentUser?.email + "!",
                Toast.LENGTH_SHORT
            )
            toast.show()


        }

    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        initListeners()
    }



    private fun  initListeners(){

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initComponents(){
        btnSignUp = findViewById(R.id.btnSignUpMain);
        btnLogin = findViewById<Button>(R.id.btnLogin);
    }


    suspend fun isTrainer() : Boolean {
        var currentUser = auth.currentUser
        var userSnapshot = FirebaseSingleton.db.collection("users").get().await()
        var user = User("", "", "", "", "", false)

        for (userDoc in userSnapshot.documents) {
            if (userDoc.getString("email") == currentUser?.email) {
                if (userDoc.getBoolean("trainer") == true) {
                    return true
                } else {
                    return false
                }

            }

        }
        return false
    }
        /*
    suspend fun dbCharge() {



        var users = mutableListOf<User>()
        var userSnapshot = FirebaseSingleton.db.collection("users").get().await()

        for (userDoc in userSnapshot.documents) {
            val name = userDoc.getString("name") ?: ""
            val lastName = userDoc.getString("lastName") ?: ""
            val email = userDoc.getString("email") ?: ""
            val password = userDoc.getString("password") ?: ""
            val trainer = userDoc.getBoolean("trainer") ?: ""


            users.add(User(
                name, lastName, email, password, trainer as Boolean
            ))

        }

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
                    exercises = exercisesList
                )
            )
        }



        return ;
    }



 */


    }