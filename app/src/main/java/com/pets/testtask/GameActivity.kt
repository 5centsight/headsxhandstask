package com.pets.testtask

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.pets.testtask.service.BattleService
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
        updateUI()
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
        viewModel.setupGame()
        logTextView.text = ""
        addToLog("Новая игра началась!")
        addToLog("Вы встретили ${viewModel.getCurrentMonster()?.name}а")
        updateUI()
    }

    private fun onAttack() {
        if (viewModel.isGameOver() || viewModel.isVictory()) {
            return
        }

        val playerResult = viewModel.playerAttack()
        val player = viewModel.getPlayer()
        handleAttackResult(player.name, playerResult)

        if (checkGameEnd()) return

        val monster = viewModel.getCurrentMonster()
        if (monster != null && monster.isAlive()) {
            val monsterResult = viewModel.monsterAttack()
            handleAttackResult(monster.name, monsterResult)
        }

        checkGameEnd()
        updateUI()
    }

    private fun onHeal() {
        if (viewModel.playerHeal()) {
            addToLog("Вы исцелились! Осталось исцелений: ${viewModel.getRemainingHeals()}")
            updateUI()

            val monster = viewModel.getCurrentMonster()
            if (monster != null && monster.isAlive()) {
                val monsterResult = viewModel.monsterAttack()
                handleAttackResult(monster.name, monsterResult)
                checkGameEnd()
                updateUI()
            }
        } else {
            addToLog("Нельзя исцелиться!")
        }
    }

    private fun handleAttackResult(attackerName: String, result: BattleService.BattleResult) {
        when (result) {
            is BattleService.BattleResult.Miss -> {
                addToLog("$attackerName промахнулся!")
            }
            is BattleService.BattleResult.Hit -> {
                addToLog("$attackerName нанес ${
                    result.damage
                } урона!")
            }
            is BattleService.BattleResult.Kill -> {
                addToLog("$attackerName нанес ${
                    result.damage
                } урона и убил ${viewModel.getCurrentMonster()?.name}а!"
                )

                if (attackerName == "Герой") {
                    val nextMonster = viewModel.nextMonster()
                    if (!nextMonster) {
                        addToLog("Вы встретили ${viewModel.getCurrentMonster()?.name}а")
                    }
                }
            }
        }
    }

    private fun checkGameEnd(): Boolean {
        return if (viewModel.isGameOver()) {
            addToLog("Игра окончена! Вы проиграли!")
            showGameOverDialog()
            true
        } else if (viewModel.isVictory()) {
            addToLog("Поздравляем! Вы победили всех монстров!")
            showVictoryDialog()
            true
        } else {
            false
        }
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
    private fun updateUI() {
        val player = viewModel.getPlayer()
        val monster = viewModel.getCurrentMonster()

        playerStatsTextView.text = """
            Игрок: ${player.name}
            Здоровье: ${player.health}/${player.maxHealth}
            Атака: ${player.attack}
            Защита: ${player.defense}
            Исцелений: ${player.healCount}
        """.trimIndent()

        if (monster != null) {
            monsterStatsTextView.text = """
                Монстр: ${monster.name}
                Здоровье: ${monster.health}/${monster.maxHealth}
                Атака: ${monster.attack}
                Защита: ${monster.defense}
            """.trimIndent()
        } else {
            monsterStatsTextView.text = "Все монстры побеждены!"
        }

        attackButton.isEnabled = !viewModel.isGameOver() && !viewModel.isVictory()
        healButton.isEnabled =
            viewModel.getRemainingHeals() > 0 && !viewModel.isGameOver() && !viewModel.isVictory()

        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }

    private fun addToLog(message: String) {
        val currentText = logTextView.text.toString()
        logTextView.text = if (currentText.isEmpty()) {
            message
        } else {
            "$currentText\n$message"
        }
    }
}