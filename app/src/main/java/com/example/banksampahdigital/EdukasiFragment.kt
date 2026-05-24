package com.example.banksampahdigital

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class EdukasiFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke fragment_edukasi.xml
        val view = inflater.inflate(R.layout.fragment_edukasi, container, false)

        // 1. Logika Klik Card Sampah Organik
        view.findViewById<CardView>(R.id.btn_organik)?.setOnClickListener {
            pindahKeDetail("organik", "Sampah Organik")
        }

        // 2. Logika Klik Card Sampah Logam
        view.findViewById<CardView>(R.id.btn_logam)?.setOnClickListener {
            pindahKeDetail("logam", "Sampah Logam")
        }

        // 3. Logika Klik Card Sampah Tekstil
        view.findViewById<CardView>(R.id.btn_Tekstil)?.setOnClickListener {
            pindahKeDetail("tekstil", "Sampah Tekstil")
        }

        // 4. Logika Klik Card Sampah B3
        view.findViewById<CardView>(R.id.btn_B3)?.setOnClickListener {
            pindahKeDetail("b3", "Sampah B3 (Berbahaya)")
        }

        // 5. Logika Klik Card Sampah Kaca
        view.findViewById<CardView>(R.id.btn_kaca)?.setOnClickListener {
            pindahKeDetail("kaca", "Sampah Kaca")
        }

        // 6. Logika Klik Card Sampah Elektronik
        view.findViewById<CardView>(R.id.btn_elektronik)?.setOnClickListener {
            pindahKeDetail("elektronik", "Sampah Elektronik")
        }

        // 7. Logika Klik Card Sampah Anorganik
        view.findViewById<CardView>(R.id.btn_Anorganik)?.setOnClickListener {
            pindahKeDetail("anorganik", "Sampah Anorganik")
        }

        // 8. Logika Klik Card Sampah Kertas
        view.findViewById<CardView>(R.id.btn_kertas)?.setOnClickListener {
            pindahKeDetail("kertas", "Sampah Kertas")
        }

        return view
    }

    // FUNGSI BANTUAN: Biar tidak buat transaksi berulang-ulang sampai 8 kali
    private fun pindahKeDetail(idKategori: String, namaKategori: String) {
        // Menggunakan Intent untuk membuka DetailEdukasiActivity
        val intent = Intent(requireContext(), DetailEdukasiActivity::class.java)

        // Titipkan data kategori ke dalam intent
        intent.putExtra("EXTRA_ID_KATEGORI", idKategori)
        intent.putExtra("EXTRA_NAMA_KATEGORI", namaKategori)

        // Jalankan Activity
        startActivity(intent)
    }
}