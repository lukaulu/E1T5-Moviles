package com.example.newgymapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newgymapp.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import kotlin.text.get
import kotlin.text.toInt


private lateinit var etEmail : EditText;
private lateinit var etPassword : EditText;

private lateinit var  btnLogin: Button;
private lateinit var signuplink: TextView;
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

    private fun initListeners(){


        btnLogin.setOnClickListener {
            val auth = Firebase.auth
            auth.signInWithEmailAndPassword(etEmail.text.toString()+ "@gmail.com", etPassword.text.toString())
                .addOnSuccessListener {
                    Log.i("UCM", "usuario logeado")
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)

                }.addOnFailureListener {
                    val users = mutableListOf<User>()




                }

        }
        signuplink.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
    private fun initComponents(){
        btnLogin = findViewById(R.id.btnLogIn)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        signuplink = findViewById(R.id.signuplinktv)
    }


}