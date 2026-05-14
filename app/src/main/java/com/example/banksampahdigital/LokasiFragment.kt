package com.example.banksampahdigital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment

class LokasiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke fragment_lokasi.xml
        val view = inflater.inflate(R.layout.fragment_lokasi, container, false)

        // Jika nanti kamu punya tombol di halaman lokasi, taruh di bawah sini dengan view.findViewById

        return view
    }
}