package com.example.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TableRow
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.example.minesweeper.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        for (i in 1..4) {
            val row = TableRow(this)
            row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT)
            for (i in 1..4) {
                val tile = LayoutInflater.from(this).inflate(R.layout.tile, null)
                val button = tile.findViewById<LottieAnimationView>(R.id.tileAnim)
                val text = tile.findViewById<TextView>(R.id.tileText)
                button.setOnClickListener {
                    button.playAnimation()
                }
                row.addView(tile)
            }
            binding.tableLayout.addView(row)
        }
    }
}