package com.example.banksampahdigital

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var tvHeaderTitle: TextView
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Tetap mempertahankan tampilan full screen smooth milikmu
        setContentView(R.layout.activity_main)

        // 1. Inisialisasi Judul Header dan ViewPager2
        tvHeaderTitle = findViewById(R.id.tv_header_title)
        viewPager = findViewById(R.id.viewPager)

        // Daftar judul halaman sesuai urutan index ViewPager2
        val titles = arrayOf("Dashboard", "Tracking Lokasi", "Edukasi Sampah", "Estimasi Harga")

        // 2. Set Adapter untuk ViewPager2
        viewPager.adapter = ViewPagerAdapter(this)

        // 3. Logika deteksi halaman (Swipe aktif CUMA di Dashboard)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tvHeaderTitle.text = titles[position]

                // Hanya index 0 (Dashboard) yang isUserInputEnabled-nya TRUE (bisa di-swipe ke kanan)
                // Begitu pindah ke index 1 (Tracking), otomatis jadi FALSE (terkunci)
                viewPager.isUserInputEnabled = (position == 0)
            }
        })

        // 4. Inisialisasi Layout Tombol Navigasi Bawah
        val bottomNavLayout = findViewById<LinearLayout>(R.id.bottom_nav)
        val navLokasi = bottomNavLayout.findViewById<LinearLayout>(R.id.nav_lokasi)
        val navEdukasi = bottomNavLayout.findViewById<LinearLayout>(R.id.nav_edukasi)
        val navEstimasi = bottomNavLayout.findViewById<LinearLayout>(R.id.nav_estimasi)

        // 5. --- LOGIKA KLIK BOTTOM NAVIGATION BAR ---
        // Mengubah currentItem langsung memindahkan halaman ViewPager2 tanpa transaksi fragment manual lagi
        navLokasi.setOnClickListener {
            viewPager.currentItem = 1 // Pindah ke Tracking
        }

        navEdukasi.setOnClickListener {
            viewPager.currentItem = 2 // Pindah ke Edukasi
        }

        navEstimasi.setOnClickListener {
            viewPager.currentItem = 3 // Pindah ke Estimasi
        }
    }
}