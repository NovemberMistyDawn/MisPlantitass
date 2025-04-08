package com.example.mis_plantitass.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mis_plantitass.R
import com.example.mis_plantitass.data.MyPlant
import com.example.mis_plantitass.data.MyPlantDAO
import com.example.mis_plantitass.databinding.ActivityMyPlantsBinding

class MyPlantsActivity : AppCompatActivity() {

    //esto nos importa el objeto de plantas
    lateinit var myplantsList: List<MyPlant>
    lateinit var binding: ActivityMyPlantsBinding
    lateinit var myplantDAO: MyPlantDAO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMyPlantsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}