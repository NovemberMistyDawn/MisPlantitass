package com.example.mis_plantitass.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            val plant =
                plantsList[position]  // Obtiene el superhéroe según la posición seleccionada.

            // Crea un Intent para abrir una nueva actividad con los detalles del superhéroe.
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(
                "PLANT_ID",
                plant.id
            )  // Pasa el ID del superhéroe a la actividad de detalle.
            startActivity(intent)  // Inicia la actividad de detalle.
        }
        // Configura el RecyclerView con el adaptador y el LayoutManager.
        binding.recyclerView.adapter = adapter  // Asocia el adaptador con el RecyclerView.
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)  // Usa un GridLayout con 2 columnas.

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
                // Ejecuta la búsqueda mientras el usuario escribe
                searchPlantsByName(newText ?: "")
                return true
            }
        })

        return true  // Devuelve verdadero para que el menú se muestre.
    }

    fun getRetrofit(): PlantService{
        val retrofit = Retrofit.Builder()

            //CAMBIAR URL ESTA MAL
            .baseUrl("https://perenual.com/api/v2/")  // URL base de la API.
            .addConverterFactory(GsonConverterFactory.create())  // Usamos Gson para convertir la respuesta JSON en objetos.
            .build()

        return retrofit.create(PlantService::class.java)  // Crea el servicio para interactuar con la API.
    }

    // Esta función busca superhéroes por nombre.
    fun searchPlantsByName(query: String) {
        // Lanza una corutina en el hilo de entrada/salida (IO) para realizar la búsqueda en segundo plano.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()  // Obtiene el servicio Retrofit para la API.
                val result = service.findPlantsByQuery(query, "sk-sCUk67f500f7c4bc19647") // Llama a la API con el 'query' como parámetro.

                // Verifica si 'result' o 'result.results' son nulos
                if (result?.results != null && result.results.isNotEmpty()) {
                    Log.d("API_RESPONSE", "Resultados encontrados: ${result.results.size}")

                    // Muestra todos los nombres comunes de las plantas obtenidas
                    result.results.forEach {
                        Log.d("API_RESPONSE", "Planta: ${it.common_name}")
                    }

                    // Filtra las plantas que contienen el 'query' (sin usar contains)
                    val filteredPlants = result.results.filter {
                        // Compara directamente el nombre común y científico con la consulta
                        it.common_name.lowercase().startsWith(query.lowercase()) ||
                                it.scientific_name.lowercase().startsWith(query.lowercase())
                    }

                    Log.d("API_RESPONSE", "Plantas filtradas: ${filteredPlants.size}")

                    // Actualiza la lista de plantas
                    plantsList = filteredPlants

                    // Lanza una corutina en el hilo principal para actualizar la interfaz de usuario
                    CoroutineScope(Dispatchers.Main).launch {
                        adapter.items = plantsList  // Actualiza los elementos del adaptador con las plantas filtradas
                        adapter.notifyDataSetChanged()  // Notifica al adaptador que los datos han cambiado
                    }
                } else {
                    // Si no hay resultados o es null, maneja el caso aquí
                    Log.d("API_RESPONSE", "No se encontraron resultados en la API.")
                    plantsList = listOf()  // Si no se encuentran plantas, vacía la lista
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("API_ERROR", "Error al realizar la búsqueda: ${e.message}")
            }
        }
    }

}