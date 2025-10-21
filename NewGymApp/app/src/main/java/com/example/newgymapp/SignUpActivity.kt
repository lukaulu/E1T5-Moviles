package com.example.newgymapp

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
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
private lateinit var etName: EditText;
private lateinit var etLastName: EditText;
private lateinit var etBirthdate: EditText;


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
                        password = etPassword.text.toString(),
                        name = etName.text.toString(),
                        lastName = etLastName.text.toString(),
                        birthdate = etBirthdate.text.toString(),

                    )
                    if (switchbutton.isChecked){
                        newuser.trainer = true
                    }else{
                        newuser.trainer = false
                    }

                    db.collection("users").add(newuser)
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
                    val toast = Toast.makeText(
                        applicationContext,
                        "You are already signed up",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()}



        }
        loginlink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }



    }

    private fun initComponents(){
        btnSignUp= findViewById(R.id.btnSignUp)
        etUser= findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        loginlink = findViewById(R.id.loginlinktv)
        switchbutton = findViewById(R.id.toggleusertype)
        etName= findViewById(R.id.etNewName)
        etLastName= findViewById(R.id.etNewLastName)
        etBirthdate = findViewById(R.id.etNewBirthdate)

    }
}
