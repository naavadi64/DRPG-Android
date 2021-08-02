package com.example.dicerollplayinggame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dicerollplayinggame.models.GameEntity
import com.example.dicerollplayinggame.models.GameLogic
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    // classes
    private lateinit var gameLogic: GameLogic
    private lateinit var adapter: LayoutAdapter

    // layouts and views
    private lateinit var clRoot: ConstraintLayout
    private lateinit var rvGameView: RecyclerView
    private lateinit var btMainButton: Button
    private lateinit var ivDice: ImageView
    private lateinit var mtvGameLog: TextView
    private lateinit var tvRollInfo: TextView
    private lateinit var tvPlayerHealth: TextView
    private lateinit var tvEnemyHealth: TextView

    private var inCombat: Boolean = false
    private var diceRoll: Int = 0
    private var enemyRoll: Int = 0 // used to set enemyType

    // cheats
    private var playerGodMode: Boolean = false // player takes no damage
    private var playerInstagib: Boolean = false // player one-shots enemy

    // battle stats
    private var currentPlayerHealth = 0
    private var playerAtkDmg: Int = 0
    private var enemyAtkDmg: Int = 0

    // persistent stats
    private var score: Int = 0
    private var gameOver: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        clRoot = findViewById(R.id.clRoot)
        rvGameView = findViewById(R.id.rvGameView)
        btMainButton = findViewById(R.id.btMainButton)
        ivDice = findViewById(R.id.ivDice)
        mtvGameLog = findViewById(R.id.mtvGameLog)
        tvRollInfo = findViewById(R.id.tvRollInfo)

        // init rvGameView
        adapter = LayoutAdapter(this, 1, enemyRoll, true, object: LayoutAdapter.GameUpdate {
            override fun onGameUpdate(position: Int) {
                //updateGame()
                //Log.i(TAG, "Game Updated")
            }

        })
        rvGameView.adapter = adapter
        rvGameView.setHasFixedSize(true)
        rvGameView.layoutManager = GridLayoutManager(this, 1)

        currentPlayerHealth = 50  // remove hardcoded current health
        gameLogic = GameLogic(0, currentPlayerHealth)

        tvPlayerHealth = findViewById(R.id.tvPlayerHealth)
        tvPlayerHealth.text = "Player HP: ${gameLogic.getPlayerHealth()}/${gameLogic.getPlayerMaxHealth()}"
        tvEnemyHealth = findViewById(R.id.tvEnemyHealth)
        tvEnemyHealth.text = "No Enemy!"

        mtvGameLog.text = ("Roll the dice to find a new encounter!")

        buttonListenerMethod()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {  // Restart game
                gameOver = false
                currentPlayerHealth = 50
                enemyRoll = 0
                score = 0
                inCombat = false
                gameLogic = GameLogic(0, currentPlayerHealth)
                updateGame(true)
                Snackbar.make(clRoot, "Game Refreshed!", Snackbar.LENGTH_LONG).show()
                tvPlayerHealth.text = "Player HP: ${gameLogic.getPlayerHealth()}/${gameLogic.getPlayerMaxHealth()}"
                tvEnemyHealth.text = "No Enemy!"
                mtvGameLog.text = ("Roll the dice to find a new encounter!")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buttonListenerMethod() {

        btMainButton = findViewById(R.id.btMainButton)
        btMainButton.setOnClickListener() {
            diceRoll = (1..6).random()  // Get Random value

            if (!gameOver) {
                when (diceRoll) {  // Set dice image
                    1 -> {ivDice.setImageResource(R.drawable.one)}
                    2 -> {ivDice.setImageResource(R.drawable.two)}
                    3 -> {ivDice.setImageResource(R.drawable.three)}
                    4 -> {ivDice.setImageResource(R.drawable.four)}
                    5 -> {ivDice.setImageResource(R.drawable.five)}
                    6 -> {ivDice.setImageResource(R.drawable.six)}
                    0 -> {ivDice.setImageResource(R.drawable.rolling)}
                }
                tvRollInfo.text = "You rolled a: $diceRoll"

                if (!inCombat) {  // if not in combat, generate new encounter
                    enemyRoll = diceRoll
                    mtvGameLog.text = ("Encountered an enemy! Difficulty Rating: ${enemyRoll}")
                    gameLogic = GameLogic(enemyRoll, currentPlayerHealth)
                    inCombat = true
                    updateGame(true)
                    return@setOnClickListener
                }

                updateGame()
            } else {
                Snackbar.make(clRoot, "Can't roll when you're dead! Final Score: $score", Snackbar.LENGTH_LONG).show()
            }
        }

    }

    private fun updateGame(newEncounter: Boolean = false) {
        if (newEncounter) {
            // update rvGameView
            adapter = LayoutAdapter(this, 1, enemyRoll, !gameLogic.isPlayerDead(), object: LayoutAdapter.GameUpdate {
                override fun onGameUpdate(position: Int) {
                    //updateGame()
                    //Log.i(TAG, "Game Updated")
                }
            })
            rvGameView.adapter = adapter
            tvEnemyHealth.text = "Enemy HP: ${gameLogic.getEnemyHealth()}/${gameLogic.getEnemyMaxHealth()}"  // reset health UI
            return  // skip damage calculation
        }

        // damage calculations
        playerAtkDmg = gameLogic.getPlayerAttack() * diceRoll
        enemyAtkDmg = gameLogic.getEnemyAttack() * (1..6).random()
        mtvGameLog.text = ("Attacked the enemy for ${playerAtkDmg} damage!\n Enemy attacked you for ${enemyAtkDmg} damage!")

        if (playerGodMode) {
            gameLogic.changePlayerHealth(0)
        } else {
            gameLogic.changePlayerHealth(enemyAtkDmg * -1)
            currentPlayerHealth = gameLogic.getPlayerHealth()
        }

        if (playerInstagib) {
            gameLogic.changeEnemyHealth(-1000000)
        } else {
            gameLogic.changeEnemyHealth((playerAtkDmg * -1))
        }

        tvPlayerHealth.text = "Player HP: ${gameLogic.getPlayerHealth()}/${gameLogic.getPlayerMaxHealth()}"
        tvEnemyHealth.text = "Enemy HP: ${gameLogic.getEnemyHealth()}/${gameLogic.getEnemyMaxHealth()}"

        // check status of entities
        when {
            gameLogic.isPlayerDead() -> {
                Snackbar.make(clRoot, "You Died! Final Score: $score", Snackbar.LENGTH_LONG).show()
                gameOver = true
            }
            gameLogic.isEnemyDead() -> {
                var exp = 5 + enemyRoll * 5
                Snackbar.make(clRoot, "Enemy Vanquished! Got $exp EXP!", Snackbar.LENGTH_LONG).show()
                playerProgression(exp)
                var heal = 2 * diceRoll
                gameLogic.changePlayerHealth(heal)
                currentPlayerHealth = gameLogic.getPlayerHealth()
                tvPlayerHealth.text = "Player HP: ${gameLogic.getPlayerHealth()}/${gameLogic.getPlayerMaxHealth()}"
                mtvGameLog.append("\nFound a health potion, healed for ${heal} points!\nRoll the dice to find the next encounter!")
                enemyRoll = 0  // Set to 0 so rvGameView does not show enemy and for next roll
            }
            else -> {
                // No-op
            }
        }

        // update rvGameView
        adapter = LayoutAdapter(this, 1, enemyRoll, !gameLogic.isPlayerDead(), object: LayoutAdapter.GameUpdate {
            override fun onGameUpdate(position: Int) {
                //updateGame()
                //Log.i(TAG, "Game Updated")
            }
        })
        rvGameView.adapter = adapter

        if (enemyRoll == 0) { inCombat = false }  // set inCombat so player can find next encounter
    }

    private fun playerProgression(exp: Int) {
        score += exp
    }

}