package com.example.findtofine.ui.navbar.history

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TableLayout
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.findtofine.R
import com.example.findtofine.data.api.ApiConfig
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.data.response.GetTaskDetailResponse
import com.example.findtofine.data.response.ItemsItem
import com.example.findtofine.databinding.ActivityDetailHistoryBinding
import com.example.findtofine.databinding.ItemTableRowBinding
import com.example.findtofine.ui.navbar.home.AdapterDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailHistoryBinding
    private lateinit var adapter: AdapterHistoryDetail
    private var taskDetail: GetTaskDetailResponse? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Detail Trip"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        val taskId = intent.getStringExtra("task_id") ?: return
        fetchTaskDetail(taskId)
    }

    private fun fetchTaskDetail(taskId: String) {
        val token = SharedPrefManager.getUserData(this)["token"] ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService().getDetailTask(token, taskId)
                withContext(Dispatchers.Main) {
                    taskDetail = response
                    updateUI(response)

                    val startDate = taskDetail!!.startDate ?: ""
                    val finishDate = taskDetail!!.finishDate ?: ""

                    val duaration = getDuration(startDate,finishDate)

                    binding.tvDuration.text = "Duration : $duaration Days"
                }
            } catch (e: Exception) {
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
            .into(binding.ivGambar)

        val nonNullItems: List<ItemsItem> = task.items?.filterNotNull() ?: emptyList()
        populateTable(nonNullItems)
    }

    private fun populateTable(items: List<ItemsItem>) {
        val inflater = LayoutInflater.from(this)
        val tableLayout: TableLayout = binding.tableLayout

        for ((index, item) in items.withIndex()) {
            val rowBinding = ItemTableRowBinding.inflate(inflater)
            rowBinding.tvNumber.text = (index + 1).toString()
            rowBinding.tvName.text = item.name

            if (item.checked == true) {
                rowBinding.ivAvailability.setImageResource(R.drawable.centang_icon) // Use your check image
            } else {
                rowBinding.ivAvailability.setImageResource(R.drawable.close_icon) // Use your cancel image
            }

            tableLayout.addView(rowBinding.root)
        }
    }

    private fun getDuration(startDate: String, finishDate: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDateObj = sdf.parse(startDate)
        val finishDateObj = sdf.parse(finishDate)

        // Calculate the difference in milliseconds
        val diffInMillis = finishDateObj.time - startDateObj.time

        // Convert milliseconds to days
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}