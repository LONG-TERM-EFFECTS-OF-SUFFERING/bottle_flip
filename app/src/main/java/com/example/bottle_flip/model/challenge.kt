package com.example.bottle_flip.model

import java.io.Serializable

data class Challenge(
    var id: Int = 0, // Mantiene id como Int
    var description: String = "" // Valor predeterminado para compatibilidad con Firestore
) : Serializable
