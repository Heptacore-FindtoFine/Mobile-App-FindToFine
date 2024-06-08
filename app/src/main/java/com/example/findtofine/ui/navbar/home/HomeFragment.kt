package com.example.findtofine.ui.navbar.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.findtofine.R
import com.example.findtofine.databinding.FragmentHomeBinding
import com.example.findtofine.databinding.FragmentProfileBinding
import com.example.findtofine.ui.navbar.profile.editprofile.EditProfileActivity

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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

        // Set onClickListener here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}