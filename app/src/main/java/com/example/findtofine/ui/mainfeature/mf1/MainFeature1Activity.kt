package com.example.findtofine.ui.mainfeature.mf1

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.findtofine.data.ImageItem
import com.example.findtofine.data.api.ApiConfig
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.databinding.ActivityMainFeature1Binding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.lifecycle.lifecycleScope
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

class MainFeature1Activity : AppCompatActivity() {
    private lateinit var binding: ActivityMainFeature1Binding
    private var currentPhotoPath: String? = null
    private var selectedImageUri: Uri? = null
    private val imageList = mutableListOf<ImageItem>()
    private lateinit var imageAdapter: MainFeatureAdapter
    private var imageSource: ImageSource? = null
    private lateinit var progressDialog: ProgressDialog

    enum class ImageSource {
        UPLOAD_FOTO,
        ADD_ITEMS
    }

    companion object {
        private const val REQUEST_CAMERA = 1
        private const val REQUEST_GALLERY = 2
        private const val REQUEST_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainFeature1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading Data")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        imageAdapter = MainFeatureAdapter(imageList)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainFeature1Activity)
            adapter = imageAdapter
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnNext.setOnClickListener {
            if (currentPhotoPath == null && selectedImageUri == null) {
                Toast.makeText(this@MainFeature1Activity, "Please take a photo or select from gallery first", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    uploadTask()
                }
            }
        }

        binding.ivUploadFoto.setOnClickListener {
            imageSource = ImageSource.UPLOAD_FOTO
            showPictureDialog()
        }

        binding.etStartDate.setOnClickListener {
            showDatePickerDialog(binding.etStartDate)
        }
        binding.etEndDate.setOnClickListener {
            showDatePickerDialog(binding.etEndDate)
        }

        binding.ivAddItems.setOnClickListener {
            imageSource = ImageSource.ADD_ITEMS
            showPictureDialog()
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
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
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
                REQUEST_CAMERA -> {
                    selectedImageUri = null
                    currentPhotoPath?.let { path ->
                        val file = File(path)
                        val uri = Uri.fromFile(file)
                        handleImageResult(uri)
                    }
                }

                REQUEST_GALLERY -> {
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
        // Gunakan selectedImageUri untuk memeriksa sumber gambar
        when (imageSource) {
            ImageSource.UPLOAD_FOTO -> {
                selectedImageUri = uri
                Glide.with(this)
                    .load(uri)
                    .fitCenter()
                    .into(binding.ivUploadFoto)
            }

            ImageSource.ADD_ITEMS -> {
                addImageToList(uri)  // Tetap gunakan uri yang diterima dari parameter
            }

            null -> { /* No action needed */ }
        }
    }


    private fun addImageToList(uri: Uri) {
        val imageName = "Image ${imageList.size + 1}"
        imageList.add(ImageItem(uri, imageName))
        imageAdapter.notifyDataSetChanged()
    }

    private fun getFileFromUri(uri: Uri): File? {
        val path = getPathFromUri(uri)
        return if (path != null) File(path) else null
    }

    private fun getPathFromUri(uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
    }

    @SuppressLint("Recycle")
    private suspend fun uploadTask() {
        progressDialog.show()
        if (currentPhotoPath == null && selectedImageUri == null) {
            Toast.makeText(this@MainFeature1Activity, "Please take a photo or select from gallery first", Toast.LENGTH_SHORT).show()
            return
        }
        // Retrieve user input
        val title = binding.etTitleTrip.text.toString().trim()
        val startDate = binding.etStartDate.text.toString().trim()
        val endDate = binding.etEndDate.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val description = binding.etDeskripsi.text.toString().trim()

        // Check if any field is empty
        if (title.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Retrieve token from SharedPrefManager
        val token = SharedPrefManager.getUserData(this)["token"] ?: return

        // Prepare uploadFotoPart
        val uploadFotoPart = when {
            currentPhotoPath != null -> {
                // Image taken from camera
                val uploadFotoFile = File(currentPhotoPath!!)
                val uploadFotoRequestBody = uploadFotoFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", uploadFotoFile.name, uploadFotoRequestBody)
            }
            selectedImageUri != null -> {
                // Image selected from gallery
                val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                val byteArray = inputStream?.readBytes()
                byteArray?.let {
                    val uploadFotoRequestBody = it.toRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", "upload.jpg", uploadFotoRequestBody)
                } ?: run {
                    Toast.makeText(this@MainFeature1Activity, "Failed to process the selected image", Toast.LENGTH_SHORT).show()
                    return
                }
            }
            else -> {
                // This should ideally not happen if we validated properly above
                Toast.makeText(this@MainFeature1Activity, "Failed to process the selected image", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Convert images from recyclerView to List<MultipartBody.Part>
        val itemsParts = mutableListOf<MultipartBody.Part>()
        for (imageItem in imageList) {
            val imageFile = getFileFromUri(imageItem.imageUri)
            if (imageFile != null) {
                val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("items", imageFile.name, imageRequestBody)
                itemsParts.add(imagePart)
            }
        }

        val titlePart = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val startDatePart = startDate.toRequestBody("text/plain".toMediaTypeOrNull())
        val endDatePart = endDate.toRequestBody("text/plain".toMediaTypeOrNull())
        val locationPart = location.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())

        // Call API to upload task
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService().uploadTask(
                    "Bearer $token",
                    titlePart,
                    uploadFotoPart,
                    startDate,
                    endDate,
                    locationPart,
                    descriptionPart,
                    itemsParts
                )

                // Handle response
                if (response != null) {
                    progressDialog.dismiss()
                    // Task uploaded successfully, do something if needed
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this@MainFeature1Activity, "Failed to upload task", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressDialog.dismiss()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainFeature1Activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}