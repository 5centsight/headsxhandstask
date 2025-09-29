package com.pets.testtask

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pets.testtask.state.GameState
import com.pets.testtask.viewmodel.GameViewModel


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
        initializeViews()
        startNewGame()
    }

    private fun initializeViews() {
        scrollView = findViewById(R.id.scrollView)
        logTextView = findViewById(R.id.logTextView)
        playerStatsTextView = findViewById(R.id.playerStatsTextView)
        monsterStatsTextView = findViewById(R.id.monsterStatsTextView)
        attackButton = findViewById(R.id.attackButton)
        healButton = findViewById(R.id.healButton)

        attackButton.setOnClickListener { onAttack() }
        healButton.setOnClickListener { onHeal() }
    }

    private fun startNewGame() {
        viewModel.setupSession()
        updateUI(viewModel.gameState.value)
    }

    private fun onAttack() {
        viewModel.performAttack()
        updateUI(viewModel.gameState.value)
    }

    private fun onHeal() {
        viewModel.performHeal()
        updateUI(viewModel.gameState.value)
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

        playerStatsTextView.text = """
            Игрок: ${state.player.name}
            Здоровье: ${state.player.health}/${state.player.maxHealth}
            Атака: ${state.player.attack}
            Защита: ${state.player.defense}
            Исцелений: ${state.player.healCount}
        """.trimIndent()

        if (state.currentMonster != null) {
            monsterStatsTextView.text = """
                Монстр: ${state.currentMonster.name}
                Здоровье: ${state.currentMonster.health}/${state.currentMonster.maxHealth}
                Атака: ${state.currentMonster.attack}
                Защита: ${state.currentMonster.defense}
            """.trimIndent()
        } else {
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