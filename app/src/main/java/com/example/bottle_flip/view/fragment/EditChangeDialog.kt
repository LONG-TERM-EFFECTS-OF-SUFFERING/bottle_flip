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
import com.example.bottle_flip.databinding.EditChallengeDialogBinding
import com.example.bottle_flip.model.Challenge
import com.example.bottle_flip.viewmodel.ChallengeViewModel



class EditChallengeDialog : DialogFragment() {

    private lateinit var receivedChallenge: Challenge
    private val challengeViewModel: ChallengeViewModel by viewModels()
    private lateinit var binding: EditChallengeDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val dialog = AlertDialog.Builder(requireContext())

        // Inflar el layout existente y obtener el binding
        binding = EditChallengeDialogBinding.inflate(LayoutInflater.from(context))
        dataChallenge()
        // Referenciar los elementos del layout usando el binding
        val editTextChallenge = binding.etDescripcionReto
        val buttonCancel = binding.btnCancelar
        val buttonSave = binding.btnGuardar

        // Configurar el botón Cancelar
        buttonCancel.setOnClickListener {
            dismiss()  // Cierra el diálogo
        }

        // Configurar el botón Guardar
        buttonSave.setOnClickListener {
            Log.d("upDateDebug", "inicio")
            updateChallenge()
            findNavController().navigate(R.id.action_edit_to_challenges)
            //dismiss()  // Cierra el diálogo
        }

        // Configurar el diálogo
        val alertDialog = dialog.setView(binding.root).setCancelable(true).create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        return dialog.create()
    }

    private fun dataChallenge() {
        val receivedBundle = arguments
        receivedChallenge = receivedBundle?.getSerializable("clave") as Challenge
        binding.etDescripcionReto.hint = receivedChallenge.description

    }

    private fun updateChallenge(){
        val receivedBundle = arguments
        receivedChallenge = receivedBundle?.getSerializable("clave") as Challenge
        val description = binding.etDescripcionReto.text.toString()
        val id = receivedChallenge.id
        val challenge = Challenge(id, description)
        Log.d("upDateDebug", challenge.toString())
        challengeViewModel.updateChallenge(challenge)

    }
}
