package com.example.findtofine.ui.navbar.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findtofine.R
import com.example.findtofine.data.MyItems
import com.example.findtofine.data.api.ApiConfig
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.data.response.GetAllTaskResponse
import com.example.findtofine.databinding.FragmentHomeBinding
import com.example.findtofine.databinding.FragmentProfileBinding
import com.example.findtofine.ui.navbar.profile.editprofile.EditProfileActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: AdapterHome

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        adapter = AdapterHome(emptyList()) { item ->
            val intent = Intent(requireContext(), DetailTripActivity::class.java)
            intent.putExtra("task_id", item.id)
            startActivity(intent)
        }
        binding.recyclerView.adapter = adapter

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Handle swipe actions if needed
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)

        fetchData()
    }

    private fun fetchData() {
        val token = SharedPrefManager.getUserData(requireContext())["token"] ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService().getAllTask(token)
                val items = response.mapNotNull {
                    it?.let { task ->
                        if (task.status == "false") {
                            MyItems(
                                title = task.title ?: "",
                                subtitle = task.location ?: "",
                                image = task.image ?: "",
                                items = task.items ?: 0,
                                id = task.id ?: "",
                                createAt = task.createdAt ?: "",
                                status = task.status ?: ""
                            )
                        } else {
                            null
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    adapter.updateData(items)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Handle the error
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}