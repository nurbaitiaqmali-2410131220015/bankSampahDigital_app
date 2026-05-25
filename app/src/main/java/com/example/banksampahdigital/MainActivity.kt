package com.example.banksampahdigital

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tvHeaderTitle: TextView

    // Deklarasi variabel untuk tombol navigasi custom LinearLayout
    private lateinit var btnDashboard: LinearLayout
    private lateinit var btnTracking: LinearLayout
    private lateinit var btnEdukasi: LinearLayout
    private lateinit var btnEstimasi: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Inisialisasi komponen View dari activity_main.xml
        viewPager = findViewById(R.id.viewPager)
        tvHeaderTitle = findViewById(R.id.tv_header_title)

        // 2. Inisialisasi komponen Tombol Menu dari layout_navigation.xml
        btnDashboard = findViewById(R.id.nav_dashboard)
        btnTracking = findViewById(R.id.nav_lokasi)
        btnEdukasi = findViewById(R.id.nav_edukasi)
        btnEstimasi = findViewById(R.id.nav_estimasi)

        // 3. Memasang adapter ViewPager2 (Menghubungkan ke fragment-fragment)
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // 4. Kunci swipe manual agar Maps di LokasiFragment tidak konflik saat digeser
        viewPager.isUserInputEnabled = false

        // 5. Logika Event Klik Manual pada Tombol Menu Custom (LinearLayout)
        btnDashboard.setOnClickListener {
            viewPager.currentItem = 0
            updateHeaderAndMenu(0)
        }

        btnTracking.setOnClickListener {
            viewPager.currentItem = 1
            updateHeaderAndMenu(1)
        }

        btnEdukasi.setOnClickListener {
            viewPager.currentItem = 2
            updateHeaderAndMenu(2)
        }

        btnEstimasi.setOnClickListener {
            viewPager.currentItem = 3
            updateHeaderAndMenu(3)
        }

        // 6. Sinkronisasi callback jika ada perubahan halaman pada ViewPager2
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateHeaderAndMenu(position)
            }
        })

        // Set tampilan awal saat aplikasi pertama kali dibuka (Default: Dashboard)
        updateHeaderAndMenu(0)
    }

    /**
     * Fungsi untuk mengubah teks Header Title secara dinamis
     * sesuai dengan fragment halaman yang sedang aktif.
     */
    private fun updateHeaderAndMenu(position: Int) {
        when (position) {
            0 -> tvHeaderTitle.text = "Dashboard"
            1 -> tvHeaderTitle.text = "Tracking Lokasi"
            2 -> tvHeaderTitle.text = "Edukasi Sampah"
            3 -> tvHeaderTitle.text = "Estimasi Harga"
        }
    }
}