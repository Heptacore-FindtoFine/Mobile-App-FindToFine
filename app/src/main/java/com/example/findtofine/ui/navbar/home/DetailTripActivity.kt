package com.example.findtofine.ui.navbar.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.findtofine.R
import com.example.findtofine.data.api.ApiConfig
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.data.response.GetTaskDetailResponse
import com.example.findtofine.data.response.ItemsItem
import com.example.findtofine.databinding.ActivityDetailTripBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailTripActivity : AppCompatActivity(), AdapterDetail.OnDataChangeListener {
    private lateinit var binding: ActivityDetailTripBinding
    private lateinit var adapter: AdapterDetail
    private var taskDetail: GetTaskDetailResponse? =null
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Gathering Data")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Detail Trip"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        adapter = AdapterDetail(emptyList(),this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        val taskId = intent.getStringExtra("task_id") ?: return
        fetchTaskDetail(taskId)

        binding.btnEditTrip.setOnClickListener {
            taskDetail?.let {
                Log.d("DetailTripActivity", "Editing trip: $it")  // Add log for debugging
                val intent = Intent(this, EditTripActivity::class.java)
                intent.putExtra("task_detail", it)
                startActivity(intent)
                finish()
            } ?: run {
                Log.e("DetailTripActivity", "taskDetail is null")
            }
        }

        binding.btnEndtrip.setOnClickListener {
            taskDetail?.let {
                Log.d("DetailTripActivity", "End trip: $it")  // Add log for debugging
                val intent = Intent(this, CheckedItemsActivity::class.java)
                intent.putExtra("task_detail", it)
                startActivity(intent)
                finish()
            } ?: run {
                Log.e("DetailTripActivity", "taskDetail is null")
            }
        }
    }

    private fun fetchTaskDetail(taskId: String) {
        progressDialog.show()
        val token = SharedPrefManager.getUserData(this)["token"] ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService().getDetailTask(token, taskId)
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    taskDetail = response
                    updateUI(response)
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                e.printStackTrace() // Handle the error
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(task: GetTaskDetailResponse) {
        binding.tvDate.text = "Data : ${task.createdAt}"
        binding.tvDeskripsi.text = "Description : ${task.description}"
        binding.tvLocation.text = "Location : ${task.location}"
        // Load image using Glide
        Glide.with(this)
            .load(task.image)
            .into(binding.ivGambarTitle)

        val nonNullItems: List<ItemsItem> = task.items?.filterNotNull() ?: emptyList()
        adapter.updateData(nonNullItems)
    }

    private fun updateTotalItemsTextView() {
        val totalItems = adapter.itemCount
        binding.tvTotal.text = "Total items: $totalItems"
    }

    override fun onDataChanged() {
        updateTotalItemsTextView()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}