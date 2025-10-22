package com.pets.testtask

import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.pets.testtask.state.GameState
import com.pets.testtask.viewmodel.GameViewModel
import kotlinx.coroutines.launch


class GameActivity : AppCompatActivity() {

    private val gameViewModel: GameViewModel by viewModels()
    private lateinit var logTextView: TextView
    private lateinit var playerStatsTextView: TextView
    private lateinit var monsterStatsTextView: TextView
    private lateinit var attackButton: Button
    private lateinit var healButton: Button
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
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
        scrollView = findViewById(R.id.scrollView)
        logTextView = findViewById(R.id.logTextView)
        playerStatsTextView = findViewById(R.id.playerStatsTextView)
        monsterStatsTextView = findViewById(R.id.monsterStatsTextView)
        attackButton = findViewById(R.id.attackButton)
        healButton = findViewById(R.id.healButton)

        attackButton.setOnClickListener { gameViewModel.performAttack() }
        healButton.setOnClickListener { gameViewModel.performHeal() }
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

        playerStatsTextView.text = state.playerStats
        monsterStatsTextView.text = state.monsterStats

        logTextView.text = state.gameLog.joinToString("\n")

        attackButton.isEnabled = !state.isGameOver && !state.isVictory
        healButton.isEnabled =
            state.player.healCount > 0 && !state.isGameOver && !state.isVictory

        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }

        if (state.isGameOver) {
            showGameOverDialog()
        } else if (state.isVictory) {
            showVictoryDialog()
        }
    }
}