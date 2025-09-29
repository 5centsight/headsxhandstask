package com.pets.testtask.state

import com.pets.testtask.model.DamageRange
import com.pets.testtask.model.Monster
import com.pets.testtask.model.Player

data class GameState(
    val player: Player = Player("", 1, 1, 1, 1, DamageRange(1, 1)),
    val monsters: List<Monster> = emptyList(),
    val currentMonster: Monster? = null,
    val currentMonsterIndex: Int = 0,
    val gameLog: List<String> = emptyList(),
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false
)