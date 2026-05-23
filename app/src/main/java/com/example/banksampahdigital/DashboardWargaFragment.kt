package com.example.banksampahdigital

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

// 1. Ganti AppCompatActivity() menjadi Fragment()
class DashboardWargaFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    // 2. Gunakan onCreateView untuk Fragment, bukan onCreate lagi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke layout XML asli milikmu
        val view = inflater.inflate(R.layout.activity_dashboard_warga, container, false)

        // 3. Tambahkan "view." di depan findViewById karena berada di dalam Fragment
        val tvSalamWarga = view.findViewById<TextView>(R.id.tvSalamWarga)

        // Gunakan requireContext() sebagai pengganti "this" untuk SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val emailLogin = sharedPreferences.getString("EMAIL_USER", "")

        if (!emailLogin.isNullOrEmpty()) {
            db.collection("users").document(emailLogin).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val namaWarga = document.getString("nama") ?: "Warga"
                        tvSalamWarga.text = "Halo, $namaWarga!"
                    }
                }
                .addOnFailureListener { e ->
                    // Gunakan requireContext() untuk Toast di dalam Fragment
                    Toast.makeText(requireContext(), "Gagal memuat profil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }
}