// kotlin
package com.example.newgymapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
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
import kotlinx.coroutines.withContext

private lateinit var etEmail: EditText
private lateinit var etPassword: EditText
private lateinit var btnLogin: Button
private lateinit var signuplink: TextView

private var db = FirebaseSingleton.db


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        initListeners()
    }

    private fun initListeners() {

        btnLogin.setOnClickListener {

            val auth = FirebaseSingleton.auth
            var user = User("", "", "", "", "", false)

            //se llama a la bd para cargar el usuario
            CoroutineScope(Dispatchers.IO).launch {
                user = usersChargeDB()

            }
            //validacion de campos vacios
            if (etEmail.text.toString().isEmpty() || etPassword.text.toString().isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                auth.signInWithEmailAndPassword( //intenta logear con firebase
                    etEmail.text.toString() + "@gmail.com",
                    etPassword.text.toString()
                )
                    .addOnSuccessListener {
                        Log.i("UCM", "usuario logeado")

                        val intent = Intent( // si tiene exito, va a la home correspondiente
                            this,
                            if (user.trainer) TrainerHomeActivity::class.java else ClientHomeActivity::class.java // un if inline para elegir la actividad segun si es trainer o no
                        )
                        Log.i("UCM", "${intent}")
                        startActivity(intent)
                    }
                    .addOnFailureListener {//aunque no este en firebase como user, puede estar en la bd, asi que se comprueba ahi
                        val intent =
                            Intent( //se prepara el intent para la home correspondiente, ya que no se puede dentro de la corrutina
                                this,
                                if (user.trainer) TrainerHomeActivity::class.java else ClientHomeActivity::class.java
                            )
                        CoroutineScope(Dispatchers.IO).launch {
                            val login =
                                usersLogDB() //comprueba en la bd si el usuario y contraseña son correctos
                            withContext(Dispatchers.Main) {
                                if (login) {
                                    auth.createUserWithEmailAndPassword( //si el usuario esta en la bd pero no en firebase como user, lo crea (por ejemplo si se crea a mano en la bd)
                                        etEmail.text.toString() + "@gmail.com",
                                        etPassword.text.toString()
                                    ).addOnSuccessListener {

                                        startActivity(intent)

                                    }.addOnFailureListener { ex ->
                                        Toast.makeText(
                                            applicationContext,
                                            "Error creating user: ${ex.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else { //si no esta en la bd ni en el user de firebase, mensaje de error
                                    Log.i("UCM", "Error login")
                                    Toast.makeText(
                                        applicationContext,
                                        "The user and password are wrong",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
            }
        }

        // Listener separado para el link de signup
        signuplink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }


    }

    private fun initComponents() {
        btnLogin = findViewById(R.id.btnLogIn)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        signuplink = findViewById(R.id.signuplinktv)
    }

    suspend fun usersLogDB(): Boolean {
        val userSnapshot = FirebaseSingleton.db.collection("users").get().await()

        for (userDoc in userSnapshot.documents) {
            if (userDoc.getString("email")
                    ?.lowercase() == (etEmail.text.toString() + "@gmail.com").lowercase() &&
                userDoc.getString("password") == etPassword.text.toString()
            ) {

                Log.i("UCM", "login seteado a true")

                return true
            }
        }
        Log.i("UCM", "login seteado a false")
        return false
    }

    suspend fun usersChargeDB(): User {
        val userSnapshot = FirebaseSingleton.db.collection("users").get().await()
        var user = User("", "", "", "", "", false)

        for (userDoc in userSnapshot.documents) {
            if (userDoc.getString("email") == etEmail.text.toString() + "@gmail.com" &&
                userDoc.getString("password") == etPassword.text.toString()
            ) {
                user = User(
                    userDoc.getString("email") ?: "",
                    userDoc.getString("password") ?: "",
                    userDoc.getString("name") ?: "",
                    userDoc.getString("lastName") ?: "",
                    userDoc.getString("birthdate") ?: "",
                    userDoc.getBoolean("trainer") ?: false
                )
            }


        }
        return user

    }
}