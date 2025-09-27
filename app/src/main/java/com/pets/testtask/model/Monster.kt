package com.pets.testtask.model

class Monster(
    name: String,
    attack: Int,
    defense: Int,
    health: Int,
    maxHealth: Int,
    damage: DamageRange
) : Creature(name, attack, defense, health, maxHealth, damage)