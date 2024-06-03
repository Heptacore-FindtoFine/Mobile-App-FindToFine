package com.example.findtofine.ui.welcomepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class WelcomeViewPager(private val layouts: List<Int>): RecyclerView.Adapter<WelcomeViewPager.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WelcomeViewPager.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: WelcomeViewPager.ViewHolder, position: Int) {}

    override fun getItemCount(): Int {
        return layouts.size
    }

    override fun getItemViewType(position: Int): Int {
        return layouts[position]
    }

}