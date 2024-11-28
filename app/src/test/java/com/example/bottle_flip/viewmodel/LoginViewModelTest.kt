package com.example.bottle_flip.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.bottle_flip.model.UserRequest
import com.example.bottle_flip.repository.LoginRepository
import org.junit.Assert.*
import io.mockk.*
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
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