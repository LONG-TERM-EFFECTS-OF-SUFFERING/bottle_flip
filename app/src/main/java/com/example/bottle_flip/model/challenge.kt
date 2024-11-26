package com.example.bottle_flip.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Challenge(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0, // Mantiene id como Int
    var description: String = "" // Valor predeterminado para compatibilidad con Firestore
) : Serializable
