package com.example.mis_plantitass.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mis_plantitass.R

class WelcomeActivity : AppCompatActivity() {

    lateinit var txtBienvenida: TextView
    lateinit var editTextNombre: EditText
    lateinit var btnGuardarNombre: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_welcome)


        val contentLayout = findViewById<View>(R.id.contentLayout)
        val anim = AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up)
        contentLayout.startAnimation(anim)

        // Referencias
        txtBienvenida = findViewById(R.id.textBienvenida) // tu TextView de bienvenida
        editTextNombre = findViewById(R.id.editTextNombre)
        btnGuardarNombre = findViewById(R.id.btnGuardarNombre)

        val sharedPref = getSharedPreferences("MisPlantitasPrefs", MODE_PRIVATE)
        val nombreUsuario = sharedPref.getString("nombre_usuario", null)

        if (nombreUsuario != null) {
            // Si ya hay nombre, muestra saludo
            txtBienvenida.text = "¡Bienvenido/a, $nombreUsuario!"
        } else {
            // Si no hay nombre, muestra campos para ingresarlo
            editTextNombre.visibility = View.VISIBLE
            btnGuardarNombre.visibility = View.VISIBLE

            btnGuardarNombre.setOnClickListener {
                val nombreIngresado = editTextNombre.text.toString().trim()
                if (nombreIngresado.isNotEmpty()) {
                    sharedPref.edit().putString("nombre_usuario", nombreIngresado).apply()
                    Toast.makeText(this, "¡Hola, $nombreIngresado!", Toast.LENGTH_SHORT).show()
                    recreate() // Reinicia la actividad para actualizar el texto
                } else {
                    Toast.makeText(this, "Por favor, escribe tu nombre", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Botones de navegación
        val btnEmpezar: Button = findViewById(R.id.btnBuscarPlantas)
        btnEmpezar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val btnMisPlantas: Button = findViewById(R.id.btnMisPlantas)
        btnMisPlantas.setOnClickListener {
            startActivity(Intent(this, MyPlantsActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}