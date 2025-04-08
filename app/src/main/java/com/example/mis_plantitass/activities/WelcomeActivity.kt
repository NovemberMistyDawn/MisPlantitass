package com.example.mis_plantitass.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mis_plantitass.R

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)


        // Configurar el botón "Empezar", aqui lo busca
        val btnEmpezar: Button = findViewById(R.id.btnBuscarPlantas)

        //aqui le pone un setonclicklistener
        btnEmpezar.setOnClickListener {
            // Crear un Intent para abrir MainActivity, un intent es la accion de un boton
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)  // Iniciar la actividad MainActivity, como el intent
            //finish()  // Finaliza la WelcomeActivity para que no vuelva cuando presionas "Atrás"
        }

        val btnMisPlantas: Button = findViewById(R.id.btnMisPlantas)
btnMisPlantas.setOnClickListener {
    val intent = Intent(this, MyPlantsActivity::class.java)
    startActivity(intent)
}

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}