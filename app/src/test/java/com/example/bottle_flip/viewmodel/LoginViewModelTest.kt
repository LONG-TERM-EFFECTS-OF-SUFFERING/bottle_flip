package com.example.bottle_flip.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.bottle_flip.model.UserRequest
import com.example.bottle_flip.repository.LoginRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import org.junit.Assert.*
import io.mockk.*

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.AuthResult
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

class LoginViewModelTest {

    //@get:Rule
    //val rule = InstantTaskExecutorRule() //código que involucra LiveData y ViewModel
    //private lateinit var loginViewModel: LoginViewModel
    //private lateinit var loginRepository: LoginRepository

    //@Before
    //fun setUp() {
    //    loginRepository = mock(LoginRepository::class.java)
    //    loginViewModel = LoginViewModel(loginRepository)
    //}

    // Necesario para probar LiveData y asegurar que las tareas se ejecuten en el mismo hilo
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val mockRepository: LoginRepository = mockk(relaxed = true)
    private val viewModel = LoginViewModel(mockRepository)

    @Test
    fun `loginUser should invoke repository and handle success`() {
        // Arrange
        val userRequest = UserRequest(email = "camilo@gmail.com", password = "1234567")
        val callbackSlot = slot<(Boolean) -> Unit>()

        // Mockear el comportamiento del repositorio
        every { mockRepository.loginUser(userRequest, capture(callbackSlot)) } answers {
            callbackSlot.captured(true) // Simular éxito
        }

        var result = false
        val isLoggedIn: (Boolean) -> Unit = { result = it }

        // Act
        viewModel.loginUser(userRequest, isLoggedIn)

        // Assert
        assertEquals(true, result)
        verify(exactly = 1) { mockRepository.loginUser(userRequest, any()) }
    }

    @Test
    fun `logoutUser should call repository logoutUser`() {
        // Act
        viewModel.logoutUser()

        // Assert
        verify(exactly = 1) { mockRepository.logoutUser() }
    }

    @Test
    fun `loginUser should invoke repository and handle failure`() {
        // Arrange
        val userRequest = UserRequest(email = "camilo@gmail.com", password = "1234567")
        val callbackSlot = slot<(Boolean) -> Unit>()

        // Mockear el comportamiento del repositorio
        every { mockRepository.loginUser(userRequest, capture(callbackSlot)) } answers {
            callbackSlot.captured(false) // Simular fallo
        }

        var result = false
        val isLoggedIn: (Boolean) -> Unit = { result = it }

        // Act
        viewModel.loginUser(userRequest, isLoggedIn)

        // Assert
        assertEquals(false, result)
        verify(exactly = 1) { mockRepository.loginUser(userRequest, any()) }
    }
}