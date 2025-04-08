package com.example.mis_plantitass.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.mis_plantitass.data.MyPlant
import com.example.mis_plantitass.databinding.ItemMyplantBinding

class myplantsAdapter(
    var items: List<MyPlant>,
    val onClick: (Int) -> Unit,
    val onDelete: (Int) -> Unit
) : Adapter<MyPlantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPlantViewHolder {
        val binding = ItemMyplantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPlantViewHolder(binding)
    }


    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MyPlantViewHolder, position: Int) {
        val myplant = items[position]
        holder.render(myplant)
        holder.itemView.setOnClickListener {
            onClick(position)
        }
        holder.binding.deleteButton.setOnClickListener {
            onDelete(position)
        }
    }

    fun updateItems(items: List<MyPlant>) {
        this.items = items
        notifyDataSetChanged()
    }
}

class MyPlantViewHolder(val binding: ItemMyplantBinding) : ViewHolder(binding.root) {

    fun render(myplant: MyPlant) {
        binding.titleTextView.text = myplant.common_name
    }
}