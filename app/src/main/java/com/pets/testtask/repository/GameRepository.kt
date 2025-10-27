package com.pets.testtask.repository

import com.pets.testtask.state.GameState

interface GameRepository {
    suspend fun createNewGame(): GameState
}