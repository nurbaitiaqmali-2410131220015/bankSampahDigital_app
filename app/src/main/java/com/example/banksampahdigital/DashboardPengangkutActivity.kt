package com.example.banksampahdigital

import android.content.Intent
import android.os.Bundle
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