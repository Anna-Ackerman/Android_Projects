package com.example.converter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment

class LengthConverterFragment : Fragment() {

    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var inputValue: EditText
    private lateinit var resultValue: TextView

    private val units = arrayOf(
        "Сантиметр",
        "Метр",
        "Кілометр",
        "Дюйм",
        "Миля",
        "Ярд",
        "Фут"
    )

    // Коефіцієнти переведення в метри
    private val toMeters = mapOf(
        "Сантиметр" to 0.01,
        "Метр" to 1.0,
        "Кілометр" to 1000.0,
        "Дюйм" to 0.0254,
        "Миля" to 1609.34,
        "Ярд" to 0.9144,
        "Фут" to 0.3048
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_converter, container, false)

        view.findViewById<TextView>(R.id.converter_title).text = "Конвертер довжини"

        spinnerFrom = view.findViewById(R.id.spinner_from)
        spinnerTo = view.findViewById(R.id.spinner_to)
        inputValue = view.findViewById(R.id.input_value)
        resultValue = view.findViewById(R.id.result_value)

        setupSpinners()
        setupInputListener()

        return view
    }

    private fun setupSpinners() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerFrom.adapter = adapter
        spinnerTo.adapter = adapter
        spinnerTo.setSelection(1) // За замовчуванням вибираємо інший елемент

        val listener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                convert()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerFrom.onItemSelectedListener = listener
        spinnerTo.onItemSelectedListener = listener
    }

    private fun setupInputListener() {
        inputValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                convert()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun convert() {
        val input = inputValue.text.toString()
        if (input.isEmpty() || input == "." || input == "-") {
            resultValue.text = "0"
            return
        }

        try {
            val value = input.toDouble()
            val fromUnit = spinnerFrom.selectedItem.toString()
            val toUnit = spinnerTo.selectedItem.toString()

            // Конвертуємо через метри
            val inMeters = value * (toMeters[fromUnit] ?: 1.0)
            val result = inMeters / (toMeters[toUnit] ?: 1.0)

            resultValue.text = String.format("%.4f", result)
        } catch (e: NumberFormatException) {
            resultValue.text = "Помилка"
        }
    }
}