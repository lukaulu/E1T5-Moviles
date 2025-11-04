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

private lateinit var btnSignUp: Button;
private lateinit var btnLogin: Button;

private val auth = FirebaseSingleton.auth


class MainActivity : AppCompatActivity() {


    //al empezar la app, chequea si hay un usuario logueado
    override fun onStart() {
        super.onStart()
        //el auth es de firebase, para ver usuarios y asi
        var currentUser = auth.currentUser
        if (currentUser != null) {
            //se usa una corrutina para no bloquear el hilo principal, ya que se llama a la bd y no es instantaneo
            CoroutineScope(Dispatchers.IO).launch {

                //mira si es trainer o no y hace el intent, que basicamente es ir a otra actividad
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

            //mensaje de bienvenida
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


    private fun initListeners() { //inicializa los botones para ir a las otras actividades

        btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)

        }
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initComponents() { //asocia los botones con los del layout
        btnSignUp = findViewById(R.id.btnSignUpMain);
        btnLogin = findViewById(R.id.btnLogin);
    }


    suspend fun isTrainer(): Boolean { //funcion que mira si el usuario es trainer o no
        var currentUser = auth.currentUser
        var userSnapshot = FirebaseSingleton.db.collection("users").get().await()

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

}