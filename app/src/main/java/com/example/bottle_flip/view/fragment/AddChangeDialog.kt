package com.example.bottle_flip.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.bottle_flip.R
import com.example.bottle_flip.databinding.DialogAgregarRetoBinding
import com.example.bottle_flip.model.Challenge
import com.example.bottle_flip.viewmodel.ChallengeViewModel

class AddChangeDialog : Fragment() {

    private lateinit var binding: DialogAgregarRetoBinding
    private val ChallengeViewModel: ChallengeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAgregarRetoBinding.inflate(inflater)
        //binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controladores()

        binding.etDescripcionReto.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val textEntered = !s.isNullOrBlank()
                binding.btnAgregar.isEnabled = textEntered
                binding.btnAgregar.backgroundTintList = if (textEntered)
                    ContextCompat.getColorStateList(requireContext(), android.R.color.holo_orange_light)

                else
                    ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray) // Cambia el color si deseas un diferente para el estado deshabilitado

                binding.btnAgregar.setTextColor(
                    if (textEntered)
                        ContextCompat.getColor(requireContext(), android.R.color.white) // Color del texto cuando está habilitado
                    else
                        ContextCompat.getColor(requireContext(), android.R.color.black) //
                )

            }




            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })


    }

    private fun controladores() {
        //validarDatos()
        binding.btnCancelar.setOnClickListener{
            findNavController().popBackStack()
        }
        binding.btnAgregar.setOnClickListener {
            saveChallenge()
        }
    }

    private fun saveChallenge(){
        val description = binding.etDescripcionReto.text.toString()
        val challenge = Challenge(description = description )
        Log.d("ChallengeDebug", challenge.toString())
        ChallengeViewModel.saveChallenge(challenge)
        Log.d("test22",challenge.toString())
        Toast.makeText(context,"Artículo guardado !!", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()


    }

    private fun validarDatos() {
        val listEditText = listOf(binding.etDescripcionReto)


    }

}