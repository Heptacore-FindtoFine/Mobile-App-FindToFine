package com.example.findtofine.ui.navbar.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findtofine.data.MyItems
import com.example.findtofine.databinding.ItemCardBinding

class AdapterHome(
    private var items: List<MyItems>,
    private val itemClickListener: (MyItems) -> Unit
) : RecyclerView.Adapter<AdapterHome.MyViewHolder>() {

    var totalItems: Int = 0
    var completedTrips: Int = 0

    class MyViewHolder(
        private val binding: ItemCardBinding,
        private val itemClickListener: (MyItems) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MyItems) {
            Glide.with(binding.imageView.context)
                .load(item.image)
                .into(binding.imageView)
            binding.textViewTitle.text = item.title
            binding.textViewSubtitle.text = item.subtitle
            binding.textViewBelowImage.text = item.items.toString()
            binding.root.setOnClickListener { itemClickListener(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<MyItems>) {
        items = newItems
        totalItems = items.sumBy { it.items }
        completedTrips = items.count { it.status == "true" }
        notifyDataSetChanged()
    }
}
