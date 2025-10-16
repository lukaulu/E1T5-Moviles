package com.example.firebaseariketa.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebaseariketa.FirebaseSingleton
import com.example.firebaseariketa.HomeActivity
import com.example.firebaseariketa.R


class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogIn:Button

    val auth = FirebaseSingleton.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponentes()
        setLisenners()
    }

    private fun initComponentes() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogIn = findViewById(R.id.btnLogIn)
    }

    private fun setLisenners() {

        btnLogIn.setOnClickListener{
            auth.signInWithEmailAndPassword(etEmail.text.toString(),etPassword.text.toString())
                .addOnSuccessListener {
                    Log.i("ROI","Login OK")
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.i("ROI","Login KO")
                    Toast.makeText(this, "Email o Password incorrectos", Toast.LENGTH_SHORT).show()
                }
        }
    }
}