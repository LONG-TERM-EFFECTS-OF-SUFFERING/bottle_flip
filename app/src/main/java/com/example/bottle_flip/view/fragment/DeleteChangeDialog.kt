package com.example.bottle_flip.view.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.bottle_flip.R
import com.example.bottle_flip.databinding.DeleteChallengeDialogBinding
import com.example.bottle_flip.model.Challenge
import com.example.bottle_flip.viewmodel.ChallengeViewModel

class DeleteChangeDialog: DialogFragment() {

    private val challengeViewModel: ChallengeViewModel by viewModels()
    private lateinit var receivedChallenge: Challenge
    private lateinit var binding: DeleteChallengeDialogBinding
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext())

        // Inflar el layout existente y obtener el binding
        binding = DeleteChallengeDialogBinding.inflate(LayoutInflater.from(context))
        dataChallenge()
        // Referenciar los elementos del layout usando el binding
        val editTextChallenge = binding.deleteDescription
        val buttonSi = binding.btnSi
        val buttonNo = binding.btnNo

        // Configurar el botón si
        buttonSi.setOnClickListener {
            deleteChallenge()
            findNavController().navigate(R.id.action_delete_to_challenges)
            //dismiss()  // Cierra el diálogo
        }

        // Configurar el botón no
        buttonNo.setOnClickListener {

            dismiss()  // Cierra el diálogo
        }

        // Configurar el diálogo
        val alertDialog = dialog.setView(binding.root).setCancelable(true).create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog.create()
    }

    private fun dataChallenge() {
        val receivedBundle = arguments
        receivedChallenge = receivedBundle?.getSerializable("clave") as Challenge
        binding.deleteDescription.text = receivedChallenge.description

    }

    private fun deleteChallenge(){
        val receivedBundle = arguments
        receivedChallenge = receivedBundle?.getSerializable("clave") as Challenge
        val description = binding.deleteDescription.text.toString()
        val id = receivedChallenge.id
        val challenge = Challenge(id, description)
        Log.d("deleteDebug", challenge.toString())
        challengeViewModel.deleteChallenge(challenge)

    }
}
