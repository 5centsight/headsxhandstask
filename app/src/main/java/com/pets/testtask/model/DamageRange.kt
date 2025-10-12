package com.pets.testtask.model

data class DamageRange(val min: Int = 1, val max: Int = 1) {

    init {
        require(min in 1..max) { "Некорректный диапазон урона: $min-$max" }
    }

    fun getRandomDamage(): Int {
        return (min..max).random()
    }
}
