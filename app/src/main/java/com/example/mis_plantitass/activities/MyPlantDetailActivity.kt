package com.example.mis_plantitass.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mis_plantitass.data.MyPlant
import com.example.mis_plantitass.data.MyPlantDAO
import com.example.mis_plantitass.databinding.ActivityMyPlantDetailBinding
import com.example.mis_plantitass.utils.DatabaseManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MyPlantDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityMyPlantDetailBinding
    lateinit var myplantDAO: MyPlantDAO
    var plantId: Long = -1
    var currentPlant: MyPlant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPlantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myplantDAO = MyPlantDAO(DatabaseManager(this))
        plantId = intent.getLongExtra(MyPlantsActivity.PLANT_ID, -1)



        //si el ID es -1, osea si el ID es valido,continuamos
        if (plantId != -1L) {

            //Buscamos en la base de datos la planta con ese plantId usando un DAO (una clase que accede a la base de datos). El resultado se guarda en currentPlant
            currentPlant = myplantDAO.findById(plantId)



            if (currentPlant == null) {
                Log.e("ERROR", "No se encontró planta con ID: $plantId")
                finish()
                return
            }

            val ultimaFecha = currentPlant?.ultimaFechaRiego ?: 0L
            val ahora = System.currentTimeMillis()
            val dosDiasEnMillis = 2 * 24 * 60 * 60 * 1000L

            // Solo evaluar el estado si hay una fecha válida
            if (ultimaFecha > 0L && (ahora - ultimaFecha > dosDiasEnMillis)) {
                currentPlant?.regada = false
            }

            // Mostrar nombre y estado
            binding.plantNameTextView.text = currentPlant?.common_name
            binding.plantStatusTextView.text =
                if (currentPlant?.regada == true) "Estado: Regada" else "Estado: No regada"

            // Mostrar fecha solo si está marcada como regada
            if (currentPlant?.regada == true && ultimaFecha > 0L) {
                val sdf = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault())
                val fechaFormateada = sdf.format(Date(ultimaFecha))
                binding.lastWateredTextView.text = "Último riego: $fechaFormateada"
            } else {
                binding.lastWateredTextView.text = "Último riego: —"
            }

            binding.plantWateredCheckBox.isChecked = currentPlant?.regada == true
            binding.plagueCheckBox.isChecked = currentPlant?.tienePlagas == true

            // Guardar cambios
            binding.savePlantStatusButton.setOnClickListener {
                updatePlantStatus()
            }

        } else {
            Toast.makeText(this, "No se recibió una planta válida", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updatePlantStatus() {
        currentPlant?.let { plant ->
            // Actualizar el estado de la planta según los checkboxes
            plant.regada = binding.plantWateredCheckBox.isChecked
            plant.tienePlagas = binding.plagueCheckBox.isChecked

            // SOLO actualiza la fecha de riego si el checkbox está marcado como regada
            if (plant.regada) {
                plant.ultimaFechaRiego = System.currentTimeMillis() // Si está regada, actualizamos la fecha
            } else {
                // Si no está regada, dejamos la fecha a 0L o la última fecha válida
                plant.ultimaFechaRiego = 0L
            }

            // Actualizamos la base de datos
            myplantDAO.update(plant)

            // Depurar estado antes de finalizar
            Log.d("DEBUG", "Estado de la planta actualizado. Fecha de riego: ${plant.ultimaFechaRiego}")

            Toast.makeText(this, "Estado de la planta actualizado", Toast.LENGTH_SHORT).show()
            finish() // Vuelves a la actividad anterior
        }
    }
}