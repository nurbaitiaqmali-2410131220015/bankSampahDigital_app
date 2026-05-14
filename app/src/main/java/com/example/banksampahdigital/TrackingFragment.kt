package com.example.banksampahdigital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class TrackingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke fragment_tracking.xml
        val view = inflater.inflate(R.layout.fragment_tracking, container, false)

        // Logika Tombol Lapor Sampah
        val btnLapor = view.findViewById<Button>(R.id.btnLapor)
        btnLapor.setOnClickListener {
            Toast.makeText(context, "Klik Button Lapor", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}