package com.example.bottle_flip.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.example.bottle_flip.databinding.ItemRetoBinding
import com.example.bottle_flip.view.viewholder.ChallengeViewHolder
import com.example.bottle_flip.model.Challenge



class ChallengeAdapter(private val listChallenge:MutableList<Challenge>, private val navController: NavController):RecyclerView.Adapter<ChallengeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeViewHolder {
        val binding = ItemRetoBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ChallengeViewHolder(binding, navController)
    }

    override fun getItemCount(): Int {
        return listChallenge.size
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        val Challenge = listChallenge[position]
        holder.setItemInventory(Challenge)

    }
}