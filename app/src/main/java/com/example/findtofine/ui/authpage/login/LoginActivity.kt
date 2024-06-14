package com.example.findtofine.ui.authpage.login

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.CheckBox
import android.widget.Toast
import com.example.findtofine.MainActivity
import com.example.findtofine.R
import com.example.findtofine.data.api.ApiConfig
import com.example.findtofine.data.pref.SharedPrefManager
import com.example.findtofine.databinding.ActivityLoginBinding
import com.example.findtofine.databinding.NotifCustomLoginBinding
import com.example.findtofine.ui.authpage.forgotpw.ForgotPwActivity
import com.example.findtofine.ui.authpage.register.RegisterActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignIn: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignIn = GoogleSignIn.getClient(this, gso)

        val checkBox: CheckBox = binding.cbRememberMe
        val isChecked: Boolean = checkBox.isChecked

        binding.tvSignUp.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPwActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            signIn(email,pass)
        }

        binding.btnGoogle.setOnClickListener {
            signInGoogle()
        }
    }

    private fun showCustomNotif(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogBinding = NotifCustomLoginBinding.inflate(layoutInflater)
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

    private fun signIn(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val apiService = ApiConfig.getApiService()
            try {
                val response = apiService.login(email, password)
                withContext(Dispatchers.Main) {
                    if (response.data != null) {
                        // Login success, show notification
                        SharedPrefManager.saveUserData(
                            this@LoginActivity,
                            response.data.uid!!,
                            response.data.email!!,
                            response.data.token!!
                        )
                        showCustomNotif()
                    } else {
                        // Show error message
                        Toast.makeText(this@LoginActivity, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    // Show error message
                    Toast.makeText(this@LoginActivity, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    // Show error message
                    Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signInGoogle(){
        val signInIntent = googleSignIn.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    // Update UI with the signed-in user's information
                    showCustomNotif()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    // Update UI accordingly
                }
            }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}