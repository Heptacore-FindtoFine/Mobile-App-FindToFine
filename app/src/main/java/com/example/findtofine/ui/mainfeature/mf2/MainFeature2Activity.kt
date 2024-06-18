package com.example.findtofine.ui.mainfeature.mf2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.findtofine.R
import com.example.findtofine.data.api.ApiConfig
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.databinding.ActivityMainFeature2Binding
import com.example.findtofine.ui.mainfeature.mf3.MainFeature3Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class MainFeature2Activity : AppCompatActivity() {
    private lateinit var binding: ActivityMainFeature2Binding
    private lateinit var imageAdapter: ImageAdapter
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private var imageUriList: MutableList<Uri> = mutableListOf()

    companion object {
        private const val REQUEST_CAMERA = 1
        private const val REQUEST_GALLERY = 2
        private const val REQUEST_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFeature2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("title")
        val startDate = intent.getStringExtra("startDate")
        val finishDate = intent.getStringExtra("finishDate")
        val location = intent.getStringExtra("location")
        val description = intent.getStringExtra("description")
        val imageUriString = intent.getStringExtra("imageUri")

        selectedImageUri = imageUriString?.let { Uri.parse(it) }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ivAddImage.setOnClickListener {
            showPictureDialog()
        }

        binding.btnNext.setOnClickListener {
            uploadTask(title, startDate, finishDate, location, description)

        }

        setupRecyclerView()
    }

    private fun uploadTask(
        title: String?,
        startDate: String?,
        finishDate: String?,
        location: String?,
        description: String?
    ) {
        if (selectedImageUri != null && title != null && startDate != null && finishDate != null && location != null && description != null) {
            val file = File(selectedImageUri!!.path!!)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageBody = MultipartBody.Part.createFormData("image", file.name, requestFile)

            // Membuat list item untuk contoh
            val items = mutableListOf<MultipartBody.Part>()
            for (uri in imageUriList) {
                val file = File(uri.path!!)
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageBody = MultipartBody.Part.createFormData("items", file.name, requestFile)
                items.add(imageBody)
            }

            val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val startDatePart = startDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val endDatePart = finishDate.toRequestBody("text/plain".toMediaTypeOrNull())
            val locationPart = location.toRequestBody("text/plain".toMediaTypeOrNull())
            val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

            val service = ApiConfig.getApiService()
            val token = SharedPrefManager.getUserData(this)["token"] ?: return

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.uploadTask(
                        token,
                        titlePart,
                        imageBody,
                        startDate,
                        finishDate,
                        locationPart,
                        descriptionPart,
                        items
                    )
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainFeature2Activity, "Upload successful", Toast.LENGTH_SHORT).show()

                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainFeature2Activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Incomplete data or no image selected", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter(imageUriList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = imageAdapter
    }

    private fun showPictureDialog() {
        val pictureDialog = MaterialAlertDialogBuilder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems) { _, which ->
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
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                startActivityForResult(takePictureIntent, REQUEST_CAMERA)
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
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showPictureDialog()
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_GALLERY -> {
                    val selectedImageUri: Uri? = data?.data
                    if (selectedImageUri != null) {
                        imageUriList.add(selectedImageUri)
                        imageAdapter.notifyDataSetChanged()
                    }
                }
                REQUEST_CAMERA -> {
                    currentPhotoPath?.let { path ->
                        val file = File(path)
                        val uri = Uri.fromFile(file)
                        imageUriList.add(uri)
                        imageAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}
