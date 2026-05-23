package com.example.banksampahdigital

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailPenjemputanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_tugas_penjemputan)

        // 1. Inisialisasi komponen yang dipakai saja
        val tvNamaWarga = findViewById<TextView>(R.id.tvNamaWarga)
        val tvAlamat = findViewById<TextView>(R.id.tvAlamat)
        val tvDetailSampah = findViewById<TextView>(R.id.tvDetailSampah)
        val btnSelesaiAngkut = findViewById<Button>(R.id.btnSelesaiAngkut)

        // 2. Tangkap data dari intent
        val namaWarga = intent.getStringExtra("NAMA_WARGA")
        val alamatWarga = intent.getStringExtra("ALAMAT_WARGA")
        val detailSampah = intent.getStringExtra("DETAIL_SAMPAH")

        // 3. Tampilkan data ke layar
        tvNamaWarga.text = "Warga: $namaWarga"
        tvAlamat.text = alamatWarga
        tvDetailSampah.text = detailSampah

        // 4. Logika tombol selesai saja
        btnSelesaiAngkut.setOnClickListener {
            Toast.makeText(this, "Tugas penjemputan berhasil diselesaikan!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}