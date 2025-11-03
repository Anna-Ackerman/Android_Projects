package com.example.converter

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var conversionThread: ConversionThread? = null

    companion object {
        var threadHandler: Handler? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ініціалізуємо Thread з Looper
        initConversionThread()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Завантажуємо головний фрагмент при старті
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_length -> LengthConverterFragment()
                R.id.nav_weight -> WeightConverterFragment()
                R.id.nav_temperature -> TemperatureConverterFragment()
                else -> null
            }

            fragment?.let {
                loadFragment(it)
                true
            } ?: false
        }
    }

    private fun initConversionThread() {
        try {
            // Завантажуємо коефіцієнти
            val json = assets.open("coefficients.json").bufferedReader().use { it.readText() }
            val coefficients = JSONObject(json)

            // Створюємо та запускаємо Thread
            conversionThread = ConversionThread(coefficients)
            conversionThread?.start()

            // Отримуємо Handler для відправки повідомлень
            threadHandler = conversionThread?.getHandler()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Зупиняємо Thread
        conversionThread?.quitThread()
        threadHandler = null
    }
}