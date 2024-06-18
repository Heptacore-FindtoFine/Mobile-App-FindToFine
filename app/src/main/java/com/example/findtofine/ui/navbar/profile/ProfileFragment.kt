package com.example.findtofine.ui.navbar.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.findtofine.R
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.databinding.FragmentHomeBinding
import com.example.findtofine.databinding.FragmentProfileBinding
import com.example.findtofine.ui.authpage.login.LoginActivity
import com.example.findtofine.ui.navbar.profile.aboutapp.AboutActivity
import com.example.findtofine.ui.navbar.profile.editprofile.EditProfileActivity
import com.example.findtofine.ui.navbar.profile.privacypolicy.PrivacyPolicyActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set onClickListener here
        binding.llMyAccount.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }

        binding.llAbout.setOnClickListener{
            val intent = Intent(activity, AboutActivity::class.java)
            startActivity(intent)
        }

        binding.llPrivacy.setOnClickListener {
            val intent = Intent(activity, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        binding.llLogOut.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        loadProfileData()
        if (!checkPermissions()) {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        val storagePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return storagePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            REQUEST_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    // Reload profile data here
                    loadProfileData()
                } else {
                    // Permission denied
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


    private fun loadProfileData() {
        val userData = SharedPrefManager.getUserData(requireContext())
        val firstName = userData["first_name"]
        val lastName = userData["last_name"]
        binding.tvEmail.text = userData["email"]


        binding.tvName.text = "$firstName $lastName"

        val profilePicture = userData["profile_picture"]
        if (!profilePicture.isNullOrEmpty()) {
            // Gunakan Glide untuk memuat gambar dari URI ke CircleImageView
            Glide.with(this)
                .load(profilePicture)
                .fitCenter()
                .into(binding.ivProfile)
        }
    }

    private fun showLogoutConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setNegativeButton("Cancel") { dialog, which ->
                // Respond to negative button press
                dialog.dismiss()
            }
            .setPositiveButton("Logout") { dialog, which ->
                // Respond to positive button press
                logout()
            }
            .show()
    }

    private fun logout(){
        SharedPrefManager.clearUserData(requireActivity())
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_PERMISSION = 100
    }
}