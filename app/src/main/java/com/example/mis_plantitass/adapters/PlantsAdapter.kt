package com.example.mis_plantitass.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mis_plantitass.R
import com.example.mis_plantitass.data.Plant
import com.example.mis_plantitass.databinding.ItemPlantBinding
import com.squareup.picasso.Picasso

class PlantsAdapter (var items: List<Plant>, val onClick: (Int) -> Unit) : Adapter<PlantViewHolder>()   {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        // Infla el layout del item para cada superhéroe.
        val binding = ItemPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)  // Devuelve un ViewHolder que contiene el binding.
    }

    // Devuelve la cantidad de elementos en la lista de superhéroes.
    override fun getItemCount(): Int = items.size

    // Este metodo se llama para vincular los datos del superhéroe con la vista correspondiente en cada fila.
    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        // Obtiene el superhéroe en la posición actual.
        val plant = items[position]

        // Llama al metodo render del ViewHolder para mostrar los datos del superhéroe en la vista.
        holder.render(plant)

        // Establece un listener de clics en el item de la lista. Cuando el usuario hace clic, ejecuta la función `onClick` pasando la posición.
        holder.itemView.setOnClickListener {
            onClick(position)
        }
    }

    fun updateData(newItems: List<Plant>) {
        items = newItems
        notifyDataSetChanged()  // Notifica al adaptador que los datos han cambiado
    }




}

class PlantViewHolder(val binding: ItemPlantBinding) : ViewHolder(binding.root) {
    fun render(plant: Plant) {
        binding.nameTextView.text = plant.common_name // O usa scientific_name si prefieres
        Picasso.get()
            .load(plant.default_image?.regular_url) // Usar la URL regular de la imagen
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.pictureImageView)
    }
}