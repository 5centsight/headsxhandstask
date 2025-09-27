package com.pets.testtask.service

import com.pets.testtask.model.Creature

object BattleService {

    fun attack(attacker: Creature, defender: Creature): BattleResult {
        val attackModifier = calculateAttackModifier(attacker, defender)
        val success = isAttackSuccessful(attackModifier)

        if (!success) {
            return BattleResult.Miss
        }

        val damage = attacker.damage.getRandomDamage()
        defender.takeDamage(damage)

        return if (defender.isAlive()) {
            BattleResult.Hit(damage)
        } else {
            BattleResult.Kill(damage)
        }
    }

    private fun calculateAttackModifier(attacker: Creature, defender: Creature): Int {
        return maxOf(1, attacker.attack - defender.defense + 1)
    }

    private fun isAttackSuccessful(attackModifier: Int): Boolean {
        val diceCount = maxOf(1, attackModifier)

        repeat(diceCount) {
            val diceRoll = (1..6).random()
            if (diceRoll >= 5) {
                return true
            }
        }

        return false
    }

    sealed class BattleResult {
        object Miss : BattleResult()
        data class Hit(val damage: Int) : BattleResult()
        data class Kill(val damage: Int) : BattleResult()
    }
}