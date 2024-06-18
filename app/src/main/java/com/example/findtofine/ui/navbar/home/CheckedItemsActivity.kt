package com.example.findtofine.ui.navbar.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.findtofine.R
import com.example.findtofine.data.response.GetTaskDetailResponse
import com.example.findtofine.data.response.ItemsItem
import com.example.findtofine.databinding.ActivityCheckedItemsBinding

class CheckedItemsActivity : AppCompatActivity(), OnItemDeleteClickListener {
    private lateinit var binding: ActivityCheckedItemsBinding
    private lateinit var adapter: AdapterChecked
    private var taskDetail: GetTaskDetailResponse? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckedItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Review Your Packed Items"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterChecked(emptyList(), this)
        binding.recyclerView.adapter = adapter

        taskDetail = intent.getParcelableExtra("task_detail")
        taskDetail?.let { updateUI(it) }
    }

    private fun updateUI(task: GetTaskDetailResponse) {
        val nonNullItems: List<ItemsItem> = task.items?.filterNotNull() ?: emptyList()
        adapter.updateData(nonNullItems)
    }
    override fun onItemDeleteClick(position: Int) {
        adapter.removeItem(position)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}