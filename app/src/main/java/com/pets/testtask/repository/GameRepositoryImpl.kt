package com.pets.testtask.repository

import com.pets.testtask.model.DamageRange
import com.pets.testtask.model.Monster
import com.pets.testtask.model.Player
import com.pets.testtask.state.GameState

class GameRepositoryImpl : GameRepository {

    override suspend fun createNewGame(): GameState {
        val player = createPlayer()
        val monsters = createMonsters()
        return GameState(
            player = player,
            monsters = monsters,
            currentMonsterIndex = 0,
            currentMonster = monsters.firstOrNull(),
            isGameOver = false,
            isVictory = false,
            gameLog = listOf("Игра началась!", "Вы встретили ${monsters.firstOrNull()?.name}а")
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
}