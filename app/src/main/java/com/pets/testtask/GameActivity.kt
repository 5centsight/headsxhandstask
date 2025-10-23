package com.pets.testtask

import android.os.Bundle
import android.widget.ScrollView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pets.testtask.databinding.ActivityGameBinding
import com.pets.testtask.state.GameState
import com.pets.testtask.viewmodel.GameViewModel
import kotlinx.coroutines.launch


class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    private val gameViewModel by viewModels<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupObservers()
        initializeViews()

        if (savedInstanceState == null) {
            startNewGame()
        }
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
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.game_over))
            .setMessage(getString(R.string.lost_message))
            .setPositiveButton(getString(R.string.new_game)) { _, _ -> startNewGame() }
            .setNegativeButton(getString(R.string.quit)) { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun showVictoryDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.game_win))
            .setMessage(getString(R.string.win_message))
            .setPositiveButton(getString(R.string.new_game)) { _, _ -> startNewGame() }
            .setNegativeButton(getString(R.string.quit)) { _, _ -> finish() }
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