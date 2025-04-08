package com.example.mis_plantitass.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mis_plantitass.R
import com.example.mis_plantitass.data.Plant
import com.example.mis_plantitass.data.PlantService
import com.example.mis_plantitass.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding
    lateinit var plant: Plant


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())  // Obtiene los márgenes de las barras del sistema.
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )  // Ajusta el padding de la vista principal.
            insets  // Devuelve los márgenes ajustados.
        }

        val id = intent.getStringExtra("PLANT_ID")!!.toInt()

        getPlantById(id)


    }

    fun loadData(){

        Picasso.get().load(plant.default_image.url).into(binding.pictureImageView)
    }



    fun getRetrofit(): PlantService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://perenual.com/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PlantService::class.java)
    }


    fun getPlantById(id: Int){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()  // Obtiene el servicio Retrofit.
                plant = service.findPlantById(id)  // Llama a la API para obtener el superhéroe con el ID.

                // Lanza una corutina en el hilo principal para actualizar la UI.
                CoroutineScope(Dispatchers.Main).launch {
                    loadData()  // Llama a la función para cargar los datos del superhéroe en la UI.
                }
            } catch (e: Exception) {
                e.printStackTrace()  // Si ocurre un error, lo imprime en la consola.
            }
        }
    }

}
