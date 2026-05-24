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
        // 2. Gunakan setContentView seperti Activity biasa
        setContentView(R.layout.fragment_detail_edukasi)

        // Inisialisasi Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Inisialisasi Komponen (Tanpa embel-embel "view.")
        tvJudulKategori = findViewById(R.id.tvJudulKategori)
        tvHargaFlatKategori = findViewById(R.id.tvHargaFlatKategori)
        rvSampahEdukasi = findViewById(R.id.rvSampahEdukasi)

        // 3. Mengambil data kiriman dari Intent (bukan arguments lagi)
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
        db.collection("edukasi").document(idKategori).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val hargaFlat = documentSnapshot.getLong("hargaKategori")?.toInt() ?: 0
                    tvHargaFlatKategori.text = "Rp $hargaFlat/kg"
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