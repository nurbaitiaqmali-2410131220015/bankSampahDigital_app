package com.example.banksampahdigital

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    // Deklarasi TextView untuk Judul Header dinamis
    private lateinit var tvHeaderTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Membuat tampilan penuh layar (smooth)
        setContentView(R.layout.activity_main)

        // Inisialisasi komponen view dari activity_main.xml
        // 1. Inisialisasi Judul Header
        tvHeaderTitle = findViewById(R.id.tv_header_title)

// 2. Ambil view dari tag <include android:id="@+id/bottom_nav" ... />
        val bottomNavLayout = findViewById<LinearLayout>(R.id.bottom_nav)

// 3. Ambil ID menu di dalam layout include tersebut secara spesifik
        val navLokasi = bottomNavLayout.findViewById<LinearLayout>(R.id.nav_lokasi)
        val navTracking = bottomNavLayout.findViewById<LinearLayout>(R.id.nav_tracking)
        val navEdukasi = bottomNavLayout.findViewById<LinearLayout>(R.id.nav_edukasi)
        val navHarga = bottomNavLayout.findViewById<LinearLayout>(R.id.nav_harga)
        val navEstimasi = bottomNavLayout.findViewById<LinearLayout>(R.id.nav_estimasi)
        // Munculkan LokasiFragment pertama kali saat aplikasi dibuka
        if (savedInstanceState == null) {
            moveFragment(LokasiFragment(), "Lokasi")
        }

        // --- LOGIKA KLIK BOTTOM NAVIGATION BAR ---

        navLokasi.setOnClickListener {
            moveFragment(LokasiFragment(), "Lokasi")
        }

        navTracking.setOnClickListener {
            moveFragment(TrackingFragment(), "Tracking")
        }

        navEdukasi.setOnClickListener {
            moveFragment(EdukasiFragment(), "Edukasi")
        }

        navHarga.setOnClickListener {
            moveFragment(HargaFragment(), "Harga")
        }

        navEstimasi.setOnClickListener {
            moveFragment(EstimasiFragment(), "Estimasi")
        }
    }

    /**
     * Fungsi khusus untuk menukar fragment di bagian tengah layar (fragment_container)
     * Dilengkapi dengan animasi memudar (fade) agar transisinya super smooth.
     */
    private fun moveFragment(fragment: Fragment, title: String) {
        // Mengubah text judul di header sesuai halaman aktif
        tvHeaderTitle.text = title

        supportFragmentManager.beginTransaction()
            // BARIS ANIMASI DIBAWAH INI DIHAPUS/DI-KOMENTAR:
            // .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)

            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}