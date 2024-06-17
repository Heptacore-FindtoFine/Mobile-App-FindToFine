package com.example.findtofine.ui.navbar.history

import android.R
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findtofine.data.MyItems
import com.example.findtofine.data.api.ApiConfig
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.data.response.GetTaskDetailResponse
import com.example.findtofine.databinding.FragmentHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdapterHIstory

    private lateinit var originalItems: List<MyItems>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter = AdapterHIstory(emptyList()) { item ->
            val intent = Intent(requireContext(), DetailHistoryActivity::class.java)
            intent.putExtra("task_id", item.id)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter

        // Setup SearchView
        setupSearchView()

        // Setup Spinner
        setupSpinner()

        fetchData()
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchItems(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchItems(it) }
                return true
            }
        })
    }

    private fun fetchData() {
        val token = SharedPrefManager.getUserData(requireContext())["token"] ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService().getAllTask(token)
                val items = response.mapNotNull {
                    it?.let { task ->
                        MyItems(
                            title = task.title ?: "",
                            subtitle = task.location ?: "",
                            image = task.image ?: "",
                            items = task.items ?: 0,
                            id = task.id ?: "",
                            createAt = task.createdAt ?: ""
                        )
                    }
                }
                originalItems = items
                withContext(Dispatchers.Main) {
                    adapter.updateData(items)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Handle the error
            }
        }
    }

    private fun setupSpinner() {
        val spinnerItems = arrayOf("None", "Newest Date", "Oldest Date")
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, spinnerItems)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    1 -> sortItemsByDate(true) // Newest Date
                    2 -> sortItemsByDate(false) // Oldest Date
                    else -> fetchData() // None
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun sortItemsByDate(isNewest: Boolean) {
        val sortedList = if (isNewest) {
            adapter.items.sortedByDescending {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(it.createAt)
            }
        } else {
            adapter.items.sortedBy {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(it.createAt)
            }
        }
        adapter.updateData(sortedList)
    }

    private fun searchItems(query: String) {
        val filteredList = if (query.isNotBlank()) {
            adapter.items.filter {
                it.title.contains(query, ignoreCase = true)
            }
        } else {
            originalItems
        }
        adapter.updateData(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}