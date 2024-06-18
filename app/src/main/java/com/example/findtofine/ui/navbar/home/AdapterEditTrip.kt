package com.example.findtofine.ui.navbar.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findtofine.R
import com.example.findtofine.data.response.ItemsItem
import com.example.findtofine.databinding.ItemCardDetailBinding

class AdapterEditTrip(
    private var items: List<ItemsItem>,
    private val listener: OnItemDeleteClickListener
) : RecyclerView.Adapter<AdapterEditTrip.EditTripItemsViewHolder>() {

    inner class EditTripItemsViewHolder(private val binding: ItemCardDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemsItem) {
            Glide.with(binding.imageView.context)
                .load(item.image)
                .into(binding.imageView)

            binding.textViewTitle.setText(item.name)

            binding.imageViewDelete.setOnClickListener {
                listener.onItemDeleteClick(adapterPosition)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditTripItemsViewHolder {
        val binding = ItemCardDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EditTripItemsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EditTripItemsViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<ItemsItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getItems(): List<ItemsItem> {
        return items
    }

    fun removeItem(position: Int) {
        items = items.toMutableList().apply { removeAt(position) }
        notifyItemRemoved(position)
    }
}

interface OnItemDeleteClickListener {
    fun onItemDeleteClick(position: Int)
    fun onItemCountsUpdated(checkedCount: Int, missingCount: Int)
}
