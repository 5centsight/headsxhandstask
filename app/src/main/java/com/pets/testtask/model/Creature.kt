package com.pets.testtask.model

open class Creature(
    val name: String,
    var attack: Int,
    var defense: Int,
    var health: Int,
    val maxHealth: Int,
    val damage: DamageRange
) {

    init {
        require(attack in 1..30) { "Атака должна быть от 1 до 30" }
        require(defense in 1..30) { "Защита должна быть от 1 до 30" }
        require(health in 0..maxHealth) { "Здоровье должно быть от 0 до $maxHealth" }
    }

    fun isAlive(): Boolean = health > 0

    fun takeDamage(damage: Int) {
        health = maxOf(0, health - damage)
    }

    fun heal(amount: Int) {
        health = minOf(maxHealth, health + amount)
    }
}