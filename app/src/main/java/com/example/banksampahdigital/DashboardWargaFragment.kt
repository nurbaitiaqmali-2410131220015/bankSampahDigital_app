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

    private lateinit var rvAktivitasTerakhir: RecyclerView
    private lateinit var aktivitasAdapter: AktivitasAdapter
    private val listTransaksi = ArrayList<TransaksiModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_dashboard_warga, container, false)

        // Inisialisasi komponen UI (tvTotalPoin dihapus)
        val tvSalamWarga = view.findViewById<TextView>(R.id.tvSalamWarga)
        val tvTotalSaldo = view.findViewById<TextView>(R.id.tvTotalSaldo)

        // Hubungkan RecyclerView
        rvAktivitasTerakhir = view.findViewById(R.id.rvAktivitasTerakhir)
        rvAktivitasTerakhir.layoutManager = LinearLayoutManager(context)
        aktivitasAdapter = AktivitasAdapter(listTransaksi)
        rvAktivitasTerakhir.adapter = aktivitasAdapter

        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val emailLogin = sharedPreferences.getString("EMAIL_USER", "")

        if (!emailLogin.isNullOrEmpty()) {

            // 1. Ambil Nama untuk Sapaan Profil
            db.collection("users").document(emailLogin).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val namaWarga = document.getString("nama") ?: "Warga"
                        tvSalamWarga.text = "Halo, $namaWarga!"
                    }
                }

            // 2. Pasang Listener Snapshot untuk Real-Time Update Saldo & List
            db.collection("transaksi")
                .whereEqualTo("emailWarga", emailLogin)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }

                    var totalSaldoWarga = 0

                    if (snapshots != null) {
                        listTransaksi.clear()

                        for (document in snapshots) {
                            val transaksi = document.toObject(TransaksiModel::class.java)
                            listTransaksi.add(transaksi)

                            // Hitung saldo khusus transaksi yang "Selesai Diangkut"
                            if (transaksi.status == "Selesai Diangkut") {
                                totalSaldoWarga += transaksi.totalHarga
                            }
                        }
                    }

                    // Tampilkan total saldo saja ke layar HP
                    tvTotalSaldo.text = "Rp $totalSaldoWarga"

                    // Beritahu adapter untuk refresh tampilan halaman
                    aktivitasAdapter.notifyDataSetChanged()
                }
        }

        return view
    }
}