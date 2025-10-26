package com.pets.testtask.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.pets.testtask.R
import com.pets.testtask.databinding.FragmentMenuBinding
import com.pets.testtask.viewmodel.GameViewModel

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private val gameViewModel: GameViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {
        binding.startButton.setOnClickListener {
            gameViewModel.setupSession()
            findNavController().navigate(R.id.action_menuFragment_to_gameFragment)
        }
        binding.exitButton.setOnClickListener {
            activity?.finish()
        }
    }
}