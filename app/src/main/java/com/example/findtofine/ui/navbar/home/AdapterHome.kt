package com.example.findtofine.ui.navbar.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.findtofine.databinding.ItemCardBinding

class AdapterHome(private val items: List<MyItem>): RecyclerView.Adapter<AdapterHome.MyViewHolder>() {

    class MyViewHolder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyItem) {
            binding.imageView.setImageResource(item.imageRes)
            binding.textViewTitle.text = item.title
            binding.textViewSubtitle.text = item.subtitle
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterHome.MyViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterHome.MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

data class MyItem(val title: String, val subtitle: String, val imageRes: Int)