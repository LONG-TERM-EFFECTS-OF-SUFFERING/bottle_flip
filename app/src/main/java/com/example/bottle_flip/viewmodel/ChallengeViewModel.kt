package com.example.bottle_flip.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bottle_flip.model.Challenge
import com.example.bottle_flip.data.ChallengeDB
import com.example.bottle_flip.repository.challengeRepository
import kotlinx.coroutines.launch
import com.example.bottle_flip.data.ChallengeDao


class ChallengeViewModel(application: Application) : AndroidViewModel(application) {

    val context = getApplication<Application>()
    private val challengeRepository = challengeRepository(context)

    private val _listChallenge = MutableLiveData<MutableList<Challenge>>()
    val listChallenge: LiveData<MutableList<Challenge>> get() = _listChallenge

    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> = _progresState

    fun saveChallenge(challenge: Challenge) {
        viewModelScope.launch {
            Log.d("viewModelDebug", challenge.toString())
            _progresState.value = true // Inicia el progreso

            try {
                challengeRepository.savechallenge(challenge) // Llama al repositorio
                Log.d("viewModel", "Desafío guardado: ${challenge.toString()}")
            } catch (e: Exception) {
                Log.e("viewModelError", "Error al guardar el desafío: ${e.message}")
            } finally {
                _progresState.value = false // Termina el progreso
            }
        }
    }


    fun getListChallenge() {
        viewModelScope.launch {
            _progresState.value = true
            try {
                _listChallenge.value = challengeRepository.getListChallenge()
               // Log.d("ChallengeViewModel", "List of challenges: ${_listChallenge.value}")
                _progresState.value = false
            } catch (e: Exception) {
                _progresState.value = false
            }

        }
    }

    fun deleteChallenge(challenge: Challenge) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                challengeRepository.deletechallenge(challenge)
                _progresState.value = false
            } catch (e: Exception) {
                _progresState.value = false
            }

        }
    }

    fun updateChallenge(challenge: Challenge) {
        viewModelScope.launch {
            _progresState.value = true
            try {
                challengeRepository.updateRepositoy(challenge)
                Log.d("viewModel", challenge.toString())
                _progresState.value = false
            } catch (e: Exception) {
                _progresState.value = false
            }
        }
    }

}

