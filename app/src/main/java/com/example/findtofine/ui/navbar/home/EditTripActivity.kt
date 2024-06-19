package com.example.findtofine.ui.navbar.home

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.findtofine.MainActivity
import com.example.findtofine.data.api.ApiConfig
import com.example.findtofine.data.UpdateTaskRequest
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.data.response.GetTaskDetailResponse
import com.example.findtofine.data.response.ItemsItem
import com.example.findtofine.databinding.ActivityEditTripBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditTripActivity : AppCompatActivity(), OnItemDeleteClickListener {
    private lateinit var binding: ActivityEditTripBinding
    private lateinit var adapter: AdapterEditTrip
    private var taskDetail: GetTaskDetailResponse? = null
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Updating Data")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.title = "Edit Trip"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AdapterEditTrip(emptyList(),this)
        binding.recyclerView.adapter = adapter


        taskDetail = intent.getParcelableExtra("task_detail")
        taskDetail?.let { updateUI(it) }

        binding.etStartDate.setOnClickListener{
            showDatePickerDialog(binding.etStartDate)
        }
        binding.etEndDate.setOnClickListener {
            showDatePickerDialog(binding.etEndDate)
        }

        binding.btnSave.setOnClickListener {
            saveEditData()
        }

        binding.btnDelete.setOnClickListener {
            deleteTask()
        }

        updateTotalItemsTextView()
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateEditText(editText, calendar)
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateEditText(editText: EditText, calendar: Calendar) {
        val format = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(format, Locale.US)
        editText.setText(sdf.format(calendar.time))
    }

    private fun updateUI(task: GetTaskDetailResponse) {
        binding.etTitleTrip.setText(task.title)
        binding.etStartDate.setText(task.startDate)
        binding.etEndDate.setText(task.finishDate)
        binding.etDeskripsi.setText(task.description)
        binding.etLocation.setText(task.location)

        Glide.with(this)
            .load(task.image)
            .into(binding.ivGambar)

        val nonNullItems: List<ItemsItem> = task.items?.filterNotNull() ?: emptyList()
        adapter.updateData(nonNullItems)
    }

    private fun saveEditData() {
        progressDialog.show()
        val title = binding.etTitleTrip.text.toString()
        val description = binding.etDeskripsi.text.toString()
        val location = binding.etLocation.text.toString()
        val items = adapter.getItems().map { it.name ?: "" }

        val startDate = binding.etStartDate.text.toString()
        val finishDate = binding.etEndDate.text.toString()


        val token = SharedPrefManager.getUserData(this)["token"] ?: return
        val taskId = taskDetail?.id ?: return

        val updateTaskRequest = UpdateTaskRequest(
            title = title,
            startDate = startDate,
            finishDate = finishDate,
            location = location,
            description = description,
            items = items,
            status = false
        )

        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val updatedTask = apiService.updateTask(
                    token = token,
                    id = taskId,
                    updateTaskRequest = updateTaskRequest
                )
                Toast.makeText(this@EditTripActivity, "Task updated successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@EditTripActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Close the activity
                progressDialog.dismiss()
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(this@EditTripActivity, "Failed to update task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteTask() {
        progressDialog.show()
        val token = SharedPrefManager.getUserData(this)["token"] ?: return
        val taskId = taskDetail?.id ?: return

        lifecycleScope.launch {
            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.deleteItems("Bearer $token", taskId)
                progressDialog.dismiss()
                if (response.message != null) {
                    Toast.makeText(this@EditTripActivity, "Task deleted successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@EditTripActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close the activity
                } else {
                    Toast.makeText(this@EditTripActivity, "Failed to delete task", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(this@EditTripActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotalItemsTextView() {
        val totalItems = adapter.itemCount
        binding.tvTotal.text = "Total items: $totalItems"
    }

    override fun onItemDeleteClick(position: Int) {
        adapter.removeItem(position)
    }

    override fun onItemCountsUpdated(checkedCount: Int, missingCount: Int) {

    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        return true
    }
}