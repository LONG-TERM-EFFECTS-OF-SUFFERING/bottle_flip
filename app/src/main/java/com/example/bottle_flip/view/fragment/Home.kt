package com.example.bottle_flip.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.example.bottle_flip.databinding.HomeBinding


class Home : Fragment() {
    private lateinit var binding: HomeBinding

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

}