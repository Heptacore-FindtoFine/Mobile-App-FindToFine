package com.example.findtofine.ui.navbar.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findtofine.data.response.ItemsItem
import com.example.findtofine.databinding.ItemCardDetailNoBinding

class AdapterDetail(private var items: List<ItemsItem>, private val listener: OnDataChangeListener) : RecyclerView.Adapter<AdapterDetail.ItemViewHolder>() {

    interface OnDataChangeListener {
        fun onDataChanged()
    }

    class ItemViewHolder(private val binding: ItemCardDetailNoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemsItem) {
            Glide.with(binding.imageView.context)
                .load(item.image) // if you have an image URL field
                .into(binding.imageView)

            binding.textViewTitle.setText(item.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCardDetailNoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<ItemsItem>) {
        items = newItems
        notifyDataSetChanged()
        listener.onDataChanged()
    }
}