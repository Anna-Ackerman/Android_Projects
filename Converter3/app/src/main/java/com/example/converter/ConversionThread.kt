package com.example.converter

import android.os.Handler
import android.os.Looper
import android.os.Message
import org.json.JSONObject

class ConversionThread(private val coefficients: JSONObject) : Thread() {

    private lateinit var handler: Handler
    private lateinit var looper: Looper

    companion object {
        const val MSG_CONVERT_LENGTH = 1
        const val MSG_CONVERT_WEIGHT = 2
        const val MSG_CONVERT_TEMPERATURE = 3
    }

    override fun run() {
        Looper.prepare()
        looper = Looper.myLooper()!!

        handler = Handler(looper) { msg ->
            when (msg.what) {
                MSG_CONVERT_LENGTH -> {
                    val data = msg.obj as ConversionData
                    val result = convertLength(data.value, data.fromUnit, data.toUnit)
                    data.callback?.invoke(result)
                    true
                }
                MSG_CONVERT_WEIGHT -> {
                    val data = msg.obj as ConversionData
                    val result = convertWeight(data.value, data.fromUnit, data.toUnit)
                    data.callback?.invoke(result)
                    true
                }
                MSG_CONVERT_TEMPERATURE -> {
                    val data = msg.obj as ConversionData
                    val result = convertTemperature(data.value, data.fromUnit, data.toUnit)
                    data.callback?.invoke(result)
                    true
                }
                else -> false
            }
        }

        Looper.loop()
    }

    fun getHandler(): Handler {
        // Чекаємо поки handler буде готовий
        while (!::handler.isInitialized) {
            Thread.sleep(10)
        }
        return handler
    }

    fun quitThread() {
        looper.quit()
    }

    // Конвертація довжини
    private fun convertLength(value: Double, fromUnit: String, toUnit: String): Double {
        return convert(value, fromUnit, toUnit, "length")
    }

    // Конвертація ваги
    private fun convertWeight(value: Double, fromUnit: String, toUnit: String): Double {
        return convert(value, fromUnit, toUnit, "weight")
    }

    // Конвертація температури
    private fun convertTemperature(value: Double, fromUnit: String, toUnit: String): Double {
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
        return try {
            val categoryCoeffs = coefficients.getJSONObject(category)
            val fromCoeff = categoryCoeffs.getDouble(fromUnit)
            val toCoeff = categoryCoeffs.getDouble(toUnit)

            val inBaseUnit = value * fromCoeff
            inBaseUnit / toCoeff
        } catch (e: Exception) {
            e.printStackTrace()
            0.0
        }
    }

    // Клас для передачі даних
    data class ConversionData(
        val value: Double,
        val fromUnit: String,
        val toUnit: String,
        val callback: ((Double) -> Unit)?
    )
}