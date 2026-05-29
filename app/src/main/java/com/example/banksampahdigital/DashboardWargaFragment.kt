package com.example.banksampahdigital

import android.content.Context
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

class DashboardWargaFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    // TAMBAHAN: Inisialisasi variabel untuk RecyclerView Riwayat Aktivitas
    private lateinit var rvAktivitasTerakhir: RecyclerView
    private lateinit var aktivitasAdapter: AktivitasAdapter
    private val listTransaksi = ArrayList<TransaksiModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke layout XML asli milikmu yang sudah diperbaiki
        val view = inflater.inflate(R.layout.activity_dashboard_warga, container, false)

        // Inisialisasi komponen UI sesuai ID XML baru
        val tvSalamWarga = view.findViewById<TextView>(R.id.tvSalamWarga)
        val tvTotalSaldo = view.findViewById<TextView>(R.id.tvTotalSaldo)
        val tvTotalPoin = view.findViewById<TextView>(R.id.tvTotalPoin)

        // TAMBAHAN: Hubungkan RecyclerView ke ID yang ada di activity_dashboard_warga.xml
        rvAktivitasTerakhir = view.findViewById(R.id.rvAktivitasTerakhir) // Sesuaikan ID RecyclerView di XML-mu

        // TAMBAHAN: Set konfigurasi susunan RecyclerView agar muncul memanjang ke bawah
        rvAktivitasTerakhir.layoutManager = LinearLayoutManager(context)
        aktivitasAdapter = AktivitasAdapter(listTransaksi)
        rvAktivitasTerakhir.adapter = aktivitasAdapter

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

            db.collection("transaksi")
                .whereEqualTo("emailWarga", emailLogin)
                // UPDATE: Kita hapus filter status "selesai" di sini agar semua transaksi (baik "menunggu" maupun "selesai") ikut ditarik ke dalam list riwayat
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    var totalSaldoWarga = 0

                    if (snapshots != null) {
                        listTransaksi.clear() // TAMBAHAN: Bersihkan list lama agar data tidak ganda saat di-refresh

                        for (document in snapshots) {
                            // TAMBAHAN: Konversi dokumen Firestore ke kelas cetakan TransaksiModel
                            val transaksi = document.toObject(TransaksiModel::class.java)
                            listTransaksi.add(transaksi)

                            // Aturan hitung saldo tetap sama: Hanya hitung harga jika statusnya "selesai"
                            if (transaksi.status == "selesai") {
                                totalSaldoWarga += transaksi.totalHarga
                            }
                        }
                    }

                    val totalPoinWarga = totalSaldoWarga / 100

                    // Eksekusi perubahan ke dalam tampilan layar HP
                    tvTotalSaldo.text = "Rp $totalSaldoWarga"
                    tvTotalPoin.text = "Setara dengan: $totalPoinWarga Poin"

                    // TAMBAHAN: Beritahu adapter kalau data baru dari Estimasi sudah masuk, biar layar otomatis ter-update
                    aktivitasAdapter.notifyDataSetChanged()
                }
        }

        return view
    }
}