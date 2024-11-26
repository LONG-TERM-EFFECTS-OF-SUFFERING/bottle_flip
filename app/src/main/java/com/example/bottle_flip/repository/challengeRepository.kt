package com.example.bottle_flip.repository
import android.content.Context
import android.util.Log
import com.example.bottle_flip.data.ChallengeDB
import com.example.bottle_flip.data.ChallengeDao
import com.example.bottle_flip.model.Challenge
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class challengeRepository(val context: Context){

    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Challenge")
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

    suspend fun deletechallenge(challenge: Challenge) {

        collectionRef.whereEqualTo("id", challenge.id).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.d("repositoryDebug3", challenge.toString())
                    throw Exception("No document found with id: ${challenge.id}")
                }

                for (document in querySnapshot.documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("repositoryDebug", "Document with id ${challenge.id} deleted successfully")
                        }
                        .addOnFailureListener { e ->
                            Log.e("repositoryDebug", "Failed to delete document: ${e.message}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("repositoryDebug", "Error fetching documents: ${e.message}", e)
            }
    }

    fun updateRepositoy(challenge: Challenge) {
        val newDescription = challenge.description

        collectionRef.whereEqualTo("id", challenge.id).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.d("repositoryDebug", "No document found with id: ${challenge.id}")
                    return@addOnSuccessListener
                }


                for (document in querySnapshot.documents) {
                    // Actualizar solo el campo "description" con el valor de challenge.description
                    document.reference.update("description", newDescription)
                        .addOnSuccessListener {
                            Log.d("repositoryDebug", "Description updated for document with id ${challenge.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.e("repositoryDebug", "Failed to update description: ${e.message}", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("repositoryDebug", "Error fetching documents: ${e.message}", e)
            }
    }


    suspend fun getRandomChallengeFromFirestore(): Challenge? {
        return withContext(Dispatchers.IO) {
            try {
                val challenges = mutableListOf<Challenge>()
                val snapshot = collectionRef.get().await() // Use coroutines with Firestore
                for (document in snapshot.documents) {
                    val challenge = document.toObject(Challenge::class.java)
                    if (challenge != null) {
                        challenges.add(challenge)
                    }
                }
                if (challenges.isNotEmpty()) challenges.random() else null
            } catch (e: Exception) {
                Log.e("FirestoreError", "Error fetching challenges: ${e.message}")
                null
            }
        }
    }


}