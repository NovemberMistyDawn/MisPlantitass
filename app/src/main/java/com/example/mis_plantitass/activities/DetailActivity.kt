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
import android.widget.Toast
import kotlinx.coroutines.delay
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

    fun loadData() {
        // Asegúrate de que plant esté inicializado
        plant?.let {
            Picasso.get().load(it.default_image?.regular_url).into(binding.pictureImageView)
        }
    }


    fun getRetrofit(): PlantService {
        val retrofit = Retrofit.Builder()


            .baseUrl("https://perenual.com/api/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PlantService::class.java)
    }


    fun getPlantById(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                val service = getRetrofit()  // Obtiene el servicio Retrofit.
                val result = service.findPlantById(id)  // Llama a la API para obtener la planta con el ID.

                val plantFromApi = result

                // Si no se obtiene la planta, muestra un mensaje de error en el hilo principal.
                if (plantFromApi != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        plant = plantFromApi
                        loadData()  // Carga los datos en la UI.
                    }
                } else {
                    // Muestra el Toast en el hilo principal si la planta no se encuentra.
                    CoroutineScope(Dispatchers.Main).launch {
                        Toast.makeText(this@DetailActivity, "Planta no encontrada", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

                // Asegúrate de que el Toast se ejecute en el hilo principal
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@DetailActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
