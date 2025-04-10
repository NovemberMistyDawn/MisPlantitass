package com.example.mis_plantitass.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mis_plantitass.data.MyPlant
import com.example.mis_plantitass.data.MyPlantDAO
import com.example.mis_plantitass.databinding.ItemMyPlantDetailBinding
import com.example.mis_plantitass.utils.DatabaseManager

class MyPlantDetailActivity : AppCompatActivity() {

    lateinit var binding: ItemMyPlantDetailBinding
    lateinit var myplantDAO: MyPlantDAO
    var plantId: Long = -1 // ID de la planta
    lateinit var currentPlant: MyPlant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ItemMyPlantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa el DAO
        myplantDAO = MyPlantDAO(DatabaseManager(this))

        // Obtén el ID de la planta desde el Intent
        plantId = intent.getLongExtra(MyPlantsActivity.PLANT_ID, -1)
        Log.d("DEBUG", "plantId: $plantId")

        if (plantId != -1L) {
            Log.d("DEBUG", "Recibido plantId: $plantId") // 👈 Aquí el log

            // Obtén la planta desde la base de datos
            currentPlant = myplantDAO.findById(plantId) ?: run {
                Log.e("ERROR", "No se encontró planta con ID: $plantId")
                finish()
                return
            }

            // Configura la interfaz con los datos de la planta
            binding.plantNameTextView.text = currentPlant.common_name
            binding.plantStatusTextView.text = if (currentPlant.regada) "Estado: Regada" else "Estado: No regada"
            binding.plantWateredCheckBox.isChecked = currentPlant.regada

            // Configura el evento de guardar cambios
            binding.savePlantStatusButton.setOnClickListener {
                updatePlantStatus()
            }
        }
    }

    // Actualiza el estado de la planta
    private fun updatePlantStatus() {
        // Cambia el estado de la planta (si está regada)
        currentPlant.regada = binding.plantWateredCheckBox.isChecked

        // Actualiza la planta en la base de datos
        myplantDAO.update(currentPlant)

        // Muestra un mensaje de éxito y vuelve atrás
        Toast.makeText(this, "Estado de la planta actualizado", Toast.LENGTH_SHORT).show()
        finish()
    }
}