package com.example.converter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Запускаємо Service
        val serviceIntent = Intent(this, ConversionService::class.java)
        startService(serviceIntent)

        // Налаштовуємо кнопки
        findViewById<Button>(R.id.btn_length).setOnClickListener {
            startActivity(Intent(this, LengthConverterActivity::class.java))
        }

        findViewById<Button>(R.id.btn_weight).setOnClickListener {
            startActivity(Intent(this, WeightConverterActivity::class.java))
        }

        findViewById<Button>(R.id.btn_temperature).setOnClickListener {
            startActivity(Intent(this, TemperatureConverterActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, ConversionService::class.java))
    }
}