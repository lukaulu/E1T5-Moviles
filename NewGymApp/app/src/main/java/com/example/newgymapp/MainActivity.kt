package com.example.newgymapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

private lateinit var btnSignUp : Button;
private lateinit var btnLogin : Button;

class MainActivity : AppCompatActivity() {

    /*
    override fun onStart() {
        super.onStart()
        val auth = FirebaseSingleton.auth
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

            val toast = Toast.makeText(
                applicationContext,
                "Wellcome back " + currentUser?.email + "!",
                Toast.LENGTH_SHORT
            )
            toast.show()}

    }

     */



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
}