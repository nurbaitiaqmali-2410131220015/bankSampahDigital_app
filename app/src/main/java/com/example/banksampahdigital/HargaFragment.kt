package com.example.banksampahdigital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HargaFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke fragment_harga.xml
        val view = inflater.inflate(R.layout.fragment_harga, container, false)

        // Halaman ini biasanya hanya menampilkan daftar harga (statis atau dari database)
        // Jika ada tombol refresh harga, bisa ditaruh di sini

        return view
    }
}