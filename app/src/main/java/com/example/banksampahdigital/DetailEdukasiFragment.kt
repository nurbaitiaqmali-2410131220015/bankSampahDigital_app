package com.example.banksampahdigital

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DetailEdukasiFragment : Fragment() {

    private lateinit var rvSampahEdukasi: RecyclerView
    private lateinit var tvJudulKategori: TextView
    private lateinit var tvHargaFlatKategori: TextView // DIGANTI/DITAMBAH: Variabel baru untuk teks harga
    private val listSampah = ArrayList<SampahEdukasi>()
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: SampahEdukasiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Menghubungkan fragment dengan layout XML asli milikmu
        val view = inflater.inflate(R.layout.fragment_detail_edukasi, container, false)

        // Inisialisasi Firebase Firestore
        db = FirebaseFirestore.getInstance()

        // Inisialisasi Komponen dari layout aslimu
        tvJudulKategori = view.findViewById(R.id.tvJudulKategori)
        tvHargaFlatKategori = view.findViewById(R.id.tvHargaFlatKategori) // DIGANTI/DITAMBAH: Hubungkan ke ID XML harga yang baru
        rvSampahEdukasi = view.findViewById(R.id.rvSampahEdukasi)

        // Mengambil data kiriman (ID & Nama Kategori) dari EdukasiFragment
        val idKategori = arguments?.getString("EXTRA_ID_KATEGORI") ?: "organik"
        val namaKategori = arguments?.getString("EXTRA_NAMA_KATEGORI") ?: "Sampah Organik"

        // 1. SET JUDUL DI DALAM HALAMAN (Menggunakan ID asli dari XML-mu)
        tvJudulKategori.text = namaKategori

        // 2. KUNCI HEADER UTAMA MainActivity agar selalu konsisten bertuliskan "Edukasi"
        activity?.findViewById<TextView>(R.id.tv_header_title)?.text = "Edukasi"

        // Setup RecyclerView secara aman
        rvSampahEdukasi.layoutManager = LinearLayoutManager(requireContext())
        adapter = SampahEdukasiAdapter(listSampah)
        rvSampahEdukasi.adapter = adapter

        // KUNCI UTAMA: Mematikan scroll internal RecyclerView agar patuh pada ScrollView milik activity_main
        rvSampahEdukasi.isNestedScrollingEnabled = false

        // DIGANTI: Memanggil fungsi baru yang bertahap (ambil harga dulu, baru ambil list sampah)
        ambilDataKategoriDanDaftarSampah(idKategori)

        return view
    }

    // DIGANTI: Fungsi penarikan data baru yang mendukung pembacaan harga kategori flat
    private fun ambilDataKategoriDanDaftarSampah(idKategori: String) {
        // TAHAP 1: Ambil data dokumen utama kategori untuk mendapatkan field hargaKategori
        db.collection("edukasi").document(idKategori).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Ambil angka hargaKategori dari Firestore, jika kosong set ke 0
                    val hargaFlat = documentSnapshot.getLong("hargaKategori")?.toInt() ?: 0

                    // Tempel teks harga tepat di samping judul
                    tvHargaFlatKategori.text = "Rp $hargaFlat/kg"
                }

                // TAHAP 2: Setelah harga berhasil diambil, baru load sub-collection daftar_sampah di bawahnya
                ambilDaftarSampah(idKategori)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Gagal memuat harga: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // DITAMBAH/DIPISAH: Fungsi khusus untuk menarik sub-collection daftar_sampah
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
                Toast.makeText(requireContext(), "Gagal memuat list data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}