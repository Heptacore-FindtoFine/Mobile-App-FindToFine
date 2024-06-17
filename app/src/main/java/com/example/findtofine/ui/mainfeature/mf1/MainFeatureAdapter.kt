package com.example.findtofine.ui.mainfeature.mf1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.findtofine.R
import com.example.findtofine.data.ImageItem

class MainFeatureAdapter(private val imageList: List<ImageItem>) :
    RecyclerView.Adapter<MainFeatureAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card_detail_no, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageItem = imageList[position]
        holder.textViewTitle.text = imageItem.imageName
        Glide.with(holder.itemView.context).load(imageItem.imageUri).into(holder.imageView)
    }

    override fun getItemCount() = imageList.size
}