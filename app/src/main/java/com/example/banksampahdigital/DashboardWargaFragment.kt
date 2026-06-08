package com.example.banksampahdigital

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
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

        val tvSalamWarga = view.findViewById<TextView>(R.id.tvSalamWarga)
        val tvTotalSaldo = view.findViewById<TextView>(R.id.tvTotalSaldo)

        // =================================================================
        // BAGIAN PERBAIKAN: LOGIKA TOMBOL LOGOUT AMAN DARI AUTO-LOGOUT
        // =================================================================
        val btnLogout = view.findViewById<ImageButton>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Memastikan fragment masih menempel pada Activity sebelum memicu perpindahan haman
            activity?.let { activityContext ->
                // 1. Hapus session email login dari SharedPreferences
                val sharedPreferences = activityContext.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                // 2. Berpindah dari MainActivity kembali ke LoginActivity
                val intent = Intent(activityContext, LoginActivity::class.java)

                // Flag ini hanya dipicu saat klik manual untuk membersihkan tumpukan halaman belakang
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

                // 3. Tutup Activity utama (MainActivity)
                activityContext.finish()
            }
        }
        // =================================================================

        rvAktivitasTerakhir = view.findViewById(R.id.rvAktivitasTerakhir)
        rvAktivitasTerakhir.layoutManager = LinearLayoutManager(context)
        aktivitasAdapter = AktivitasAdapter(listTransaksi)
        rvAktivitasTerakhir.adapter = aktivitasAdapter

        val sharedPreferences = requireContext().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val emailLogin = sharedPreferences.getString("EMAIL_USER", "")

        if (!emailLogin.isNullOrEmpty()) {

            // 1. Ambil Nama & Saldo secara Real-Time dari Koleksi "users"
            db.collection("users").document(emailLogin)
                .addSnapshotListener { document, error ->
                    if (error != null) return@addSnapshotListener

                    if (document != null && document.exists()) {
                        val namaWarga = document.getString("nama") ?: "Warga"
                        val saldoRiil = document.getLong("saldo") ?: 0L

                        tvSalamWarga.text = "Halo, $namaWarga!"
                        tvTotalSaldo.text = "Rp $saldoRiil"
                    }
                }

            // 2. Pasang Listener Snapshot untuk Mengisi List Riwayat Transaksi Saja
            db.collection("transaksi")
                .whereEqualTo("emailWarga", emailLogin)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) return@addSnapshotListener

                    if (snapshots != null) {
                        listTransaksi.clear()

                        for (document in snapshots) {
                            val transaksi = document.toObject(TransaksiModel::class.java)
                            transaksi.idTransaksi = document.id
                            listTransaksi.add(transaksi)
                        }
                    }

                    aktivitasAdapter.notifyDataSetChanged()
                }
        }

        return view
    }
}