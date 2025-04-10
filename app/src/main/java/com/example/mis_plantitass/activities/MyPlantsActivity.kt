package com.example.mis_plantitass.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mis_plantitass.R
import com.example.mis_plantitass.adapters.myplantsAdapter
import com.example.mis_plantitass.data.MyPlant
import com.example.mis_plantitass.data.MyPlantDAO
import com.example.mis_plantitass.databinding.ActivityMyPlantsBinding
import com.example.mis_plantitass.utils.DatabaseManager

class MyPlantsActivity : AppCompatActivity() {

    //esto nos importa el objeto de plantas
    lateinit var myplantsList: List<MyPlant>
    lateinit var binding: ActivityMyPlantsBinding
    lateinit var myplantDAO: MyPlantDAO


    lateinit var adapter: myplantsAdapter

    companion object {
        const val PLANT_ID = "PLANT_ID"
    }


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


       myplantDAO = MyPlantDAO(DatabaseManager(this))


        adapter = myplantsAdapter(emptyList(), ::editPlant, ::deletePlant)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)



        binding.addPlantButton.setOnClickListener {
            // Abre la actividad de añadir planta
            val intent = Intent(this, AddPlantActivity::class.java)
            startActivity(intent)
        }

        refreshData()

    }


    override fun onResume() {
        super.onResume()

        refreshData()
    }

    fun refreshData() {
        myplantsList = myplantDAO.findAll()
        adapter.updateItems(myplantsList)
    }

    fun editPlant(position: Int) {
        val task = myplantsList[position]

        val intent = Intent(this, MyPlantsActivity::class.java)
        intent.putExtra(MyPlantsActivity.PLANT_ID, task.id)
        startActivity(intent)
    }

    fun deletePlant(position: Int) {
        val task = myplantsList[position]

        AlertDialog.Builder(this)
            .setTitle("Delete task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                myplantDAO.delete(task)
                refreshData()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .setCancelable(false)
            .show()
    }
}

private fun MyPlantDAO.delete(value: Any) {}
