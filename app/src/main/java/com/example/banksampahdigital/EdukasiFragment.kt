package com.example.banksampahdigital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class EdukasiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke fragment_edukasi.xml
        val view = inflater.inflate(R.layout.fragment_edukasi, container, false)

        // Logika Klik Card Sampah Organik
        val btnOrganik = view.findViewById<CardView>(R.id.btn_organik)
        btnOrganik?.setOnClickListener {
            Toast.makeText(context, "Klik Button Sampah Organik", Toast.LENGTH_SHORT).show()
        }

        // Logika Klik Card Sampah Logam
        val btnLogam = view.findViewById<CardView>(R.id.btn_logam)
        btnLogam?.setOnClickListener {
            Toast.makeText(context, "Klik Button Sampah Logam", Toast.LENGTH_SHORT).show()
        }

        val btnTekstil = view.findViewById<CardView>(R.id.btn_Tekstil)
        btnTekstil?.setOnClickListener {
            Toast.makeText(context, "Klik Button Sampah Tekstil", Toast.LENGTH_SHORT).show()
        }

        // Logika Klik Card Sampah Logam
        val btnB3 = view.findViewById<CardView>(R.id.btn_B3)
        btnB3?.setOnClickListener {
            Toast.makeText(context, "Klik Button Sampah B3", Toast.LENGTH_SHORT).show()
        }

        val btnKaca = view.findViewById<CardView>(R.id.btn_kaca)
        btnKaca?.setOnClickListener {
            Toast.makeText(context, "Klik Button Sampah Kaca", Toast.LENGTH_SHORT).show()
        }

        // Logika Klik Card Sampah Logam
        val btnElektronik = view.findViewById<CardView>(R.id.btn_elektronik)
        btnElektronik?.setOnClickListener {
            Toast.makeText(context, "Klik Button Sampah Elektronik", Toast.LENGTH_SHORT).show()
        }

        val btnAnorganik = view.findViewById<CardView>(R.id.btn_Anorganik)
        btnAnorganik?.setOnClickListener {
            Toast.makeText(context, "Klik Button Sampah Anorganik", Toast.LENGTH_SHORT).show()
        }

        // Logika Klik Card Sampah Logam
        val btnKertas = view.findViewById<CardView>(R.id.btn_kertas)
        btnKertas?.setOnClickListener {
            Toast.makeText(context, "Klik Button Sampah Kertas", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}