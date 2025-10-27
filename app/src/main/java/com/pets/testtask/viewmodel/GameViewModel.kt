package com.pets.testtask.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pets.testtask.repository.GameRepository
import com.pets.testtask.repository.GameRepositoryImpl
import com.pets.testtask.service.GameService
import com.pets.testtask.state.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameRepository: GameRepository = GameRepositoryImpl(),
    private val gameService: GameService = GameService()
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    fun setupSession() {
        viewModelScope.launch {
            val newGameState = gameRepository.createNewGame()
            _gameState.update { it ->
                newGameState.copy()
            }
        }
    }

    fun performAttack() {
        viewModelScope.launch {
            val currentState = _gameState.value
            val newState = gameService.performAttack(currentState)
            _gameState.update { newState.copy() }
        }
    }

    fun performHeal() {
        viewModelScope.launch {
            val currentState = _gameState.value
            val newState = gameService.performHeal(currentState)
            _gameState.update { newState.copy() }
        }
    }
}