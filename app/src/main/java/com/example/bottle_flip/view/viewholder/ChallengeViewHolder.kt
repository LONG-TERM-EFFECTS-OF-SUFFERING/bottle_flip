package com.example.bottle_flip.view.viewholder

import android.os.Bundle
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.bottle_flip.R
import com.example.bottle_flip.databinding.ItemRetoBinding
import com.example.bottle_flip.model.Challenge

class ChallengeViewHolder(binding: ItemRetoBinding, navController: NavController) :
    RecyclerView.ViewHolder(binding.root) {
    val bindingItem = binding
    val navController = navController
    fun setItemInventory(challenge: Challenge) {
        bindingItem.descripcion.text = challenge.description

        bindingItem.btnEliminar.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("clave", challenge)
            navController.navigate(R.id.deleteChangeDialog, bundle)
        }

        bindingItem.btnEditar.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("clave", challenge)
            navController.navigate(R.id.editChallengeDialog, bundle)
        }
    }
}