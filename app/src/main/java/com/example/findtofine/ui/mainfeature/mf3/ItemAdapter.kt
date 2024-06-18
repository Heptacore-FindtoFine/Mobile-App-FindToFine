package com.example.findtofine.ui.mainfeature.mf3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.findtofine.databinding.ItemCardDetailBinding
import com.example.findtofine.databinding.ItemCardDetailNoBinding

class ItemAdapter(private val items: List<String>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCardDetailNoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textViewTitle.text = item
    }

    override fun getItemCount(): Int = items.size

    inner class ItemViewHolder(val binding: ItemCardDetailNoBinding) :
        RecyclerView.ViewHolder(binding.root)
}
