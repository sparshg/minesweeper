package com.example.minesweeper

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
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
    private val timer = Handler(Looper.getMainLooper())
    private var score = 0
    private var high = 0
    private val rows = 8
    private val cols = 7
    private val buttons = Array(rows) { arrayOfNulls<LottieAnimationView>(cols) }
    private val texts = Array(rows) { arrayOfNulls<TextView>(rows) }

    private var board = Array(rows) { IntArray(cols) }
    private var bombs = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        vib = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        binding.github.setOnClickListener {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/sparshg/minesweeper")
                )
            )
        }
        randomizeBoard()
        setTiles()
        startTimer()
    }

    override fun onResume() {
        super.onResume()
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        high = sh.getInt("high", 10000)
        if (high != 10000) {
            binding.highscore.text = high.toString()
        }
    }

    override fun onPause() {
        super.onPause()

        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val myEdit = sharedPreferences.edit()
        myEdit.putInt("high", high)
        myEdit.apply()
    }

    private fun randomizeBoard() {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (bombs < 9) {
                    if ((0..9).random() == 1) {
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
        for (i in 0 until rows) {
            val row = TableRow(this)
            row.layoutParams = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            for (j in 0 until cols) {
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
                if (isDarkModeOn()) {
                    button.setAnimation(R.raw.dark)
                } else {
                    button.setAnimation(R.raw.light)
                }
                button.setOnClickListener {
                    if (button.tag == 0) {
                        buttons[i][j]?.speed = 1f
                        detectRipple(i, j, i, j)
                        button.tag = 1
                        button.playAnimation()
                    }
                    if (board[i][j] == -1) {
                        vibrate()
                        stopTimer()
                        reset()
                    } else if (detectWin()) {
                        stopTimer()
                        if (score < high) {
                            high = score
                            binding.highscore.text = high.toString()
                        }
                        reset()
                    }
                }
                row.addView(tile)
            }
            binding.tableLayout.addView(row)
        }
    }

    private fun detectWin(): Boolean {
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (board[i][j] != -1 && buttons[i][j]?.tag == 0) {
                    return false
                }
            }
        }
        return true
    }


    private fun detectRipple(originR: Int, originC: Int, r: Int, c: Int) {
        buttons[r][c]?.tag = 1
        buttons[r][c]?.speed = 1f
        buttons[r][c]?.playAnimation()
        if (board[r][c] == 0) {
            if (r != rows - 1 && r >= originR) {
                detectRipple(originR, originC, r + 1, c)
            }
            if (r != 0 && r <= originR) {
                detectRipple(originR, originC, r - 1, c)
            }
            if (c != cols - 1 && c >= originC) {
                detectRipple(originR, originC, r, c + 1)
            }
            if (c != 0 && c <= originC) {
                detectRipple(originR, originC, r, c - 1)
            }
        }

    }


    private fun reset() {
        score = 0
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                if (buttons[i][j]?.tag == 1) {
                    buttons[i][j]?.tag = 0
                    buttons[i][j]?.speed = -1f
                    buttons[i][j]?.playAnimation()
                }
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            board = Array(rows) { IntArray(cols) }
            bombs = 0
            randomizeBoard()
            for (i in 0 until rows) {
                for (j in 0 until cols) {
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
            startTimer()
        }, 500)
    }

    private fun startTimer() {
        if (score == 0) {
            timer.removeCallbacks(updateScore)
            timer.postDelayed(updateScore, 1000)
        }
    }

    private fun stopTimer() {
        timer.removeCallbacks(updateScore)
    }

    private val updateScore: Runnable = object : Runnable {
        override fun run() {
            val currentMilliseconds = System.currentTimeMillis()
            score++
            binding.score.text = score.toString()
            timer.postAtTime(this, currentMilliseconds)
            timer.postDelayed(this, 1000)
        }
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vib.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vib.vibrate(100);
        }
    }

    private fun isDarkModeOn(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}