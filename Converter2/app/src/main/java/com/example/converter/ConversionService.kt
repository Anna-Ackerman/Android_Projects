package com.example.converter

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import org.json.JSONObject
import java.io.IOException

class ConversionService : Service() {

    private val binder = ConversionBinder()
    private var coefficients: JSONObject? = null

    inner class ConversionBinder : Binder() {
        fun getService(): ConversionService = this@ConversionService
    }

    override fun onBind(intent: Intent?): IBinder {
        loadCoefficients()
        return binder
    }

    private fun loadCoefficients() {
        try {
            val json = assets.open("coefficients.json").bufferedReader().use { it.readText() }
            coefficients = JSONObject(json)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Конвертація довжини
    fun convertLength(value: Double, fromUnit: String, toUnit: String): Double {
        return convert(value, fromUnit, toUnit, "length")
    }

    // Конвертація ваги
    fun convertWeight(value: Double, fromUnit: String, toUnit: String): Double {
        return convert(value, fromUnit, toUnit, "weight")
    }

    // Конвертація температури
    fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double {
        return when {
            fromUnit == toUnit -> value
            fromUnit == "Кельвін" && toUnit == "Цельсій" -> value - 273.15
            fromUnit == "Кельвін" && toUnit == "Фаренгейт" -> (value - 273.15) * 9/5 + 32
            fromUnit == "Цельсій" && toUnit == "Кельвін" -> value + 273.15
            fromUnit == "Цельсій" && toUnit == "Фаренгейт" -> value * 9/5 + 32
            fromUnit == "Фаренгейт" && toUnit == "Кельвін" -> (value - 32) * 5/9 + 273.15
            fromUnit == "Фаренгейт" && toUnit == "Цельсій" -> (value - 32) * 5/9
            else -> value
        }
    }

    // Загальна конвертація через базову одиницю
    private fun convert(value: Double, fromUnit: String, toUnit: String, category: String): Double {
        if (coefficients == null) return 0.0

        try {
            val categoryCoeffs = coefficients!!.getJSONObject(category)
            val fromCoeff = categoryCoeffs.getDouble(fromUnit)
            val toCoeff = categoryCoeffs.getDouble(toUnit)

            // Конвертуємо через базову одиницю
            val inBaseUnit = value * fromCoeff
            return inBaseUnit / toCoeff
        } catch (e: Exception) {
            e.printStackTrace()
            return 0.0
        }
    }
}
