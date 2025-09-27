package com.pets.testtask.model

data class DamageRange(val min: Int, val max: Int) {

    init {
        require(min in 1..max) { "Некорректный диапазон урона: $min-$max" }
    }

    fun getRandomDamage(): Int {
        return (min..max).random()
    }
}
