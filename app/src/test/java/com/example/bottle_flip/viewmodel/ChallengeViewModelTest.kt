package com.example.bottle_flip.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.bottle_flip.model.Challenge
import com.example.bottle_flip.repository.ChallengeRepository
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
import io.mockk.just
import io.mockk.runs
import org.junit.After
import org.junit.Assert
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class ChallengeViewModelTest {
    //private lateinit var challengeViewModel: ChallengeViewModel
    //private lateinit var challengeRepo: challengeRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // Para LiveData

    private val mockChallengeRepository: ChallengeRepository = mockk()

    private lateinit var viewModel: ChallengeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher) // Configura el test dispatcher
        viewModel = ChallengeViewModel(mockChallengeRepository)
    }

    @Test
    fun `getListChallenge updates listChallenge and progresState correctly`() = runTest {
        // Mockea el comportamiento del repositorio
        val mockChallenges = listOf(
            Challenge(21, "Challenge 21"),
            Challenge(22, "Challenge 22")
        )
        coEvery { mockChallengeRepository.getListChallenge() } returns mockChallenges

        // Ejecuta el método
        viewModel.getListChallenge()

        // Verifica los resultados
        assertEquals(mockChallenges, viewModel.listChallenge.value)
        assertEquals(false, viewModel.progresState.value)
    }

    @Test
    fun `getListChallenge handles exception and updates progresState correctly`() = runTest {
        // Simula un error en el repositorio
        coEvery { mockChallengeRepository.getListChallenge() } throws Exception("Error fetching challenges")

        // Ejecuta el método
        viewModel.getListChallenge()

        // Verifica los resultados
        assertEquals(null, viewModel.listChallenge.value) // La lista sigue nula
        assertEquals(false, viewModel.progresState.value) // progresState vuelve a false
    }


    @Test
    fun `updateChallenge updates progresState on success`() = runTest {
        // Mockea el comportamiento del repositorio
        coEvery { mockChallengeRepository.updateRepositoy(any()) } just runs

        // Crea un objeto Challenge de prueba
        val challenge = Challenge(1, "Test Challenge")

        // Ejecuta el método
        viewModel.updateChallenge(challenge)

        // Verifica que progresState sea actualizado correctamente
        assertEquals(false, viewModel.progresState.value)
    }

    @Test
    fun `updateChallenge updates progresState on failure`() = runTest {
        // Mockea un error en el repositorio
        coEvery { mockChallengeRepository.updateRepositoy(any()) } throws Exception("Error updating challenge")

        // Crea un objeto Challenge de prueba
        val challenge = Challenge(1, "Test Challenge")

        // Ejecuta el método
        viewModel.updateChallenge(challenge)

        // Verifica que progresState se actualice correctamente incluso después del error
        assertEquals(false, viewModel.progresState.value)
    }

    @Test
    fun `getRandomChallengeFromFirestore updates listChallenge on success`() = runTest {
        // Mock de un desafío aleatorio
        val mockChallenge = Challenge(id = 1, description = "Test Challenge")

        coEvery { mockChallengeRepository.getRandomChallengeFromFirestore() } returns mockChallenge

        // Ejecuta la función
        viewModel.getRandomChallengeFromFirestore()

        // Verifica que el estado de la lista se actualizó correctamente
        val result = viewModel.listChallenge.value
        assertEquals(1, result?.size)
        assertEquals(mockChallenge, result?.first())
        assertEquals(false, viewModel.progresState.value) // Verifica que progresState se actualizó
    }

    @Test
    fun `getRandomChallengeFromFirestore handles empty result`() = runTest {
        // Mock para una respuesta vacía
        coEvery { mockChallengeRepository.getRandomChallengeFromFirestore() } returns null

        // Ejecuta la función
        viewModel.getRandomChallengeFromFirestore()

        // Verifica que la lista esté vacía y progresState esté actualizado
        val result = viewModel.listChallenge.value
        assertEquals(0, result?.size)
        assertEquals(false, viewModel.progresState.value)
    }

    @Test
    fun `getRandomChallengeFromFirestore handles exception`() = runTest {
        // Mock para simular una excepción
        coEvery { mockChallengeRepository.getRandomChallengeFromFirestore() } throws Exception("Error fetching challenge")

        // Ejecuta la función
        viewModel.getRandomChallengeFromFirestore()

        // Verifica que la lista no se actualizó y progresState esté en false
        val result = viewModel.listChallenge.value
        assertEquals(null, result)
        assertEquals(false, viewModel.progresState.value)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Restaura el dispatcher original
    }
}