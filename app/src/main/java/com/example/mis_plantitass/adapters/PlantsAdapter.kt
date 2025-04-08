package com.example.mis_plantitass.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
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
        val superhero = items[position]

        // Llama al metodo render del ViewHolder para mostrar los datos del superhéroe en la vista.
        holder.render(superhero)

        // Establece un listener de clics en el item de la lista. Cuando el usuario hace clic, ejecuta la función `onClick` pasando la posición.
        holder.itemView.setOnClickListener {
            onClick(position)
        }
    }


}

class PlantViewHolder(val binding: ItemPlantBinding) : ViewHolder(binding.root) {
    fun render(plant: Plant) {
        // Establece el nombre del superhéroe en el TextView correspondiente.
        binding.nameTextView.text = plant.scientific_name
        Picasso.get().load(plant.default_image?.url).into(binding.pictureImageView)
    }
}