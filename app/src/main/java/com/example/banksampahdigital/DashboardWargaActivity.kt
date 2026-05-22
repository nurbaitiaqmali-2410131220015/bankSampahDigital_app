package com.example.banksampahdigital

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.firestore.FirebaseFirestore

class DashboardWargaActivity : AppCompatActivity() {

    // Inisialisasi Firestore untuk mengambil nama warga secara dinamis
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_warga)

        val menuJemput = findViewById<CardView>(R.id.menuJemputSampah)
        val menuLokasi = findViewById<CardView>(R.id.menuLokasiBank)
        val tvSalamWarga = findViewById<TextView>(R.id.tvSalamWarga)

        // =======================================================================
        // 1. LOGIKA AMBIL NAMA DINAMIS DARI FIRESTORE (Menggunakan Session Login)
        // =======================================================================
        val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val emailLogin = sharedPreferences.getString("EMAIL_USER", "")

        if (!emailLogin.isNullOrEmpty()) {
            db.collection("users").document(emailLogin).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val namaWarga = document.getString("nama") ?: "Warga"
                        // Mengubah teks "Halo, Nurbaiti Aqmali!" sesuai nama akun yang login
                        tvSalamWarga.text = "Halo, $namaWarga!"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal memuat profil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // =======================================================================
        // 2. AKSI KLIK TOMBOL: DIARAHKAN KE MAINACTIVITY UNTUK MEMBUKA FRAGMENT
        // =======================================================================

        // Saat menu "Jemput Sampah" diklik (Misal diarahkan ke Estimasi/Form di MainActivity)
        menuJemput.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("BUKA_FRAGMENT", "estimasi") // Kirim instruksi buka fragment estimasi
            startActivity(intent)
        }

        // Saat menu "Cari Bank Sampah" diklik (Membuka LokasiFragment via MainActivity)
        menuLokasi.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("BUKA_FRAGMENT", "lokasi") // Kirim instruksi buka fragment lokasi
            startActivity(intent)
        }
    }
}