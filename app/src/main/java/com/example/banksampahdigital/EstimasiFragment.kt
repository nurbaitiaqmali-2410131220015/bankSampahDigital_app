package com.example.banksampahdigital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class EstimasiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke fragment_estimasi.xml
        val view = inflater.inflate(R.layout.fragment_estimasi, container, false)

        // Logika Tombol Hitung Total Harga
        val btnHitung = view.findViewById<Button>(R.id.btnHitung)
        btnHitung.setOnClickListener {
            Toast.makeText(context, "Klik Button Hitung Total Harga", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}