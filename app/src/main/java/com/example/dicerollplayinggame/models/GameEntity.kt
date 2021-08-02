package com.example.dicerollplayinggame.models

data class GameEntity(
    val isControlled: Boolean, // Is entity controlled by player?

    // stats
    var maxHealth: Int = 15,
    var health: Int = 15,
    var attack: Int = 3,
    var defence: Int = 0,

    //progression
    var level: Int = 1,
    var toNextLevel: Int = 50,  // exp to increment level
    var nextLevelGrowth: Double = 0.2,  // multiply with current toNextLevel for next level exp

    var isDead: Boolean = false
) { }
