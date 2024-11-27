package com.example.bottle_flip.repository
import android.content.Context
import android.util.Log
//import com.example.bottle_flip.data.ChallengeDB
//import com.example.bottle_flip.data.ChallengeDao
import com.example.bottle_flip.model.Challenge
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class challengeRepository(val context: Context){

    private val db = FirebaseFirestore.getInstance()
    private val collectionRef = db.collection("Challenge")

    fun savechallenge(challenge: Challenge, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        // Primero obtenemos todos los documentos de la colección
        collectionRef.get()
            .addOnSuccessListener { querySnapshot ->
                var newId = challenge.id  // Usamos el id del challenge que nos pasan
                var idExists = true

                // Verificamos si el id ya está en uso
                while (idExists) {
                    idExists = false
                    for (document in querySnapshot.documents) {
                        // Comprobamos si el campo "id" en el documento ya tiene el mismo valor
                        val existingId = document.getLong("id")?.toInt()
                        if (existingId == newId) {
                            // Si el id ya existe, incrementamos el id
                            newId++
                            idExists = true
                            break
                        }
                    }
                }

                // Creamos el challenge con el nuevo id único
                val challengeWithNewId = challenge.copy(id = newId)

                // Ahora agregamos el challenge con el nuevo id
                collectionRef.add(challengeWithNewId)
                    .addOnSuccessListener { documentReference ->
                        Log.d("repositoryDebug", "Nuevo desafío agregado con ID: ${documentReference.id}")
                        onSuccess(documentReference.id)  // Aquí puedes pasar el ID del nuevo documento
                    }
                    .addOnFailureListener { e ->
                        Log.e("repositoryDebug", "Error al agregar el desafío", e)
                        onFailure(e)  // Se pasa el error para que lo manejes fuera del método
                    }
            }
            .addOnFailureListener { e ->
                Log.e("repositoryDebug", "Error al obtener los documentos", e)
                onFailure(e)
            }
    }



    suspend fun getListChallenge(): List<Challenge> {
       return try {
           val querySnapshot = collectionRef.get().await() // Usar coroutines con Firestore
           if (querySnapshot.isEmpty) {
               emptyList() // Si no hay desafíos, retornar una lista vacía
           } else {
               // Mapear los documentos a objetos Challenge
               querySnapshot.documents.mapNotNull { document ->
                   document.toObject(Challenge::class.java)?.apply {
                       id = document.getLong("id")?.toInt() ?: 0

                   }
               }
           }
       } catch (e: Exception) {
           Log.e("repositoryDebug", "Error al traer los retos: ${e.message}", e)


           emptyList() // Retorna una lista vacía en caso de error
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
                            Log.d("repositoryDebug222", challenge.toString())
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