package com.example.newgymapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
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
private lateinit var calendarbtn: ImageButton;
private lateinit var calendarcard: androidx.cardview.widget.CardView;
private lateinit var calendar: CalendarView;
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


    private fun initListener() {
        btnSignUp.setOnClickListener {
            val auth = Firebase.auth
            auth.createUserWithEmailAndPassword(
                etUser.text.toString(),
                etPassword.text.toString()
            ) //se crea el usuario en firebase auth
                .addOnSuccessListener {

                    //si funciona, se inserta en la bd
                    var newuser = User( // se crea el objeto usuario
                        email = etUser.text.toString(),
                        password = etPassword.text.toString(),
                        name = etName.text.toString(),
                        lastName = etLastName.text.toString(),
                        birthdate = etBirthdate.text.toString(),
                        trainer = false

                    )
                    //el trainer se asigna segun el switch
                    if (switchbutton.isChecked) {
                        newuser.trainer = true
                    } else {
                        newuser.trainer = false
                    }

                    //busca la coleccion users y añade el nuevo usuario
                    db.collection("users").add(newuser)
                        .addOnSuccessListener {
                            Log.i("UCM", "usuario insertado en la bd con exito")
                        }.addOnFailureListener {
                            Log.i("UCM", "error al insertar usuario en la bd")
                        }

                    Log.i("UCM", "usuario insertado")
                    val intent =
                        Intent( //una vez insertado y creado en el auth de firestore, va a la home correspondiente
                            this,
                            if (newuser.trainer) TrainerHomeActivity::class.java else ClientHomeActivity::class.java
                        )
                    Log.i("UCM", "${intent}")
                    startActivity(intent)

                }.addOnFailureListener {// si no funciona, muestra mensajes de error
                    Log.i("UCM", "error de insercion de usuario")

                    //validacion de campos vacios
                    if (etUser.text.toString().isEmpty() || etPassword.text.toString()
                            .isEmpty() || etName.text.toString()
                            .isEmpty() || etLastName.text.toString()
                            .isEmpty() || etBirthdate.text.toString().isEmpty()
                    ) {
                        val toast = Toast.makeText(
                            applicationContext,
                            "Please fill in all fields",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()

                    } else if (etPassword.text.toString().length < 6) { //validacion de longitud de contraseña

                        val toast = Toast.makeText(
                            applicationContext,
                            "Password must be at least 6 characters",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()

                    }

                    val toast = Toast.makeText( //mensaje generico de error
                        applicationContext,
                        "You are already signed up",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }


        }
        loginlink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        calendarbtn.setOnClickListener { //muestra u oculta el calendario al pulsar el boton
            calendarcard.visibility =
                if (calendarcard.visibility == androidx.cardview.widget.CardView.VISIBLE) {
                    androidx.cardview.widget.CardView.GONE
                } else {
                    androidx.cardview.widget.CardView.VISIBLE
                }
        }

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "${dayOfMonth}/${month + 1}/$year"
            etBirthdate.setText(selectedDate)
            calendarcard.visibility = androidx.cardview.widget.CardView.GONE
        }


    }

    private fun initComponents() {
        btnSignUp = findViewById(R.id.btnSignUp)
        etUser = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        loginlink = findViewById(R.id.loginlinktv)
        switchbutton = findViewById(R.id.toggleusertype)
        etName = findViewById(R.id.etNewName)
        etLastName = findViewById(R.id.etNewLastName)
        etBirthdate = findViewById(R.id.etNewBirthdate)
        calendarbtn = findViewById(R.id.calendarbtn)
        calendarcard = findViewById(R.id.calendarcard)
        calendar = findViewById(R.id.calendar)

    }
}
