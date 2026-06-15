package com.example.banksampahdigital

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

// 1. Ganti Fragment() menjadi AppCompatActivity()
class DetailEdukasiActivity : AppCompatActivity() {

    private lateinit var rvSampahEdukasi: RecyclerView
    private lateinit var tvJudulKategori: TextView
    private lateinit var tvHargaFlatKategori: TextView
    private val listSampah = ArrayList<SampahEdukasi>()
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: SampahEdukasiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Menampilkan layout detail edukasi
        setContentView(R.layout.fragment_detail_edukasi)
        db = FirebaseFirestore.getInstance()

        // Menghubungkan komponen XML ke Kotlin
        tvJudulKategori = findViewById(R.id.tvJudulKategori)
        tvHargaFlatKategori = findViewById(R.id.tvHargaFlatKategori)
        rvSampahEdukasi = findViewById(R.id.rvSampahEdukasi)

        // Mengambil data kategori yang dikirim dari EdukasiFragment melalui Intent
        val idKategori = intent.getStringExtra("EXTRA_ID_KATEGORI") ?: "organik"
        val namaKategori = intent.getStringExtra("EXTRA_NAMA_KATEGORI") ?: "Sampah Organik"

        // Set judul di dalam halaman
        tvJudulKategori.text = namaKategori

        // Setup RecyclerView
        rvSampahEdukasi.layoutManager = LinearLayoutManager(this)
        adapter = SampahEdukasiAdapter(listSampah)
        rvSampahEdukasi.adapter = adapter

        rvSampahEdukasi.isNestedScrollingEnabled = false

        // Jalankan fungsi ambil data
        ambilDataKategoriDanDaftarSampah(idKategori)
    }

    private fun ambilDataKategoriDanDaftarSampah(idKategori: String) {
        // Mengambil dokumen kategori berdasarkan idKategori
        db.collection("edukasi").document(idKategori).get()
            .addOnSuccessListener { documentSnapshot ->
                // Kondisi jika dokumen kategori ditemukan di Firestore
                if (documentSnapshot.exists()) {
                    // Kondisi jika kategori organik atau B3, maka harga disembunyikan
                    if (idKategori.equals("organik", ignoreCase = true) || idKategori.equals("b3", ignoreCase = true)) {
                        tvHargaFlatKategori.visibility = android.view.View.GONE
                    } else {
                        // Tampilkan harga normal jika kategori ekonomis (anorganik, logam, dll)
                        tvHargaFlatKategori.visibility = android.view.View.VISIBLE
                        val hargaFlat = documentSnapshot.getLong("hargaKategori")?.toInt() ?: 0
                        tvHargaFlatKategori.text = "Rp $hargaFlat/kg"
                    }
                }
                ambilDaftarSampah(idKategori)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat harga: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun ambilDaftarSampah(idKategori: String) {
        db.collection("edukasi").document(idKategori).collection("daftar_sampah")
            .get()
            .addOnSuccessListener { querySnapshot ->
                listSampah.clear()
                for (document in querySnapshot) {
                    val sampah = document.toObject(SampahEdukasi::class.java)
                    listSampah.add(sampah)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memuat list data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}