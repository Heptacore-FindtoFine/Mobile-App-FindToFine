package com.example.findtofine.ui.navbar.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.findtofine.R
import com.example.findtofine.databinding.FragmentHomeBinding
import com.example.findtofine.databinding.FragmentProfileBinding
import com.example.findtofine.ui.navbar.profile.editprofile.EditProfileActivity

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
        adapter = AdapterHome(getData())
        binding.recyclerView.adapter = adapter

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun getData(): List<MyItem> {
        return listOf(
            MyItem(
                "Hiling tipis-tipis di Bali",
                "20 Februari 2024 - Pantai Kelingking",
                R.drawable.help_icon
            ),
            // Add more items here
        )
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}