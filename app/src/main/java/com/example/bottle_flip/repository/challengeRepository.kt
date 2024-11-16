package com.example.bottle_flip.repository
import android.content.Context
import android.util.Log
import com.example.bottle_flip.data.ChallengeDB
import com.example.bottle_flip.data.ChallengeDao
import com.example.bottle_flip.model.Challenge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class challengeRepository(val context: Context){

    private var challengeDao:ChallengeDao = ChallengeDB.getDatabase(context).challengeDao()

    suspend fun savechallenge(challenge: Challenge) {
        withContext(Dispatchers.IO) {
            Log.d("repositoryDebug", challenge.toString())
            Log.d("CurrentThread", Thread.currentThread().name)

            // Guardar el desafío en la base de datos
            challengeDao.saveChallenge(challenge)

            Log.d("repositoryDebug3", challenge.toString())
        }
    }


    suspend fun getListChallenge():MutableList<Challenge>{
        return withContext(Dispatchers.IO){
            val challenges = challengeDao.getListchallenge()
            Log.d("ChallengeRepository", challenges.toString()) // Imprimir aquí
            challenges
        }
    }

    suspend fun deletechallenge(challenge: Challenge){
        withContext(Dispatchers.IO){
            challengeDao.deletechallenge(challenge)

        }
    }

    suspend fun updateRepositoy(challenge: Challenge){
        withContext(Dispatchers.IO){
            challengeDao.updatechallenge(challenge)
            Log.d("repositoryDebug", challenge.toString())

        }
    }


}