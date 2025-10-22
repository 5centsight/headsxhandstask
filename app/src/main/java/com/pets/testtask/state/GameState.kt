package com.pets.testtask.state

import com.pets.testtask.model.DamageRange
import com.pets.testtask.model.Monster
import com.pets.testtask.model.Player

data class GameState(
    val player: Player = Player("", 1, 1, 1, 1, DamageRange()),
    val monsters: List<Monster> = emptyList(),
    val currentMonster: Monster? = null,
    val currentMonsterIndex: Int = 0,
    val gameLog: List<String> = emptyList(),
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false
) {
    val playerStats: String
        get() = """
            Игрок: ${player.name}
            Здоровье: ${player.health}/${player.maxHealth}
            Атака: ${player.attack}
            Защита: ${player.defense}
            Исцелений: ${player.healCount}
        """.trimIndent()

    val monsterStats: String
        get() = currentMonster?.let { monster ->
            """
                Монстр: ${monster.name}
                Здоровье: ${monster.health}/${monster.maxHealth}
                Атака: ${monster.attack}
                Защита: ${monster.defense}
            """.trimIndent()
        } ?: "Все монстры побеждены!"
}