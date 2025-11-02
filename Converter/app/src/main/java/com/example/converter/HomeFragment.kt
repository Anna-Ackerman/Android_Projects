package com.example.converter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        view.findViewById<Button>(R.id.btn_length).setOnClickListener {
            loadFragment(LengthConverterFragment())
        }

        view.findViewById<Button>(R.id.btn_weight).setOnClickListener {
            loadFragment(WeightConverterFragment())
        }

        view.findViewById<Button>(R.id.btn_temperature).setOnClickListener {
            loadFragment(TemperatureConverterFragment())
        }

        return view
    }

    private fun loadFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}