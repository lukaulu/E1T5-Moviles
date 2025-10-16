package com.example.firebaseariketa

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebaseariketa.databinding.ActivityMainBinding
import com.example.firebaseariketa.login.LoginActivity
import com.example.firebaseariketa.rvArtist.ArtistAdapter
import com.example.firebaseariketa.signup.SignUpActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding


    //private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        //auth = Firebase.auth

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*
        binding.tvHellowWorld.setOnClickListener {
            throw RuntimeException("A la mierda!!!")
        }
        */

        InitListeners()

    }


    override fun onStart() {
        super.onStart()
        val auth = FirebaseSingleton.auth
        val currentUser = auth.currentUser
        if(currentUser != null){
            //navegar a la home
            //val intent = Intent(this, HomeActivity::class.java)
            //startActivity(intent)
        }
    }

    private fun InitListeners() {
        binding.btnSignUp.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.tvLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}