package com.example.mis_plantitass.activities

import android.content.Intent
import android.os.Bundle
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
            val superhero =
                plantsList[position]  // Obtiene el superhéroe según la posición seleccionada.

            // Crea un Intent para abrir una nueva actividad con los detalles del superhéroe.
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(
                "PLANT_ID",
                superhero.id
            )  // Pasa el ID del superhéroe a la actividad de detalle.
            startActivity(intent)  // Inicia la actividad de detalle.
        }
        // Configura el RecyclerView con el adaptador y el LayoutManager.
        binding.recyclerView.adapter = adapter  // Asocia el adaptador con el RecyclerView.
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)  // Usa un GridLayout con 2 columnas.

        // Llama a la función para buscar plantas que contengan la letra "a".
        // searchPlantsByName("a")

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
                val result = service.findPlantsByQuery(query)  // Llama a la API con la consulta proporcionada.

                val filteredPlants = result.results.filter {
                    it.scientific_name.contains(query, ignoreCase = true)
                } // Almacena los resultados de la búsqueda en la lista de superhéroes.

                plantsList = filteredPlants
                // Lanza una corutina en el hilo principal para actualizar la interfaz de usuario.
                CoroutineScope(Dispatchers.Main).launch {
                    adapter.items = plantsList  // Actualiza los elementos del adaptador con la lista de superhéroes.
                    adapter.notifyDataSetChanged()  // Notifica al adaptador que los datos han cambiado.
                }
            } catch (e: Exception) {
                e.printStackTrace()  // Si ocurre un error, lo imprime en la consola.
            }
        }
    }

}