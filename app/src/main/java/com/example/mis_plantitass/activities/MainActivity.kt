package com.example.mis_plantitass.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mis_plantitass.adapters.PlantsAdapter
import com.example.mis_plantitass.R
import com.example.mis_plantitass.data.Plant
import com.example.mis_plantitass.data.PlantService
import com.example.mis_plantitass.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var adapter: PlantsAdapter


    //esto tiene que tener la lista recibida de la Api
    var plantsList: List<Plant> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = PlantsAdapter(plantsList) { position ->
            val plant = plantsList[position]
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("IMAGE_URL", plant.default_image?.original_url)
            intent.putExtra("COMMON_NAME", plant.common_name)
            intent.putExtra("PLANT_ID", plant.id)
            startActivity(intent)
        }


        // Configura el RecyclerView con el adaptador y el LayoutManager.
        binding.recyclerView.adapter = adapter  // Asocia el adaptador con el RecyclerView.
        binding.recyclerView.layoutManager =
            GridLayoutManager(this, 2)  // Usa un GridLayout con 2 columnas.

        // Llama a la función para buscar plantas que contengan la letra "a".
        searchPlantsByName("a")

    }


    // Este metodo se ejecuta cuando se crea el menú de opciones.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)

        val menuItem = menu?.findItem(R.id.action_search) // Obtén el item
        val searchView = menuItem?.actionView as SearchView // Asegúrate de que es un SearchView

        // Configura el listener para los eventos de búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Ejecuta la búsqueda cuando el usuario presiona el botón "Buscar"
                searchPlantsByName(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return true  // Devuelve verdadero para que el menú se muestre.
    }

    fun getRetrofit(): PlantService {
        val retrofit = Retrofit.Builder()

            //CAMBIAR URL ESTA MAL
            .baseUrl("https://perenual.com/api/v2/")  // URL base de la API.
            .addConverterFactory(GsonConverterFactory.create())  // Usamos Gson para convertir la respuesta JSON en objetos.
            .build()

        return retrofit.create(PlantService::class.java)  // Crea el servicio para interactuar con la API.
    }

    // Esta función busca plantas por nombre.
    fun searchPlantsByName(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("API_REQUEST", "Buscando plantas con el query: $query")

                val service = getRetrofit()
                val result = service.findPlantsByQuery(query, 1, "sk-sCUk67f500f7c4bc19647")

                // Comprobar la respuesta cruda para ver cómo se deserializa
                val gson = Gson()
                val jsonResponse = gson.toJson(result)
                Log.d("API_RAW_RESPONSE", "Respuesta cruda de la API: $jsonResponse")

                // Verificar si hay datos en la respuesta
                if (result != null && result.data.isNotEmpty()) {
                    Log.d("API_RESPONSE", "Plantas encontradas: ${result.data.size}")
                    withContext(Dispatchers.Main) {
                        plantsList = result.data
                        adapter.updateData(plantsList)  // Actualizamos el adaptador
                    }
                } else {
                    Log.d("API_RESPONSE", "No se encontraron resultados")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "No se encontraron plantas", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("API_ERROR", "Error en la llamada a la API: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error al buscar plantas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}