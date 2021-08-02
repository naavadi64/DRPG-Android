package com.example.dicerollplayinggame

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.graphics.drawable.AnimationDrawable
import androidx.recyclerview.widget.RecyclerView

class LayoutAdapter(private val context: Context,
                    private val numElements: Int,
                    private val enemyType: Int,
                    private val playerAlive: Boolean,
                    private val gameUpdate: GameUpdate) :
        RecyclerView.Adapter<LayoutAdapter.ViewHolder>() {

    companion object {
        private const val TAG = "LayoutAdapter"
    }

    interface GameUpdate {
        fun onGameUpdate(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LayoutAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.scene, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = numElements

    override fun onBindViewHolder(holder: LayoutAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivBackground = itemView.findViewById<ImageView>(R.id.ivBackground)
        private val givPlayerChar = itemView.findViewById<ImageView>(R.id.givPlayerChar)
        private val givEnemyChar = itemView.findViewById<ImageView>(R.id.givEnemyChar)

        fun bind(position: Int) {
            if (playerAlive) {
                givPlayerChar.visibility = View.VISIBLE
            } else {
                givPlayerChar.visibility = View.INVISIBLE
            }


            when (enemyType) {
                1, 2 -> {
                            givEnemyChar.setImageResource(R.drawable.slime_idle)
                            givEnemyChar.visibility = View.VISIBLE
                }
                3, 4 -> {
                            givEnemyChar.setImageResource(R.drawable.snake_idle)
                            givEnemyChar.visibility = View.VISIBLE
                }
                5 -> {
                            givEnemyChar.setImageResource(R.drawable.reptile_idle)
                            givEnemyChar.visibility = View.VISIBLE
                }
                6 -> {
                            givEnemyChar.setImageResource(R.drawable.giant_idle)
                            givEnemyChar.visibility = View.VISIBLE
                }
                else -> {givEnemyChar.setImageResource(R.drawable.ghost_idle)
                        givEnemyChar.visibility = View.INVISIBLE
                        }
            }
            //givEnemyChar.visibility = View.VISIBLE
            //Log.i(TAG, "Player Status is: ${givPlayerChar.visibility}")
            //Log.i(TAG, "Enemy Status is: ${givEnemyChar.visibility}")
        }
    }

}
