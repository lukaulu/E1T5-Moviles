package com.example.newgymapp

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newgymapp.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

private lateinit var btnSignUp: Button;
private lateinit var etUser: EditText;
private lateinit var etPassword: EditText;
private lateinit var loginlink: TextView;
private lateinit var switchbutton: Switch;

private var db = FirebaseSingleton.db

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        initComponents()
        initListener()
    }


    private fun initListener(){
        btnSignUp.setOnClickListener {
            val auth = Firebase.auth
            auth.createUserWithEmailAndPassword(etUser.text.toString(), etPassword.text.toString())
                .addOnSuccessListener {


                    var newuser = User(
                        email = etUser.text.toString(),
                        password = etPassword.text.toString()
                    )
                    if (switchbutton.isChecked){
                        newuser.trainer = true
                    }else{
                        newuser.trainer = false
                    }

                    db.collection("Usurios").add(newuser)
                        .addOnSuccessListener {
                            Log.i("UCM","usuario insertado en la bd con exito")
                        }.addOnFailureListener {
                            Log.i("UCM","error al insertar usuario en la bd")
                        }

                    Log.i("UCM", "usuario insertado")
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)

                }.addOnFailureListener {
                    Log.i("UCM", "error de insercion de usuario")

                }

        }
        loginlink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        switchbutton.setOnClickListener {
            if (switchbutton.isChecked){

            }
        }
    }

    private fun initComponents(){
        btnSignUp= findViewById(R.id.btnSignUp)
        etUser= findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        loginlink = findViewById(R.id.loginlinktv)
        switchbutton = findViewById(R.id.toggleusertype)

    }
}
