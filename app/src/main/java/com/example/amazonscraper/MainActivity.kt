package com.example.amazonscraper

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.amazonscraper.databinding.ActivityMainBinding
import org.jsoup.Jsoup
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            hideKeyboard()
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())

            executor.execute {
                try {
                    val doc = Jsoup.connect("https://www.amazon.in/s?k=${binding.query.text}")
                        .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.38 Safari/537.36")
                        .get()
                    val first =
                        doc.select("div.s-result-item.s-asin.sg-col-0-of-12.sg-col-16-of-20.sg-col.s-widget-spacing-small.sg-col-12-of-16")
                            .not(".AdHolder")[0]
                    val label = first.select("span.a-size-medium.a-color-base.a-text-normal").text()
                    val priceSym = first.select("span.a-price-symbol").text()
                    val price = first.select("span.a-price-whole").text()
                    val src = first.select("img.s-image").attr("src")
                    val connection: HttpURLConnection = URL(src)
                        .openConnection() as HttpURLConnection
                    connection.doInput = true
                    connection.connect()
                    val input: InputStream = connection.inputStream
                    val bitmap = BitmapFactory.decodeStream(input)
                    handler.post {
                        binding.label.text = label
                        binding.image.setImageBitmap(bitmap)
                        binding.price.text = priceSym + price
                    }
                } catch (e: IndexOutOfBoundsException) {
                    handler.post {
                        binding.label.text = "Item not found"
                        binding.image.setImageResource(android.R.color.transparent)
                        binding.price.text = ""
                    }
                }
            }
        }
//        val doc = Jsoup.connect("https://wikipedia.org/wiki/Goldilocks_principle").get()
//        GlobalScope.launch(Dispatchers.Main) {
//            scrapePage()
//            val res = doc.select("div.s-result-item.s-asin.sg-col-0-of-12.sg-col-16-of-20.sg-col.s-widget-spacing-small.sg-col-12-of-16")
//            val t = res.select("span.a-size-medium.a-color-base.a-text-normal").text()
//            binding.test.text = t
//        }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}