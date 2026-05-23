package com.example.banksampahdigital

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    // Total ada 4 halaman (Dashboard, Tracking, Edukasi, Estimasi)
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DashboardWargaFragment()
            1 -> LokasiFragment()
            2 -> EdukasiFragment()
            3 -> EstimasiFragment()
            else -> LokasiFragment()
        }
    }
}