package com.example.mis_plantitass.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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

        /*menu?.findItem(R.id.action_filter)?.setOnMenuItemClickListener {
            showFilterDialog()
            true
        }*/

        return true  // Devuelve verdadero para que el menú se muestre.



    }

    fun getRetrofit(): PlantService {
        val retrofit = Retrofit.Builder()


            .baseUrl("https://perenual.com/api/v2/")  // URL base de la API.
            .addConverterFactory(GsonConverterFactory.create())  // Usamos Gson para convertir la respuesta JSON en objetos.
            .build()

        return retrofit.create(PlantService::class.java)  // Crea el servicio para interactuar con la API.
    }

    /*fun showFilterDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filters, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Filtros de Búsqueda")
            .setView(dialogView)
            .setPositiveButton("Aplicar") { _, _ ->
                val type = dialogView.findViewById<Spinner>(R.id.spinnerType).selectedItem.toString()
                val maintenance = dialogView.findViewById<Spinner>(R.id.spinnerMaintenance).selectedItem.toString()

                // Llama a la búsqueda usando esos filtros
                searchPlantsByNameWithFilters("a", type, maintenance)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        val typeSpinner = dialogView.findViewById<Spinner>(R.id.spinnerType)
        val maintenanceSpinner = dialogView.findViewById<Spinner>(R.id.spinnerMaintenance)

        val types = listOf("tree", "herb", "shrub", "all") // Puedes añadir más tipos si conoces otros
        val maintenanceLevels = listOf("Low", "Medium", "High", "all")

        typeSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)
        maintenanceSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, maintenanceLevels)



        dialog.show()
    }*/

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


   /* fun searchPlantsByNameWithFilters(query: String, type: String, maintenance: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()
                val result = service.findPlantsByQuery(query, 1, "sk-sCUk67f500f7c4bc19647")

                val filteredPlants = mutableListOf<Plant>()
                for (plant in result.data) {
                    try {
                        // Verificar si estamos recibiendo correctamente los detalles de la planta
                        val detailedPlant = service.findPlantById(plant.id, "sk-sCUk67f500f7c4bc19647")

                        // Logs para revisar los detalles de la planta
                        Log.d("FILTER_DETAIL", "Planta ${plant.id} encontrada: ${detailedPlant}")

                        // Mostrar detalles de los valores de 'type' y 'maintenance'
                        Log.d("FILTER_DETAIL", "Filtro: Type = $type, Maintenance = $maintenance")
                        Log.d("FILTER_DETAIL", "Planta: Type = ${detailedPlant.type}, Maintenance = ${detailedPlant.maintenance}")

                        val typeMatches = (type == "all" || detailedPlant.type.equals(type, true))
                        val maintenanceMatches = (maintenance == "all" || detailedPlant.maintenance.equals(maintenance, true))


                        // Verificar si los filtros coinciden
                        if (typeMatches && maintenanceMatches) {
                            Log.d("FILTER_DETAIL", "Planta ${plant.id} incluida en el filtro")
                            filteredPlants.add(detailedPlant)
                        } else {
                            Log.d("FILTER_DETAIL", "Planta ${plant.id} no coincide con los filtros")
                        }
                    } catch (e: Exception) {
                        Log.e("FILTER_DETAIL", "Error detalle planta ${plant.id}")
                    }
                }

                // Actualizar la UI con las plantas filtradas

                withContext(Dispatchers.Main) {
                    adapter.updateData(filteredPlants)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error al filtrar plantas", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }*/




}