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

private lateinit var etEmail : EditText
private lateinit var etPassword : EditText
private lateinit var btnLogin: Button
private lateinit var signuplink: TextView

private var db = FirebaseSingleton.db

private lateinit var remembermeChekBox : CheckBox

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
            val intent = Intent(this, HomeActivity::class.java)

            auth.signInWithEmailAndPassword(etEmail.text.toString() + "@gmail.com", etPassword.text.toString())
                .addOnSuccessListener {
                    Log.i("UCM", "usuario logeado")
                    startActivity(intent)
                }
                .addOnFailureListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        val login = usersLogDB()
                        withContext(Dispatchers.Main) {
                            if (login) {
                                // Si existe en la BD, creamos el usuario en Firebase Auth y navegamos al Home
                                auth.createUserWithEmailAndPassword(
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
                            } else {
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

        // Listener separado para el link de signup
        signuplink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        remembermeChekBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.i("UCM", "Remember me checked")


            } else {
                Log.i("UCM", "Remember me unchecked")
                val auth = FirebaseSingleton.auth
                auth.currentUser

            }
        }

    }

    private fun initComponents() {
        btnLogin = findViewById(R.id.btnLogIn)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        signuplink = findViewById(R.id.signuplinktv)
        remembermeChekBox = findViewById(R.id.remembermecheckbox)
    }

    suspend fun usersLogDB() : Boolean {
        var userSnapshot = FirebaseSingleton.db.collection("users").get().await()

        for (userDoc in userSnapshot.documents) {
            if (userDoc.getString("email") == etEmail.text.toString() + "@gmail.com" &&
                userDoc.getString("password") == etPassword.text.toString()) {
                Log.i("UCM", "login seteado a true")
                return true
            }
        }
        Log.i("UCM", "login seteado a false")
        return false
    }
}