package com.example.findtofine.ui.navbar.profile.editprofile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.databinding.ActivityEditProfileBinding
import com.example.findtofine.ui.mainfeature.mf1.MainFeature1Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfileData()

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnUpdateProfile.setOnClickListener {
            saveProfileData()
            finish()
        }

        binding.ivProfile.setOnClickListener {
            showPictureDialog()
        }

        binding.etDateofBirth.setOnClickListener {
            showDatePickerDialog(binding.etDateofBirth)
        }
    }

    private fun loadProfileData() {
        val userData = SharedPrefManager.getUserData(this)
        val firstName = userData["first_name"]
        val lastName = userData["last_name"]
        binding.tvEmail.text = userData["email"]

        binding.etFirstName.setText(firstName)
        binding.etLastName.setText(lastName)
        binding.etDateofBirth.setText(userData["date_of_birth"])
        binding.etEmail.setText(userData["email"])

        binding.etEmail.isEnabled = false

        binding.tvNama.text = "$firstName $lastName"

        val profilePicture = userData["profile_picture"]
        if (!profilePicture.isNullOrEmpty()) {
            // Gunakan Glide untuk memuat gambar dari URI ke CircleImageView
            Glide.with(this)
                .load(profilePicture)
                .fitCenter()
                .into(binding.ivProfile)
        }
    }

    private fun showDatePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
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

    private fun showPictureDialog() {
        val pictureDialog = MaterialAlertDialogBuilder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> {
                    if (checkPermissions()) {
                        openGallery()
                    } else {
                        requestPermissions()
                    }
                }

                1 -> {
                    if (checkPermissions()) {
                        openCamera()
                    } else {
                        requestPermissions()
                    }
                }
            }
        }
        pictureDialog.show()
    }

    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val storagePermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ),
            REQUEST_PERMISSION
        )
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Toast.makeText(this, "Error while creating file", Toast.LENGTH_SHORT).show()
                null
            }
            if (photoFile != null) {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.fileprovider",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, EditProfileActivity.REQUEST_CAMERA)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, EditProfileActivity.REQUEST_GALLERY)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            EditProfileActivity.REQUEST_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Permission granted
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                EditProfileActivity.REQUEST_CAMERA -> {
                    selectedImageUri = null
                    currentPhotoPath?.let { path ->
                        val file = File(path)
                        val uri = Uri.fromFile(file)
                        handleImageResult(uri)
                    }
                }

                EditProfileActivity.REQUEST_GALLERY -> {
                    currentPhotoPath = null
                    selectedImageUri = data?.data
                    selectedImageUri?.let {
                        handleImageResult(it)
                    }
                }
            }
        }
    }

    private fun handleImageResult(uri: Uri) {
        selectedImageUri = uri
        Glide.with(this)
            .load(uri)
            .fitCenter()
            .into(binding.ivProfile)
    }

    private fun saveProfileData() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val dateOfBirth = binding.etDateofBirth.text.toString()
        val profilePicture = when {
            selectedImageUri != null -> selectedImageUri.toString() // Dari galeri
            currentPhotoPath != null -> currentPhotoPath // Dari kamera
            else -> "" // Jika tidak ada gambar yang dipilih
        }

        if (profilePicture != null) {
            SharedPrefManager.saveProfileData(this, firstName, lastName, dateOfBirth, profilePicture)
        }
    }

    companion object {
        private const val REQUEST_CAMERA = 1
        private const val REQUEST_GALLERY = 2
        private const val REQUEST_PERMISSION = 100
    }
}