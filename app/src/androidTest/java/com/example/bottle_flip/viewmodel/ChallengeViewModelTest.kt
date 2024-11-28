package com.example.bottle_flip.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.bottle_flip.model.Challenge
import com.example.bottle_flip.repository.challengeRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.Before
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.junit.After
import org.junit.Assert
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever


class ChallengeViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mockApplication: Application = mockk(relaxed = true)
    private val mockChallengeRepository: challengeRepository = mockk(relaxed = true)

    private lateinit var viewModel: ChallengeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Inicializar Firebase para pruebas
        val options = FirebaseOptions.Builder()
            .setApplicationId("bottle-flip-1c5fc") // Reemplaza con tu ID de aplicación
            .setApiKey("AIzaSyAyT0dBrshZpH6dD2oIJRWpQu8zYdjhTMg") // Reemplaza con tu API Key
            //.setDatabaseUrl("https://your-database.firebaseio.com") // URL de la base de datos si es necesario
            .build()

        if (FirebaseApp.getApps(ApplicationProvider.getApplicationContext()).isEmpty()) {
            FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext(), options)
        }

        // Otros setups como los mocks
        Dispatchers.setMain(testDispatcher)
        mockkConstructor(challengeRepository::class)
        coEvery { anyConstructed<challengeRepository>().getListChallenge() } returns mutableListOf(
            Challenge(id = 21, description = "Challenge 21"),
            Challenge(id = 22, description = "Challenge 22")
        )

        viewModel = ChallengeViewModel(mockApplication)
    }

    @Test
    fun `getListChallenge should update progress state and listChallenge on success`() = runTest {
        // Act
        viewModel.getListChallenge()
        advanceUntilIdle()

        // Assert
        assertEquals(true, viewModel.progresState.value) // Verifica progreso inicial
        assertEquals(
            listOf(
                Challenge(id = 21, description = "Challenge 21"),
                Challenge(id = 22, description = "Challenge 22")
            ),
            viewModel.listChallenge.value
        ) // Verifica lista de desafíos
        assertEquals(false, viewModel.progresState.value) // Verifica progreso final
    }

    @Test
    fun `getListChallenge should handle exceptions`() = runTest {
        // Mockea para lanzar una excepción
        coEvery { anyConstructed<challengeRepository>().getListChallenge() } throws Exception("Error fetching challenges")

        // Act
        viewModel.getListChallenge()
        advanceUntilIdle()

        // Assert
        assertEquals(null, viewModel.listChallenge.value) // Lista debe ser nula
        assertEquals(false, viewModel.progresState.value) // Verifica progreso final
    }
}