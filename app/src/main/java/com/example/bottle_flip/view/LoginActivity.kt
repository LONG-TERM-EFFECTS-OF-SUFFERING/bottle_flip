package com.example.bottle_flip.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.bottle_flip.MainActivity
import com.example.bottle_flip.R
import com.example.bottle_flip.model.UserRequest
import com.example.bottle_flip.viewmodel.LoginViewModel
import com.example.bottle_flip.databinding.LoginBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {
	private lateinit var binding: LoginBinding
	private val loginViewModel: LoginViewModel by viewModels()
	private lateinit var sharedPreferences: SharedPreferences


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = DataBindingUtil.setContentView(this,R.layout.login)
		sharedPreferences = getSharedPreferences("shared", Context.MODE_PRIVATE)

		setup()
		viewModelObservers()
		checkSession()
	}

	private fun viewModelObservers() {
		observerIsRegistered()
	}

	private fun observerIsRegistered() {
		loginViewModel.isRegistered.observe(this) { userResponse ->
			if (userResponse.isRegistered) {
				Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
				sharedPreferences.edit().putString("email", userResponse.email).apply()
				goHome()
			} else {
				Toast.makeText(this, userResponse.message, Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun setup() {
		binding.tvRegister.setOnClickListener {
			registerUser()
		}

		binding.btnLogin.setOnClickListener {
			loginUser()
		}
	}

	private fun registerUser() {
		val email = binding.etEmail.text.toString()
		val pass = binding.etPassword.text.toString()
		val userRequest = UserRequest(email, pass)

		if (email.isNotEmpty() && pass.isNotEmpty())
			loginViewModel.registerUser(userRequest)
		else
			Toast.makeText(this, "Error: empty fields", Toast.LENGTH_SHORT).show()
	}

	private fun goHome() {
		val intent = Intent (this, MainActivity::class.java)
		startActivity(intent)
		finish()
	}

	private fun loginUser(){
		val email = binding.etEmail.text.toString()
		val pass = binding.etPassword.text.toString()
		val userRequest = UserRequest(email, pass)

		loginViewModel.loginUser(userRequest){ isLogin ->
			if (isLogin) {
				sharedPreferences.edit().putString("email", email).apply()
				goHome()
			} else
				Toast.makeText(this, "Error: incorrect login", Toast.LENGTH_SHORT).show()
		}
	}

	private fun checkSession() {
		val email = sharedPreferences.getString("email", null)
		loginViewModel.session(email) { isViewEnable ->
			if (isViewEnable) {
				binding.clContainer.visibility = View.INVISIBLE
				goHome()
			}
		}
	}
}
