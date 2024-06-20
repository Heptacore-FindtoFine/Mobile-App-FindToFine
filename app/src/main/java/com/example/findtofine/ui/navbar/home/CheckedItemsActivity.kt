package com.example.findtofine.ui.navbar.home

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findtofine.MainActivity
import com.example.findtofine.data.UpdateItemsStatus
import com.example.findtofine.data.UpdateTaskRequest
import com.example.findtofine.data.api.ApiConfig
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.data.response.GetTaskDetailResponse
import com.example.findtofine.data.response.ItemsItem
import com.example.findtofine.databinding.ActivityCheckedItemsBinding
import com.example.findtofine.databinding.NotifCustomTripBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CheckedItemsActivity : AppCompatActivity(), OnItemDeleteClickListener {
    private lateinit var binding: ActivityCheckedItemsBinding
    private lateinit var adapter: AdapterChecked
    private var taskDetail: GetTaskDetailResponse? = null
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckedItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Fetching Data")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

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

        updateTotalItemsTextView()

        binding.btnConfirm.setOnClickListener {
            updateTaskEnd()
            updateTaskItems()
        }
    }

    private fun updateTaskEnd(){
        progressDialog.show()

        val updateTaskRequest = UpdateTaskRequest(
            title = taskDetail?.title ?: "",
            startDate = taskDetail?.startDate ?: "",
            finishDate = taskDetail?.finishDate ?: "",
            location = taskDetail?.location ?: "",
            description = taskDetail?.description ?: "",
            items = adapter.getItems().map { it.name ?: "" },
            status = true
        )

        val taskId = taskDetail?.id ?: return
        val token = SharedPrefManager.getUserData(this)["token"] ?: return

        lifecycleScope.launch {
            try {
                val apiServices = ApiConfig.getApiService()
                val updatedTaskEnd = apiServices.updateTask(
                    token = token,
                    id = taskId,
                    updateTaskRequest = updateTaskRequest
                )
                progressDialog.dismiss()
                Toast.makeText(this@CheckedItemsActivity, "Task updated successfully", Toast.LENGTH_SHORT).show()
                showCustomNotif()
            } catch (e: Exception){
                progressDialog.dismiss()
                Toast.makeText(this@CheckedItemsActivity, "Failed to update task", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateTaskItems() {
        progressDialog.show()
        val checkedItemNames = adapter.getCheckedItemNames()
        val taskId = taskDetail?.id ?: return

        val token = SharedPrefManager.getUserData(this)["token"] ?: return
        val service = ApiConfig.getApiService()


        val updateItemsStatus = UpdateItemsStatus(
            name = checkedItemNames
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.updateTaskItems(
                    token = token,
                    id = taskId,
                    updateItemsStatus = updateItemsStatus
                )
                progressDialog.dismiss()
                showCustomNotif()
            } catch (e: Exception) {
                progressDialog.dismiss()
                e.printStackTrace() // Handle error
            }
        }
    }

    private fun showCustomNotif() {
        runOnUiThread {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            val dialogBinding = NotifCustomTripBinding.inflate(layoutInflater)
            dialog.setContentView(dialogBinding.root)

            dialogBinding.btnTravel.setOnClickListener {
                // Handle button click
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
                dialog.dismiss()
            }

            dialog.show()
        }
    }



    private fun updateUI(task: GetTaskDetailResponse) {
        val nonNullItems: List<ItemsItem> = task.items?.filterNotNull() ?: emptyList()
        adapter.updateData(nonNullItems)
    }

    private fun updateTotalItemsTextView() {
        val totalItems = adapter.itemCount
        binding.tvTotalItems.text = "Total items: $totalItems"
    }

    override fun onItemCountsUpdated(checkedCount: Int, missingCount: Int) {
        binding.tvItemPresent.text = "Item Present: $checkedCount"
        binding.tvItemMissing.text = "Item Missing: $missingCount"
    }

    override fun onItemDeleteClick(position: Int) {
        adapter.removeItem(position)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}