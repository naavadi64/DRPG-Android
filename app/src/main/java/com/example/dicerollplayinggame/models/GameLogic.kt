package com.example.dicerollplayinggame.models

class GameLogic(val enemyType: Int, val currentPlayerHealth: Int)
{
    var playerEntity: GameEntity = GameEntity(true, maxHealth = 50, health = currentPlayerHealth)
    var enemyEntity: GameEntity = GameEntity(false, maxHealth = enemyType*10, health = enemyType*10, attack = enemyType/2)
    var inCombat: Boolean = false

    fun getPlayerMaxHealth(): Int { return playerEntity.maxHealth }

    fun getPlayerHealth(): Int { return playerEntity.health }

    fun getPlayerAttack(): Int { return playerEntity.attack }

    fun changePlayerHealth(value: Int): Boolean{
        playerEntity.health += value
        if (playerEntity.health <= 0) {
            playerEntity.health = 0
            playerEntity.isDead = true
        } else if (playerEntity.health > playerEntity.maxHealth) {
            playerEntity.health = playerEntity.maxHealth
        }
        return playerEntity.isDead
    }

    fun isPlayerDead(): Boolean { return playerEntity.isDead }

    fun getEnemyMaxHealth(): Int { return enemyEntity.maxHealth }

    fun getEnemyHealth(): Int { return enemyEntity.health }

    fun getEnemyAttack(): Int { return enemyEntity.attack }

    fun changeEnemyHealth(value: Int): Boolean{
        enemyEntity.health += value
        if (enemyEntity.health <= 0) {
            enemyEntity.health = 0
            enemyEntity.isDead = true
        }
        return enemyEntity.isDead
    }

    fun isEnemyDead(): Boolean { return enemyEntity.isDead }

}