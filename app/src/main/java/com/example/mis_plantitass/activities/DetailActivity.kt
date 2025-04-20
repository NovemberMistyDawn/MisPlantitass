package com.example.mis_plantitass.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.R
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.mis_plantitass.data.*
import com.example.mis_plantitass.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailActivity : AppCompatActivity() {

    // Vista de binding para acceder a los elementos del layout activity_detail.xml
    lateinit var binding: ActivityDetailBinding
    // Variable para almacenar la planta que se cargó desde la API
    lateinit var plant: Plant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflamos y asignamos el layout usando view binding
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Extraemos información del intent (por ejemplo, imagen y nombre, para usos opcionales)
        val imageUrl = intent.getStringExtra("IMAGE_URL")
        val commonName = intent.getStringExtra("COMMON_NAME")

        // Cargamos la imagen con Glide (esto es opcional si loadData luego vuelve a cargarla)
        Glide.with(this)
            .load(imageUrl)
            .into(binding.pictureImageView)

        // Configuramos el padding del layout principal según los insets del sistema
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtenemos el ID de la planta del intent. Este debe estar correctamente enviado desde la actividad anterior.
        val id = intent.getIntExtra("PLANT_ID", -1)
        if (id == -1) {
            Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Llamamos a la función para obtener la planta por ID. Una vez que se cargue, se llamará loadCareGuide().
        getPlantById(id)
    }

    // Esta función carga los datos de la planta en la UI (la imagen, por ejemplo)
    fun loadData() {
        plant?.let {
            Picasso.get().load(it.default_image?.regular_url).into(binding.pictureImageView)
            // Aquí puedes asignar otros elementos si tienes, por ejemplo un TextView para el nombre:
            binding.plantNameDescription.text = it.common_name ?: "Sin nombre"
        }
    }

    // Esta función recaba la guía de cuidados para la planta
    fun loadCareGuide() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Creamos una instancia de Retrofit para la API de Care Guide
                val serviceCareGuide = getRetrofitCareGuide().create(CareGuideService::class.java)
                // Realizamos la llamada. NOTA: El endpoint funciona sin /v2, por eso la base URL es distinta.
                val careGuideResponse = serviceCareGuide.getCareGuideBySpeciesId("sk-sCUk67f500f7c4bc19647", plant.id)

                // (Suponemos que la función getCareGuideBySpeciesId devuelve directamente un CareGuideResponse)
                Log.d("CareGuide", "Respuesta recibida con ${careGuideResponse.data.size} guías")

                // Buscamos la guía que coincida con la planta
                val matchedGuide = careGuideResponse.data.find { it.species_id == plant.id }

                withContext(Dispatchers.Main) {
                    if (matchedGuide != null) {
                        Log.d("CareGuide", "Guía encontrada para: ${matchedGuide.common_name}")
                        showCareGuide(matchedGuide)
                    } else {
                        Toast.makeText(this@DetailActivity, "Guía de cuidado no encontrada", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailActivity, "No se pudo cargar la guía de cuidados", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Esta función muestra los detalles de la guía de cuidados en la UI
    fun showCareGuide(guide: CareGuide) {
        // Recorremos cada sección de la guía y asignamos el texto correspondiente a cada TextView
        for (section in guide.section) {
            when (section.type.lowercase()) {
                "sunlight" -> {
                    binding.sunlightTextView.text = "🔹 Sunlight\n${section.description}"
                }
                "watering" -> {
                    binding.wateringTextView.text = "🔹 Watering\n${section.description}"
                }
                else -> {
                    Log.d("CareGuide", "Sección desconocida: ${section.type}")
                }
            }
        }
    }

    // Función para crear Retrofit para la API de plantas (v2)
    fun getRetrofit(): Retrofit {
        // Agrega un interceptor para ver la URL en Logcat (opcional, pero muy útil)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        return Retrofit.Builder()
            .baseUrl("https://perenual.com/api/v2/")  // Base URL para info de plantas
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Función para crear Retrofit para la API de care guide (sin v2)
    fun getRetrofitCareGuide(): Retrofit {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        return Retrofit.Builder()
            .baseUrl("https://perenual.com/api/")  // Base URL sin v2 para care guide
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Función que obtiene la planta por su ID y luego carga la guía de cuidados
    fun getPlantById(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Crear el servicio para obtener la planta (v2)
                val plantService = getRetrofit().create(PlantService::class.java)
                val plantFromApi: Plant = plantService.findPlantById(id,"sk-sCUk67f500f7c4bc19647")

                if (plantFromApi != null) {
                    withContext(Dispatchers.Main) {
                        plant = plantFromApi
                        Log.d("PlantDetails", "Planta cargada: ${plant.common_name}")
                        loadData()
                    }
                    // Una vez que tenemos la planta, cargamos la guía de cuidados
                    loadCareGuide()
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DetailActivity, "Planta no encontrada", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}