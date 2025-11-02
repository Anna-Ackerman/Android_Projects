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

class TemperatureConverterFragment : Fragment() {

    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var inputValue: EditText
    private lateinit var resultValue: TextView

    private val units = arrayOf(
        "Кельвін",
        "Цельсій",
        "Фаренгейт"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_converter, container, false)

        view.findViewById<TextView>(R.id.converter_title).text = "Конвертер температури"

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
        spinnerTo.setSelection(1)

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

            // Спочатку конвертуємо в Кельвіни
            val inKelvin = when (fromUnit) {
                "Кельвін" -> value
                "Цельсій" -> value + 273.15
                "Фаренгейт" -> (value - 32) * 5/9 + 273.15
                else -> value
            }

            // Потім з Кельвінів в потрібну одиницю
            val result = when (toUnit) {
                "Кельвін" -> inKelvin
                "Цельсій" -> inKelvin - 273.15
                "Фаренгейт" -> (inKelvin - 273.15) * 9/5 + 32
                else -> inKelvin
            }

            resultValue.text = String.format("%.2f", result)
        } catch (e: NumberFormatException) {
            resultValue.text = "Помилка"
        }
    }
}