package com.example.mis_plantitass.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mis_plantitass.data.MyPlant
import com.example.mis_plantitass.data.MyPlantDAO
import com.example.mis_plantitass.databinding.ActivityAddPlantBinding
import com.example.mis_plantitass.utils.DatabaseManager

class AddPlantActivity  : AppCompatActivity() {

    lateinit var binding: ActivityAddPlantBinding
    lateinit var myplantDAO: MyPlantDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddPlantBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myplantDAO = MyPlantDAO(DatabaseManager(this))

        // Configurar el botón de guardar
        binding.savePlantButton.setOnClickListener {
            val plantName = binding.editTextPlantName.text.toString()

            if (plantName.isNotEmpty()) {
                // Creamos un objeto MyPlant con el nombre ingresado
                val newPlant = MyPlant(id = 0, common_name = plantName)

                // Insertamos la planta en la base de datos
                myplantDAO.insert(newPlant)

                // Volvemos a la actividad principal
                Toast.makeText(this, "Planta añadida", Toast.LENGTH_SHORT).show()
                finish()  // Cierra esta actividad
            } else {
                // Si el campo está vacío, mostramos un mensaje
                Toast.makeText(this, "Por favor, ingresa un nombre para la planta", Toast.LENGTH_SHORT).show()
            }
        }
    }
}