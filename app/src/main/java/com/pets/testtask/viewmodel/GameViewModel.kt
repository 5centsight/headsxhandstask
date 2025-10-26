package com.pets.testtask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pets.testtask.repository.GameRepository
import com.pets.testtask.repository.GameRepositoryImpl
import com.pets.testtask.service.BattleService
import com.pets.testtask.state.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameRepository: GameRepository = GameRepositoryImpl()
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    fun setupSession() {
        viewModelScope.launch {
            val player = gameRepository.createPlayer()
            val monsters = gameRepository.createMonsters()
            _gameState.value = GameState(
                player = player,
                monsters = monsters,
                currentMonsterIndex = 0,
                currentMonster = monsters.firstOrNull(),
                isGameOver = false,
                isVictory = false,
                gameLog = listOf("Игра началась!", "Вы встретили ${monsters.firstOrNull()?.name}а")
            )
        }
    }

    fun performAttack() {
        viewModelScope.launch {
            val currentState = _gameState.value

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
    }

    fun performHeal() {
        viewModelScope.launch {
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
        nextMonster?.let {
            addToLog("Вы встретили ${nextMonster.name}а")
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

    private fun addToLog(message: String) {
        _gameState.update { currentState ->
            currentState.copy(
                gameLog = currentState.gameLog + message
            )
        }
    }

    private fun checkGameEnd() {
        _gameState.update { currentState ->
            currentState.copy(
                isGameOver = !currentState.player.isAlive(),
                isVictory = currentState.currentMonsterIndex >=
                        currentState.monsters.size && currentState.player.isAlive()
            )
        }
    }
}