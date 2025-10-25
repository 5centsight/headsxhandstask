package com.pets.testtask.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.pets.testtask.R
import com.pets.testtask.databinding.FragmentGameBinding
import com.pets.testtask.state.GameState
import com.pets.testtask.viewmodel.GameViewModel
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val gameViewModel by viewModels<GameViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        initializeViews()

        if (savedInstanceState == null) {
            startNewGame()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                gameViewModel.gameState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun initializeViews() {
        binding.attackButton.setOnClickListener { gameViewModel.performAttack() }
        binding.healButton.setOnClickListener { gameViewModel.performHeal() }
    }

    private fun startNewGame() {
        gameViewModel.setupSession()
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.game_over))
            .setMessage(getString(R.string.lost_message))
            .setPositiveButton(getString(R.string.new_game)) { _, _ -> startNewGame() }
            .setNegativeButton(getString(R.string.quit)) { _, _ -> findNavController().navigate(R.id.action_gameFragment_to_menuFragment) }
            .setCancelable(false)
            .show()
    }

    private fun showVictoryDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.game_win))
            .setMessage(getString(R.string.win_message))
            .setPositiveButton(getString(R.string.new_game)) { _, _ -> startNewGame() }
            .setNegativeButton(getString(R.string.quit)) { _, _ -> findNavController().navigate(R.id.action_gameFragment_to_menuFragment) }
            .setCancelable(false)
            .show()
    }

    private fun updateUI(state: GameState) {

        binding.playerStatsTextView.text = state.playerStats
        binding.monsterStatsTextView.text = state.monsterStats

        binding.logTextView.text = state.gameLog.joinToString("\n")

        binding.attackButton.isEnabled = !state.isGameOver && !state.isVictory
        binding.healButton.isEnabled =
            state.player.healCount > 0 && !state.isGameOver && !state.isVictory

        binding.scrollView.post {
            binding.scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }

        if (state.isGameOver) {
            showGameOverDialog()
        } else if (state.isVictory) {
            showVictoryDialog()
        }
    }
}