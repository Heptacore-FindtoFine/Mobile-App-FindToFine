package com.example.findtofine.ui.mainfeature.mf2

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findtofine.databinding.ItemCardDetailBinding


class ImageAdapter(private val imageUriList: MutableList<Uri>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemCardDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUri = imageUriList[position]
        Glide.with(holder.itemView.context)
            .load(imageUri)
            .into(holder.binding.imageView)

        holder.binding.imageViewDelete.setOnClickListener {
            removeItem(position)
        }

        holder.binding.textViewTitle.text = "Image ${position + 1}"
    }

    override fun getItemCount(): Int = imageUriList.size

    inner class ImageViewHolder(val binding: ItemCardDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun removeItem(position: Int) {
        if (position >= 0 && position < imageUriList.size) {
            imageUriList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, imageUriList.size)
        }
    }
}
