package com.example.bottle_flip.Dialogs


import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.bottle_flip.databinding.DialogChallengeBinding
import com.example.bottle_flip.view.fragment.Game
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import kotlin.random.Random

class ChallengeDialog {
    companion object {
        fun showDialogChallenge(
            context: Context,
            fragment: androidx.fragment.app.Fragment,
            challengeText: String
        ) {
            val inflater = LayoutInflater.from(context)
            val binding = DialogChallengeBinding.inflate(inflater)

            val alertDialog = AlertDialog.Builder(context).create()
            alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            alertDialog.setCancelable(false) // Don't allow to cancel the dialog by clicking outside
            alertDialog.setView(binding.root) // Set the custom layout to the dialog

            // Get a random Pokémon image from an API
            fragment.lifecycleScope.launch(Dispatchers.IO) {
                val pokemonImageUrl = fetchRandomPokemonImage()
                withContext(Dispatchers.Main) {
                    Glide.with(fragment)
                        .load(pokemonImageUrl)
                        .into(binding.ivPokemon)
                }
            }

            // Get a random challenge from the local database (Simulated)
            binding.tvChallenge.text = challengeText

            // Button to close the dialog
            binding.btnClose.setOnClickListener {
                Toast.makeText(context, "Reto cerrado", Toast.LENGTH_SHORT).show()
                alertDialog.dismiss()
            }

            alertDialog.setOnDismissListener {
                if (fragment is Game) {
                    fragment.statusShowDialog(false)
                }
            }

            alertDialog.show()
        }

        private suspend fun fetchRandomPokemonImage(): String {
            return withContext(Dispatchers.IO) {
                val apiUrl = "https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json"
                val json = URL(apiUrl).readText()
                val pokemonArray = JSONObject(json).getJSONArray("pokemon")
                val randomIndex = Random.nextInt(pokemonArray.length())
                val randomPokemon = pokemonArray.getJSONObject(randomIndex)
                randomPokemon.getString("img") // URL de la imagen del Pokémon
            }
        }

        private fun getRandomChallenge(): String {
            val challenges = listOf(
                "Desafío 1: Completa 10 saltos",
                "Desafío 2: Corre en el lugar durante 1 minuto",
                "Desafío 3: Haz 5 flexiones"
            )
            return challenges.random()
        }
    }
}
