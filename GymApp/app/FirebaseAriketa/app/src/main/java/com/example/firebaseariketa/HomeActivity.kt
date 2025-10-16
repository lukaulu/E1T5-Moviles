package com.example.firebaseariketa

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseariketa.model.Artist
import com.example.firebaseariketa.model.Song
import com.example.firebaseariketa.rvArtist.ArtistAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.random.Random

class HomeActivity : AppCompatActivity() {

    val db = FirebaseSingleton.db
    var artistas :List<Artist> = emptyList()

    private lateinit var artistAdapter: ArtistAdapter
    private lateinit var rvArtist:RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Prueba de insercion
        //insertarArtista()
        initComponents()
        initUI()

    }

    private fun initComponents() {
        rvArtist = findViewById(R.id.rvArtist)
    }

    private fun initUI() {

        artistAdapter = ArtistAdapter(artistas)
        rvArtist.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        rvArtist.adapter = artistAdapter

        CoroutineScope(Dispatchers.IO).launch {
            artistas = LeerArtista()
            Log.i("ROI","artistas $artistas")

            withContext(Dispatchers.Main) {
                //Tengo que volver a pasarle la lista al adapter una vez que ha terminado el hilo suspendido, y actualizarlo
                artistAdapter.artist= artistas
                artistAdapter.notifyDataSetChanged()
            }
        }


    }


    suspend fun LeerArtista(): List<Artist> {
        val db = FirebaseSingleton.db
        val listaArtists = mutableListOf<Artist>()

        try {
            val artistsSnapshot = db.collection("artists").get().await()

            for (artistDoc in artistsSnapshot.documents) {
                val name = artistDoc.getString("name") ?: ""
                val description = artistDoc.getString("description") ?: ""
                val image = artistDoc.getString("image") ?: ""
                val artistId = artistDoc.id

                val songsSnapshot = db.collection("artists")
                    .document(artistId)
                    .collection("songs")
                    .get()
                    .await()


                val songsList = songsSnapshot.documents.map { songDoc ->
                    Song(
                        name = songDoc.getString("name") ?: "",
                        duration = songDoc.getLong("duration")?.toInt() ?: 0
                    )
                }

                listaArtists.add(Artist(name, description,image, songsList))

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listaArtists
    }



    private fun insertarArtista() {

        val db = FirebaseSingleton.db

        for (i in 1..10) {
            val random = Random.nextInt(0, 100)

            val canciones =
                listOf(
                    Song("Canción 1", 210),
                    Song("Canción 2", 180),
                    Song("Canción 3", 240)
                )


            val artista = Artist(
                name = "Artista $random",
                description = "Descripcion $random",
                image = "https://link-a-la-imagen.com/$random.jpg",
                songs = canciones
            )


            // Creamos una copia del artista sin la lista de canciones para guardar solo los datos básicos
            val artistaParaGuardar = hashMapOf(
                "name" to artista.name,
                "description" to artista.description,
                "image" to artista.image,
            )

            db.collection("artists")
                .add(artistaParaGuardar)
                .addOnSuccessListener { artistDocRef ->
                    Log.i("ROI", "Artista $artistDocRef insertado")
                    // Guardar canciones en la subcolección "songs"
                    for (song in artista.songs) {
                        artistDocRef.collection("songs")
                            .add(song)
                            .addOnSuccessListener { songDocRef ->
                                Log.i("ROI", "Cancion $songDocRef insertado")
                            }
                            .addOnFailureListener { e ->
                                println("Error: $e")
                            }
                    }
                }
                .addOnFailureListener { e ->
                    println("Error: $e")
                }
        }
    }

}