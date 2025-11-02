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

class WeightConverterFragment : Fragment() {

    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var inputValue: EditText
    private lateinit var resultValue: TextView

    private val units = arrayOf(
        "Грам",
        "Кілограм",
        "Тона",
        "Карат",
        "Фунт",
        "Пуд"
    )

    // Коефіцієнти переведення в кілограми
    private val toKilograms = mapOf(
        "Грам" to 0.001,
        "Кілограм" to 1.0,
        "Тона" to 1000.0,
        "Карат" to 0.0002,
        "Фунт" to 0.453592,
        "Пуд" to 16.3807
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_converter, container, false)

        view.findViewById<TextView>(R.id.converter_title).text = "Конвертер ваги"

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

            val inKilograms = value * (toKilograms[fromUnit] ?: 1.0)
            val result = inKilograms / (toKilograms[toUnit] ?: 1.0)

            resultValue.text = String.format("%.4f", result)
        } catch (e: NumberFormatException) {
            resultValue.text = "Помилка"
        }
    }
}