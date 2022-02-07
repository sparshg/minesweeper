package com.example.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TableRow
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.minesweeper.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private val board = arrayOf(
        arrayOf(0, 0, 0, 0),
        arrayOf(0, 0, 0, 0),
        arrayOf(0, 0, 0, 0),
        arrayOf(0, 0, 0, 0)
    )
    private var bombs = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        randomizeBoard()
        setTiles()
    }

    private fun randomizeBoard() {
        for (i in 0..3) {
            for (j in 0..3) {
                if (bombs < 10) {
                    val spin = (0..3).random()
                    if (spin == 1) {
                        bombs++
                        for (u in -1..1) {
                            for (v in -1..1) {
                                try {
                                    if (board[i + u][j + v] != -1) {
                                        board[i + u][j + v]++
                                    }
                                } catch (e: IndexOutOfBoundsException) {
                                }
                            }
                        }
                        board[i][j] = -1
                    }
                }
            }
        }
        if (bombs == 0) {
            bombs++
            board[1][0]++
            board[1][1]++
            board[0][1]++
            board[0][0] = -1
        }
        Log.d("TAG", board.contentDeepToString())
        Log.d("TAG", bombs.toString())
    }

    private fun setTiles() {
        for (i in 0..3) {
            val row = TableRow(this)
            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            for (j in 0..3) {
                val tile = LayoutInflater.from(this).inflate(R.layout.tile, null)
                val button = tile.findViewById<LottieAnimationView>(R.id.tileAnim)
                val text = tile.findViewById<TextView>(R.id.tileText)
                if (board[i][j] != -1) {
                    if (board[i][j] == 0) {
                        text.text = ""
                    } else {
                        text.text = board[i][j].toString()
                    }
                }
                button.setOnClickListener {
                    button.playAnimation()
                }
                row.addView(tile)
            }
            binding.tableLayout.addView(row)
        }
    }
}