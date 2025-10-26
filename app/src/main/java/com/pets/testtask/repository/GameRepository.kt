package com.pets.testtask.repository

import com.pets.testtask.model.Monster
import com.pets.testtask.model.Player

interface GameRepository {
    suspend fun createPlayer(): Player
    suspend fun createMonsters(): List<Monster>
}