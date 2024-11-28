package com.example.bottle_flip.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bottle_flip.model.Challenge
import com.example.bottle_flip.repository.ChallengeRepository
import kotlinx.coroutines.launch
import com.example.bottle_flip.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ChallengeViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository
): ViewModel() {
    private val _listChallenge = SingleLiveEvent<MutableList<Challenge>>()
    val listChallenge: LiveData<MutableList<Challenge>> get() = _listChallenge

    private val _progresState = MutableLiveData(false)
    val progresState: LiveData<Boolean> = _progresState

    fun saveChallenge(challenge: Challenge) {
        
        _progresState.value = true


        challengeRepository.savechallenge(challenge,
            onSuccess = { documentId ->
                Log.d("viewModel", "Desafío guardado con ID: $documentId")
                _progresState.value = false
            },
            onFailure = { e ->
                Log.e("viewModelError", "Error al guardar el desafío: ${e.message}")
                _progresState.value = false
            }
        )
    }



    fun getListChallenge() {
        viewModelScope.launch {
            _progresState.value = true
            try {
                _listChallenge.value = challengeRepository.getListChallenge().toMutableList()
               Log.d("ChallengeViewModel", "List of challenges: ${_listChallenge.value}")
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
                Log.d("viewModelDebug", challenge.toString())
                challengeRepository.deletechallenge(challenge)
                Log.d("viewModelDebug2", challenge.toString())
                _progresState.value = false
            } catch (e: Exception) {
                Log.d("viewModelDebug3", challenge.toString())
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


    fun getRandomChallengeFromFirestore() {
        viewModelScope.launch {
            _progresState.value = true
            try {
                val challenge = challengeRepository.getRandomChallengeFromFirestore()
                _listChallenge.value = mutableListOf<Challenge>().apply {
                    challenge?.let { add(it) }
                }
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error fetching random challenge: ${e.message}")
            } finally {
                _progresState.value = false
            }
        }
    }
}

