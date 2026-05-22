package com.example.banksampahdigital

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DetailPenjemputanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hubungkan ke file XML layout_tugas_penjemputan yang sudah ada di folder layout
        setContentView(R.layout.layout_tugas_penjemputan)

        // 1. Inisialisasi semua komponen teks dan tombol dari XML
        val tvNamaWarga = findViewById<TextView>(R.id.tvNamaWarga)
        val tvAlamat = findViewById<TextView>(R.id.tvAlamat)
        val tvDetailSampah = findViewById<TextView>(R.id.tvDetailSampah)
        val btnBukaPeta = findViewById<Button>(R.id.btnBukaPeta)
        val btnSelesaiAngkut = findViewById<Button>(R.id.btnSelesaiAngkut)

        // 2. Tangkap data yang dititipkan oleh TugasAdapter saat kartu diklik
        val namaWarga = intent.getStringExtra("NAMA_WARGA")
        val alamatWarga = intent.getStringExtra("ALAMAT_WARGA")
        val detailSampah = intent.getStringExtra("DETAIL_SAMPAH")

        // 3. Tampilkan data tersebut ke layar HP Kurir
        tvNamaWarga.text = "Warga: $namaWarga"
        tvAlamat.text = alamatWarga
        tvDetailSampah.text = detailSampah

        // 4. Logika ketika tombol "BUKA NAVIGASI PETA" diklik
        btnBukaPeta.setOnClickListener {
            // Di sini nanti bisa kamu kembangkan untuk melempar koordinat ke Google Maps asli
            Toast.makeText(this, "Membuka rute navigasi menuju rumah $namaWarga", Toast.LENGTH_SHORT).show()
        }

        // 5. Logika ketika tombol "SELESAI ANGKUT SAMPAH" diklik
        btnSelesaiAngkut.setOnClickListener {
            Toast.makeText(this, "Tugas penjemputan berhasil diselesaikan!", Toast.LENGTH_SHORT).show()
            // Menutup halaman detail dan kembali ke daftar antrean dashboard pengangkut
            finish()
        }
    }
}