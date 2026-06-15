package com.example.banksampahdigital

import android.content.Context // BARU: Pastikan ini di-import untuk SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DashboardPengangkutActivity : AppCompatActivity() {

    private lateinit var rvDaftarTugas: RecyclerView
    private lateinit var tugasAdapter: TugasAdapter
    private var daftarTugasList = ArrayList<TugasModel>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_pengangkut)

        // Menghubungkan tombol logout dari XML ke kode Kotlin
        val btnLogout = findViewById<ImageButton>(R.id.btnLogout)

        // Menjalankan aksi saat tombol logout ditekan
        btnLogout.setOnClickListener {

            // Mengambil context Activity yang sedang aktif
            this.let { activityContext ->

                // Mengambil SharedPreferences yang digunakan untuk menyimpan sesi login
                val sharedPreferences = activityContext.getSharedPreferences(
                    "UserSession",
                    Context.MODE_PRIVATE
                )

                // Menghapus seluruh data sesi login yang tersimpan di perangkat
                // agar pengguna tidak otomatis login kembali
                sharedPreferences.edit().clear().apply()

                // Membuat Intent untuk berpindah ke halaman Login
                val intent = Intent(
                    activityContext,
                    LoginActivity::class.java
                )

                // Menghapus seluruh riwayat halaman sebelumnya
                // sehingga tombol Back tidak bisa kembali ke Dashboard
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TASK

                // Menjalankan perpindahan ke LoginActivity
                startActivity(intent)

                // Menutup DashboardPengangkutActivity
                // agar sesi logout benar-benar selesai
                activityContext.finish()
            }
        }
        rvDaftarTugas = findViewById(R.id.rvDaftarTugas)
        rvDaftarTugas.layoutManager = LinearLayoutManager(this)
        rvDaftarTugas.setHasFixedSize(true)

        tugasAdapter = TugasAdapter(daftarTugasList) { tugasItem ->
            val intent = Intent(this, DetailPenjemputanActivity::class.java).apply {
                putExtra("ID_TRANSAKSI", tugasItem.idTransaksi)
                putExtra("EMAIL_WARGA", tugasItem.emailWarga)
                putExtra("NAMA_WARGA", tugasItem.namaWarga)
                putExtra("ALAMAT_WARGA", tugasItem.alamatWarga)
                putExtra("NAMA_SAMPAH", tugasItem.namaSampah)
                putExtra("JENIS_SAMPAH", tugasItem.jenisSampah)
                putExtra("BERAT_SAMPAH", tugasItem.beratSampah)
                putExtra("TOTAL_HARGA", tugasItem.totalHarga)
                putExtra("POIN", tugasItem.poin)
            }
            startActivity(intent)
        }
        rvDaftarTugas.adapter = tugasAdapter

        muatAntreanJemputanRealtime()
    }

    private fun muatAntreanJemputanRealtime() {
        db.collection("transaksi")
            .whereEqualTo("status", "Menunggu Kurir")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Gagal memantau data: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    daftarTugasList.clear()
                    for (document in snapshots) {
                        val assignment = document.toObject(TugasModel::class.java)
                        assignment.idTransaksi = document.id
                        daftarTugasList.add(assignment)
                    }
                    tugasAdapter.notifyDataSetChanged()
                }
            }
    }
}