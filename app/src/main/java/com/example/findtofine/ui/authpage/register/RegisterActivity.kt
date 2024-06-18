package com.example.findtofine.ui.authpage.register

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import com.example.findtofine.MainActivity
import com.example.findtofine.R
import com.example.findtofine.data.api.ApiConfig
import com.example.findtofine.databinding.ActivityRegisterBinding
import com.example.findtofine.databinding.NotifCustomLoginBinding
import com.example.findtofine.databinding.NotifCustomRegisterBinding
import com.example.findtofine.ui.authpage.forgotpw.ForgotPwActivity
import com.example.findtofine.ui.authpage.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Registering In")
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)

        auth = FirebaseAuth.getInstance()

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            val conPass = binding.etConPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty() || conPass.isEmpty()){
                Toast.makeText(this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(pass != conPass) {
                Toast.makeText(this, "Password must be same",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            register(email,pass)
        }
    }

    private fun showCustomNotif(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = NotifCustomRegisterBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnTravel.setOnClickListener {
            // Handle button click
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun register(email: String, password: String) {
        progressDialog.show()
        CoroutineScope(Dispatchers.IO).launch {
            val apiService = ApiConfig.getApiService()
            try {
                val response = apiService.register(email, password)
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    if (response.data != null) {
                        // Login success, show notification
                        showCustomNotif()
                    } else {
                        // Show error message
                        Toast.makeText(this@RegisterActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    // Show error message
                    Toast.makeText(this@RegisterActivity, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    progressDialog.dismiss()
                    // Show error message
                    Toast.makeText(this@RegisterActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}