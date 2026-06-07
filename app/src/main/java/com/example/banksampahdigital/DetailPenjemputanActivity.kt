package com.example.banksampahdigital

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DetailPenjemputanActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    // Gunakan model ItemSampah yang sudah ada untuk menampung data timbangan sementara
    private val listTimbanganSementara = ArrayList<ItemTimbangan>()
    private lateinit var itemTimbanganAdapter: ItemTimbanganAdapter

    private var totalHargaKolektif = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_tugas_penjemputan)

        val tvNamaWarga = findViewById<TextView>(R.id.tvNamaWarga)
        val tvAlamat = findViewById<TextView>(R.id.tvAlamat)
        val tvDetailSampah = findViewById<TextView>(R.id.tvDetailSampah)
        val tvKurirTotalHarga = findViewById<TextView>(R.id.tvKurirTotalHarga)

        val etKurirJenisSampah = findViewById<EditText>(R.id.etKurirJenisSampah)
        val etKurirBeratSampah = findViewById<EditText>(R.id.etKurirBeratSampah)

        val btnKurirTambahItem = findViewById<Button>(R.id.btnKurirTambahItem)
        val btnSelesaiAngkut = findViewById<Button>(R.id.btnSelesaiAngkut)

        // Tangkap data dari intent
        val idTransaksi = intent.getStringExtra("ID_TRANSAKSI") ?: ""
        val emailWarga = intent.getStringExtra("EMAIL_WARGA") ?: ""
        val namaWarga = intent.getStringExtra("NAMA_WARGA") ?: ""
        val alamatWarga = intent.getStringExtra("ALAMAT_WARGA") ?: ""
        val namaSampah = intent.getStringExtra("NAMA_SAMPAH") ?: ""
        val jenisSampah = intent.getStringExtra("JENIS_SAMPAH") ?: ""

        tvNamaWarga.text = "Warga: $namaWarga"
        tvAlamat.text = "Alamat: $alamatWarga"
        tvDetailSampah.text = "Catatan Warga: $namaSampah ($jenisSampah)"

        // Setup RecyclerView Timbangan Sementara
        val rvTimbanganKurir = findViewById<RecyclerView>(R.id.rvTimbanganKurir)
        rvTimbanganKurir.layoutManager = LinearLayoutManager(this)

        itemTimbanganAdapter = ItemTimbanganAdapter(
            listTimbanganSementara,
            onEditClick = { position ->
                val item = listTimbanganSementara[position]
                etKurirJenisSampah.setText(item.jenis)
                etKurirBeratSampah.setText(item.berat.toString())

                listTimbanganSementara.removeAt(position)
                hitungUlangTotal(tvKurirTotalHarga)
            },
            onDeleteClick = { position ->
                listTimbanganSementara.removeAt(position)
                hitungUlangTotal(tvKurirTotalHarga)
                Toast.makeText(this, "Item dihapus", Toast.LENGTH_SHORT).show()
            }
        )
        rvTimbanganKurir.adapter = itemTimbanganAdapter

        // LOGIKA BUTTON 1: TAMBAH ITEM TIMBANGAN (Mencocokkan Harga Ke Database per Jenis)
        btnKurirTambahItem.setOnClickListener {
            val jenisId = etKurirJenisSampah.text.toString().trim().lowercase()
            val beratStr = etKurirBeratSampah.text.toString().trim()

            if (jenisId.isEmpty() || beratStr.isEmpty()) {
                Toast.makeText(this, "Lengkapi jenis dan berat sampah riil!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val beratValue = beratStr.toDouble()

            // Ambil data hargaKategori secara presisi dari Firestore berdasarkan jenisId tunggal
            db.collection("edukasi").document(jenisId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        val hargaPerKg = doc.getLong("hargaKategori")?.toInt() ?: 2000
                        val kalkulasiHargaItem = (beratValue * hargaPerKg).toInt()

                        // Masukkan ke list antrean timbangan kurir
                        val timbanganBaru = ItemTimbangan(jenisId.replaceFirstChar { it.uppercase() }, jenisId, beratValue, kalkulasiHargaItem)
                        listTimbanganSementara.add(timbanganBaru)

                        hitungUlangTotal(tvKurirTotalHarga)

                        // Clear inputan timbangan
                        etKurirJenisSampah.text.clear()
                        etKurirBeratSampah.text.clear()
                    } else {
                        Toast.makeText(this, "ID Kategori '$jenisId' tidak valid di database!", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Koneksi database gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // LOGIKA BUTTON 2: FINAL SUBMIT (Transfer Saldo Akumulatif & Selesai)
        btnSelesaiAngkut.setOnClickListener {
            if (listTimbanganSementara.isEmpty()) {
                Toast.makeText(this, "Daftar timbangan masih kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSelesaiAngkut.isEnabled = false

            val totalBeratKolektif = listTimbanganSementara.sumOf { it.berat }
            val totalPoinKolektif = totalHargaKolektif / 100

            val gabunganNamaDanBerat = listTimbanganSementara.joinToString(", ") { "${it.nama} (${it.berat} kg)" }
            val gabunganJenis = listTimbanganSementara.joinToString(", ") { it.jenis }

            val userRef = db.collection("users").document(emailWarga)
            val transaksiRef = db.collection("transaksi").document(idTransaksi)

            db.runTransaction { transaction ->
                val userSnapshot = transaction.get(userRef)
                val saldoLama = userSnapshot.getLong("saldo") ?: 0L
                val poinLama = userSnapshot.getLong("poin") ?: 0L

                transaction.update(userRef, "saldo", saldoLama + totalHargaKolektif)
                transaction.update(userRef, "poin", poinLama + totalPoinKolektif)

                // Simpan data kalkulasi riil ke dokumen transaksi
                transaction.update(transaksiRef, "namaSampah", gabunganNamaDanBerat)
                transaction.update(transaksiRef, "jenisSampah", gabunganJenis)
                transaction.update(transaksiRef, "beratSampah", totalBeratKolektif)
                transaction.update(transaksiRef, "totalHarga", totalHargaKolektif)
                transaction.update(transaksiRef, "poin", totalPoinKolektif)
                transaction.update(transaksiRef, "status", "Selesai Diangkut")

                null
            }.addOnSuccessListener {
                Toast.makeText(this, "Tugas sukses! Rp $totalHargaKolektif ditransfer ke warga.", Toast.LENGTH_LONG).show()
                finish()
            }.addOnFailureListener { e ->
                btnSelesaiAngkut.isEnabled = true
                Toast.makeText(this, "Gagal memproses transaksi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hitungUlangTotal(tvTotal: TextView) {
        itemTimbanganAdapter.notifyDataSetChanged()
        totalHargaKolektif = listTimbanganSementara.sumOf { it.totalHargaItem }
        tvTotal.text = "Rp $totalHargaKolektif"
    }
}