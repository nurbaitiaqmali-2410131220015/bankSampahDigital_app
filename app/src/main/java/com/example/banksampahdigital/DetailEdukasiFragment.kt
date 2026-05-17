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
    private lateinit var tvJudulKategori: TextView // Kita panggil lagi variabel judulnya
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

        // Ambil data dari Firebase Firestore
        ambilDataDariFirebase(idKategori)

        return view
    }

    private fun ambilDataDariFirebase(idKategori: String) {
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
                Toast.makeText(requireContext(), "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}