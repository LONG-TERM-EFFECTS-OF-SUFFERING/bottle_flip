package com.example.bottle_flip.model

import java.io.Serializable

data class Challenge(
    var id: Int = 0,
    var description: String = ""
) : Serializable
