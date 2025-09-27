package com.pets.testtask.model

class Player(
    name: String,
    attack: Int,
    defense: Int,
    health: Int,
    maxHealth: Int,
    damage: DamageRange
) : Creature(name, attack, defense, health, maxHealth, damage) {
    var healCount: Int = 4

    private fun canHeal(): Boolean = healCount > 0 && isAlive()

    fun useHeal(): Boolean {
        if (!canHeal()) return false

        val healAmount = (maxHealth * 0.3).toInt()
        heal(healAmount)
        healCount--
        return true
    }
}