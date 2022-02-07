package com.example.tictactoe

import android.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.setPadding
import com.airbnb.lottie.LottieAnimationView
import com.example.tictactoe.databinding.ActivityMainBinding

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
//                val b = tile.findViewById<TextView>(R.id.tileText)
//                b.setOnClickListener {
//                    b.playAnimation()
//                }
                row.addView(tile)
            }
            binding.tableLayout.addView(row)
        }
    }
}