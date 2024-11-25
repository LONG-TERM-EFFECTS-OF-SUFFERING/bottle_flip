package com.example.bottle_flip.repository

import com.example.bottle_flip.model.UserRequest
import com.example.bottle_flip.model.UserResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import javax.inject.Inject


class LoginRepository @Inject constructor(
	private val firebaseAuth: FirebaseAuth
) {
	fun registerUser(userRequest: UserRequest, callback: (UserResponse) -> Unit) {
		try {
			firebaseAuth.createUserWithEmailAndPassword(userRequest.email, userRequest.password)
				.addOnCompleteListener { task ->
					lateinit var userResponse: UserResponse

					if (task.isSuccessful) {
						val email = task.result?.user?.email
						userResponse = UserResponse(
								email = email,
								isRegistered = true,
								message = "Successful register"
							)
					} else {
						val error = task.exception
						userResponse = if (error is FirebaseAuthUserCollisionException) {
									UserResponse(
										isRegistered = false,
										message = "The user already exists"
									)
								} else {
									UserResponse(
										isRegistered = false,
										message = "Error in registration"
									)
								}
					}
					callback(userResponse)
				}
		} catch (e: Exception) {
			callback(
				UserResponse(
					isRegistered = false,
					message = e.message ?: "Unknown error"
				)
			)
		}
	}

	fun loginUser(userRequest: UserRequest, callback: (Boolean) -> Unit) {
		val email = userRequest.email
		val password = userRequest.password

		if (email.isNotEmpty() && password.isNotEmpty()) {
			firebaseAuth.signInWithEmailAndPassword(email, password)
				.addOnCompleteListener {
					if (it.isSuccessful)
						callback(true)
					else
						callback(false)
				}
		} else
			callback(false)
	}

	fun logoutUser() {
		firebaseAuth.signOut()
	}
}