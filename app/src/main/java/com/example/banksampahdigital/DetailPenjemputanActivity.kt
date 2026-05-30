package com.example.banksampahdigital

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class DetailPenjemputanActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_tugas_penjemputan)

        val tvNamaWarga = findViewById<TextView>(R.id.tvNamaWarga)
        val tvAlamat = findViewById<TextView>(R.id.tvAlamat)
        val tvDetailSampah = findViewById<TextView>(R.id.tvDetailSampah)
        val btnSelesaiAngkut = findViewById<Button>(R.id.btnSelesaiAngkut)

        val idTransaksi = intent.getStringExtra("ID_TRANSAKSI") ?: ""
        val emailWarga = intent.getStringExtra("EMAIL_WARGA") ?: ""
        val namaWarga = intent.getStringExtra("NAMA_WARGA") ?: ""
        val alamatWarga = intent.getStringExtra("ALAMAT_WARGA") ?: ""
        val namaSampah = intent.getStringExtra("NAMA_SAMPAH") ?: ""
        val jenisSampah = intent.getStringExtra("JENIS_SAMPAH") ?: ""
        val beratSampah = intent.getDoubleExtra("BERAT_SAMPAH", 0.0)
        val totalHarga = intent.getIntExtra("TOTAL_HARGA", 0)
        val poin = intent.getIntExtra("POIN", 0)

        tvNamaWarga.text = "Warga: $namaWarga"
        tvAlamat.text = "Alamat: $alamatWarga"
        tvDetailSampah.text = "$namaSampah ($jenisSampah) - $beratSampah Kg\nEstimasi: Rp $totalHarga (+ $poin Poin)"

        btnSelesaiAngkut.setOnClickListener {
            if (idTransaksi.isEmpty() || emailWarga.isEmpty()) {
                Toast.makeText(this, "Data transaksi rusak!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSelesaiAngkut.isEnabled = false

            val userRef = db.collection("users").document(emailWarga)
            val transaksiRef = db.collection("transaksi").document(idTransaksi)

            db.runTransaction { transaction ->
                val userSnapshot = transaction.get(userRef)

                val saldoLama = userSnapshot.getLong("saldo") ?: 0L
                val poinLama = userSnapshot.getLong("poin") ?: 0L

                val saldoBaru = saldoLama + totalHarga
                val poinBaru = poinLama + poin

                transaction.update(userRef, "saldo", saldoBaru)
                transaction.update(userRef, "poin", poinBaru)
                transaction.update(transaksiRef, "status", "Selesai Diangkut")

                null
            }.addOnSuccessListener {
                Toast.makeText(this, "Tugas Selesai! Saldo warga otomatis bertambah.", Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener { e ->
                btnSelesaiAngkut.isEnabled = true
                Toast.makeText(this, "Gagal mengonfirmasi penjemputan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}