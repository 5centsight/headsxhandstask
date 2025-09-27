package com.pets.testtask.viewmodel

import androidx.lifecycle.ViewModel
import com.pets.testtask.model.DamageRange
import com.pets.testtask.model.Monster
import com.pets.testtask.model.Player
import com.pets.testtask.service.BattleService

class GameViewModel : ViewModel() {
    private lateinit var player: Player
    private val monsters = mutableListOf<Monster>()
    private var currentMonsterIndex = 0

    fun setupGame() {
        createPlayer()
        createMonsters()
        currentMonsterIndex = 0
    }

    private fun createPlayer() {
        player = Player(
            name = "Герой",
            attack = (1..30).random(),
            defense = (1..30).random(),
            health = 100,
            maxHealth = 100,
            damage = DamageRange(10, 20)
        )
    }

    private fun createMonsters() {
        monsters.clear()

        val monsterNames = listOf("Гоблин", "Орк", "Тролль", "Лич", "Дракон")

        monsterNames.forEach { name ->
            monsters.add(
                Monster(
                name = name,
                attack = (1..30).random(),
                defense = (1..30).random(),
                health = 50 + (0..50).random(),
                maxHealth = 100,
                damage = DamageRange(5, 15)
            )
            )
        }
    }

    fun getCurrentMonster(): Monster? {
        return if (currentMonsterIndex < monsters.size) {
            monsters[currentMonsterIndex]
        } else {
            null
        }
    }

    fun getPlayer(): Player = player

    fun playerAttack(): BattleService.BattleResult {
        val monster = getCurrentMonster() ?: return BattleService.BattleResult.Miss
        return BattleService.attack(player, monster)
    }

    fun monsterAttack(): BattleService.BattleResult {
        val monster = getCurrentMonster() ?: return BattleService.BattleResult.Miss
        return BattleService.attack(monster, player)
    }

    fun nextMonster(): Boolean {
        currentMonsterIndex++
        return currentMonsterIndex >= monsters.size
    }

    fun isGameOver(): Boolean = !player.isAlive()

    fun isVictory(): Boolean = currentMonsterIndex >= monsters.size && player.isAlive()

    fun playerHeal(): Boolean {
        return player.useHeal()
    }

    fun getRemainingHeals(): Int = player.healCount
}