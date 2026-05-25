package com.example.banksampahdigital

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    // 1. Tentukan total halaman fragment (Sekarang menjadi 4)
    override fun getItemCount(): Int {
        return 4
    }

    // 2. Tentukan urutan fragment yang akan muncul berdasarkan indeks posisi (0-3)
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DashboardWargaFragment() // Halaman pertama (Indeks 0)
            1 -> LokasiFragment()         // Halaman kedua (Indeks 1)
            2 -> EdukasiFragment()        // Halaman ketiga (Indeks 2)
            3 -> EstimasiFragment()       // Halaman keempat (Indeks 3)
            else -> DashboardWargaFragment()
        }
    }
}