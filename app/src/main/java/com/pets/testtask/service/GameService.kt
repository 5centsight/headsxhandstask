package com.pets.testtask.service

import com.pets.testtask.state.GameState

class GameService() {
    fun performAttack(currentState: GameState): GameState {
        val playerResult = playerAttack(currentState)
        var updatedState = handleAttackResult(currentState, currentState.player.name, playerResult)

        if (updatedState.currentMonster?.isAlive() == true && !updatedState.isGameOver) {
            val monster = updatedState.currentMonster
            val monsterResult = monsterAttack(currentState)
            updatedState = handleAttackResult(updatedState, monster.name, monsterResult)
        }

        return checkGameEnd(updatedState)
    }

    fun performHeal(currentState: GameState): GameState {
        val healed = currentState.player.useHeal()
        var updatedState = if (healed) {
            currentState.copy(gameLog = currentState.gameLog + "Вы исцелились! Осталось исцелений: ${currentState.player.healCount}")
        } else {
            currentState.copy(gameLog = currentState.gameLog + "Нельзя исцелиться!")
        }
        if (healed && updatedState.currentMonster?.isAlive() == true) {
            val monsterResult = monsterAttack(updatedState)
            updatedState =
                handleAttackResult(updatedState, updatedState.currentMonster.name, monsterResult)
        }
        return checkGameEnd(updatedState)
    }

    private fun playerAttack(gameState: GameState): BattleService.BattleResult {
        val monster = gameState.currentMonster ?: return BattleService.BattleResult.Miss
        return BattleService.attack(gameState.player, monster)
    }

    private fun monsterAttack(gameState: GameState): BattleService.BattleResult {
        val monster = gameState.currentMonster ?: return BattleService.BattleResult.Miss
        return BattleService.attack(monster, gameState.player)
    }

    private fun nextMonster(currentState: GameState): GameState {
        val nextIndex = currentState.currentMonsterIndex + 1
        val nextMonster = if (nextIndex < currentState.monsters.size) {
            currentState.monsters[nextIndex]
        } else {
            null
        }
        val updatedState = currentState.copy(
            currentMonsterIndex = nextIndex,
            currentMonster = nextMonster
        )
        return nextMonster?.let {
            updatedState.copy(gameLog = updatedState.gameLog + "Вы встретили ${nextMonster.name}а")
        } ?: updatedState
    }

    private fun handleAttackResult(
        currentState: GameState,
        attackerName: String,
        result: BattleService.BattleResult
    ): GameState {
        return when (result) {
            is BattleService.BattleResult.Miss -> {
                currentState.copy(gameLog = currentState.gameLog + "$attackerName промахнулся!")
            }

            is BattleService.BattleResult.Hit -> {
                val updatedState = updateCreatureHealth(currentState, attackerName, result.damage)
                updatedState.copy(
                    gameLog = updatedState.gameLog + "$attackerName нанес ${result.damage} урона!"
                )
            }

            is BattleService.BattleResult.Kill -> {
                var updatedState = updateCreatureHealth(currentState, attackerName, result.damage)
                updatedState = updatedState.copy(
                    gameLog = updatedState.gameLog + "$attackerName нанес ${
                        result.damage
                    } урона и убил ${currentState.currentMonster?.name}а!"
                )

                if (attackerName == currentState.player.name) {
                    updatedState = nextMonster(updatedState)
                }
                updatedState
            }
        }
    }

    private fun updateCreatureHealth(
        currentState: GameState,
        attackerName: String,
        damage: Int
    ): GameState {
        return currentState.copy(
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

    private fun checkGameEnd(currentState: GameState): GameState {
        return currentState.copy(
            isGameOver = !currentState.player.isAlive(),
            isVictory = currentState.currentMonsterIndex >=
                    currentState.monsters.size && currentState.player.isAlive()
        )
    }
}