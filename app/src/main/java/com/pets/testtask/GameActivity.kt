package com.pets.testtask

import android.annotation.SuppressLint
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

    private val viewModel: GameViewModel by viewModels()
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
                viewModel.gameState.collect { state ->
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

        attackButton.setOnClickListener { viewModel.performAttack() }
        healButton.setOnClickListener { viewModel.performHeal() }
    }

    private fun startNewGame() {
        viewModel.setupSession()
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle("Игра окончена")
            .setMessage("Вы проиграли. Хотите сыграть еще?")
            .setPositiveButton("Новая игра") { _, _ -> startNewGame() }
            .setNegativeButton("Выйти") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun showVictoryDialog() {
        AlertDialog.Builder(this)
            .setTitle("Победа!")
            .setMessage("Вы победили всех монстров! Хотите сыграть еще?")
            .setPositiveButton("Новая игра") { _, _ -> startNewGame() }
            .setNegativeButton("Выйти") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(state: GameState) {

        state.player.let { player ->
            playerStatsTextView.text = """
                Игрок: ${player.name}
                Здоровье: ${player.health}/${player.maxHealth}
                Атака: ${player.attack}
                Защита: ${player.defense}
                Исцелений: ${player.healCount}
            """.trimIndent()

        }

        state.currentMonster?.let { monster ->
            monsterStatsTextView.text = """
                Монстр: ${monster.name}
                Здоровье: ${monster.health}/${monster.maxHealth}
                Атака: ${monster.attack}
                Защита: ${monster.defense}
            """.trimIndent()
        } ?: run {
            monsterStatsTextView.text = "Все монстры побеждены!"
        }

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