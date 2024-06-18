package com.example.findtofine.ui.navbar.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findtofine.R
import com.example.findtofine.data.response.ItemsItem
import com.example.findtofine.databinding.ItemCardDetailBinding
import com.example.findtofine.databinding.ItemCardDetailYesBinding

class AdapterChecked (
    private var items: List<ItemsItem>,
    private val listener: OnItemDeleteClickListener
) : RecyclerView.Adapter<AdapterChecked.EditTripItemsViewHolder>() {

    private var checkedCount: Int = 0
    private var missingCount: Int = 0

    inner class EditTripItemsViewHolder(private val binding: ItemCardDetailYesBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ItemsItem) {
            Glide.with(binding.imageView.context)
                .load(item.image)
                .into(binding.imageView)

            binding.textViewTitle.setText(item.name)

            binding.imageViewNonCheck.setOnClickListener {
                binding.cardView.setCardBackgroundColor(binding.root.context.getColor(R.color.merah))
                binding.textViewTitle.setTextColor(binding.root.context.getColor(R.color.white))
                items[adapterPosition].checked = false
                updateCounts()
            }

            binding.imageViewCheck.setOnClickListener {
                binding.cardView.setCardBackgroundColor(binding.root.context.getColor(R.color.hijau2))
                binding.textViewTitle.setTextColor(binding.root.context.getColor(R.color.white))
                items[adapterPosition].checked = true
                updateCounts()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditTripItemsViewHolder {
        val binding = ItemCardDetailYesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    fun getCheckedItemNames(): List<String> {
        return items.filter { it.checked == true }.mapNotNull { it.name }
    }


    private fun updateCounts() {
        checkedCount = items.count { it.checked == true }
        missingCount = items.count { it.checked == false }
        // Panggil metode untuk memperbarui TextView di CheckedItemsActivity
        listener.onItemCountsUpdated(checkedCount, missingCount)
    }
}

