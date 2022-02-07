package com.example.minesweeper

import android.content.Context
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.minesweeper.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var vib: Vibrator
    private val buttons = Array(4) { arrayOfNulls<LottieAnimationView>(4) }
    private val texts = Array(4) { arrayOfNulls<TextView>(4) }

    private var board = arrayOf(
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
        vib = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        randomizeBoard()
        setTiles()
    }

    private fun randomizeBoard() {
        for (i in 0..3) {
            for (j in 0..3) {
                if (bombs < 10) {
                    if ((0..3).random() == 1) {
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
                buttons[i][j] = button
                texts[i][j] = text

                if (board[i][j] != -1) {
                    if (board[i][j] == 0) {
                        text.text = ""
                    } else {
                        text.text = board[i][j].toString()
                    }
                }

                button.tag = 0
                button.setOnClickListener {
                    if (button.tag == 0) {
                        buttons[i][j]?.speed = 1f
                        button.playAnimation()
                    }
                    button.tag = 1
                    if (board[i][j] == -1) {
                        vibrate()
                        reset()
                    }
                }
                row.addView(tile)
            }
            binding.tableLayout.addView(row)
        }
    }

    private fun reset() {
        for (i in 0..3) {
            for (j in 0..3) {
                if (buttons[i][j]?.tag == 1) {
                    buttons[i][j]?.tag = 0
                    buttons[i][j]?.speed = -1f
                    buttons[i][j]?.playAnimation()
                }
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            board = arrayOf(
                arrayOf(0, 0, 0, 0),
                arrayOf(0, 0, 0, 0),
                arrayOf(0, 0, 0, 0),
                arrayOf(0, 0, 0, 0)
            )
            bombs = 0
            randomizeBoard()
            for (i in 0..3) {
                for (j in 0..3) {
                    if (board[i][j] != -1) {
                        if (board[i][j] == 0) {
                            texts[i][j]?.text = ""
                        } else {
                            texts[i][j]?.text = board[i][j].toString()
                        }
                    } else {
                        texts[i][j]?.text = resources.getString(R.string.bomb)
                    }
                }
            }
        }, 500)
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vib.vibrate(100);
        }
    }
}