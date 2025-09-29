package com.pets.testtask.viewmodel

import androidx.lifecycle.ViewModel
import com.pets.testtask.model.DamageRange
import com.pets.testtask.model.Monster
import com.pets.testtask.model.Player
import com.pets.testtask.service.BattleService
import com.pets.testtask.state.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    fun setupSession() {
        val player = createPlayer()
        val monsters = createMonsters()
        _gameState.value = GameState(
            player = player,
            monsters = monsters,
            currentMonster = monsters.firstOrNull(),
            gameLog = listOf("Игра началась!", "Вы встретили ${monsters.firstOrNull()?.name}а")
        )
    }

    fun performAttack() {
        val currentState = _gameState.value
        if (currentState.isGameOver || currentState.isVictory) return

        val playerResult = playerAttack()
        handleAttackResult(currentState.player.name, playerResult)

        val updatedState = _gameState.value
        if (updatedState.currentMonster?.isAlive() == true && !updatedState.isGameOver) {
            val monster = updatedState.currentMonster
            val monsterResult = monsterAttack()
            handleAttackResult(monster.name, monsterResult)
        }

        checkGameEnd()
    }

    fun performHeal() {
        val currentState = _gameState.value
        if (currentState.player.useHeal()) {
            addToLog("Вы исцелились! Осталось исцелений: ${currentState.player.healCount}")

            val updatedState = _gameState.value
            val monster = updatedState.currentMonster
            if (monster != null && monster.isAlive()) {
                val monsterResult = monsterAttack()
                handleAttackResult(monster.name, monsterResult)
            }
            checkGameEnd()
        } else {
            addToLog("Нельзя исцелиться!")
        }
    }

    private fun handleAttackResult(attackerName: String, result: BattleService.BattleResult) {
        val currentState = _gameState.value
        when (result) {
            is BattleService.BattleResult.Miss -> {
                addToLog("$attackerName промахнулся!")
            }

            is BattleService.BattleResult.Hit -> {
                addToLog(
                    "$attackerName нанес ${
                        result.damage
                    } урона!"
                )
                updateCreatureHealth(attackerName, result.damage)
            }

            is BattleService.BattleResult.Kill -> {
                addToLog(
                    "$attackerName нанес ${
                        result.damage
                    } урона и убил ${currentState.currentMonster?.name}а!"
                )
                updateCreatureHealth(attackerName, result.damage)

                if (attackerName == currentState.player.name) {
                    nextMonster()
                }
            }
        }
    }

    private fun updateCreatureHealth(attackerName: String, damage: Int) {
        val currentState = _gameState.value
        _gameState.value = currentState.copy(
            player = if (attackerName != currentState.player.name) {
                currentState.player.apply { takeDamage(damage) }
            } else {
                currentState.player
            },
            currentMonster = if (attackerName == currentState.player.name) {
                currentState.currentMonster?.apply { takeDamage(damage) }
            } else {
                currentState.currentMonster
            }
        )
    }

    private fun createPlayer(): Player {
        return Player(
            name = "Герой",
            attack = (1..30).random(),
            defense = (1..30).random(),
            health = 100,
            maxHealth = 100,
            damage = DamageRange(10, 20)
        )
    }

    private fun createMonsters(): List<Monster> {
        val monsterNames = listOf("Гоблин", "Орк", "Тролль", "Лич", "Дракон")
        return monsterNames.map { name ->
                Monster(
                name = name,
                attack = (1..30).random(),
                defense = (1..30).random(),
                health = 50 + (0..50).random(),
                maxHealth = 100,
                damage = DamageRange(5, 15)
            )
        }
    }

    fun playerAttack(): BattleService.BattleResult {
        val monster = _gameState.value.currentMonster ?: return BattleService.BattleResult.Miss
        return BattleService.attack(_gameState.value.player, monster)
    }

    fun monsterAttack(): BattleService.BattleResult {
        val monster = _gameState.value.currentMonster ?: return BattleService.BattleResult.Miss
        return BattleService.attack(monster, _gameState.value.player)
    }

    fun nextMonster() {
        val currentState = _gameState.value
        val nextIndex = currentState.currentMonsterIndex + 1
        val nextMonster = if (nextIndex < currentState.monsters.size) {
            currentState.monsters[nextIndex]
        } else {
            null
        }
        _gameState.value = currentState.copy(
            currentMonsterIndex = nextIndex,
            currentMonster = nextMonster
        )
        if (nextMonster != null) {
            addToLog("Вы встретили ${nextMonster.name}а")
        }
    }

    private fun addToLog(message: String) {
        val currentState = _gameState.value
        _gameState.value = currentState.copy(
            gameLog = currentState.gameLog + message
        )
    }

    private fun checkGameEnd() {
        val currentState = _gameState.value
        val isGameOver = !currentState.player.isAlive()
        val isVictory =
            currentState.currentMonsterIndex >= currentState.monsters.size && currentState.player.isAlive()

        _gameState.value = currentState.copy(
            isGameOver = isGameOver,
            isVictory = isVictory
        )

        if (isGameOver) {
            addToLog("Игра окончена! Вы проиграли!")
        } else if (isVictory) {
            addToLog("Поздравляем! Вы победили всех монстров!")
        }
    }
}