package com.example.banksampahdigital

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class EstimasiFragment : Fragment() {

    // Inisialisasi basis data Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke fragment_estimasi.xml
        val view = inflater.inflate(R.layout.fragment_estimasi, container, false)

        // Hubungkan elemen UI berdasarkan ID yang baru kita tambahkan
        val etNamaSampah = view.findViewById<EditText>(R.id.etNamaSampah)
        val etJenisKategori = view.findViewById<EditText>(R.id.etJenisKategori)
        val etBeratSampah = view.findViewById<EditText>(R.id.etBeratSampah)
        val tvTotalHargaEstimasi = view.findViewById<TextView>(R.id.tvTotalHargaEstimasi)
        val etNamaBankSampah = view.findViewById<EditText>(R.id.etNamaBankSampah)

        val btnHitung = view.findViewById<Button>(R.id.btnHitung)
        val btnSetorJemput = view.findViewById<Button>(R.id.btnSetorJemput)

        // Konstanta tarif dasar per kilogram (Misal: Rp 3.000)
        val hargaPerKg = 3000
        var totalHargaKalkulasi = 0

        // 1. Logika Klik Tombol "Hitung Total Harga"
        btnHitung.setOnClickListener {
            val beratStr = etBeratSampah.text.toString().trim()

            if (beratStr.isEmpty()) {
                Toast.makeText(context, "Masukkan berat sampah dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hitung matematika konversi harga
            val beratValue = beratStr.toInt()
            totalHargaKalkulasi = beratValue * hargaPerKg

            // Ubah teks total harga di dalam CardView hasil
            tvTotalHargaEstimasi.text = "Rp. $totalHargaKalkulasi"
            Toast.makeText(context, "Total harga berhasil dihitung!", Toast.LENGTH_SHORT).show()
        }

        // 2. Logika Klik Tombol "Setor Dan Jemput" (Kirim ke Firestore / CREATE)
        btnSetorJemput.setOnClickListener {
            val namaSampah = etNamaSampah.text.toString().trim()
            val jenisSampah = etJenisKategori.text.toString().trim()
            val beratStr = etBeratSampah.text.toString().trim()
            val namaBank = etNamaBankSampah.text.toString().trim()

            // Validasi kelengkapan formulir sebelum dikirim ke database
            if (namaSampah.isEmpty() || jenisSampah.isEmpty() || beratStr.isEmpty() || namaBank.isEmpty()) {
                Toast.makeText(context, "Mohon lengkapi seluruh kolom input!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (totalHargaKalkulasi == 0) {
                Toast.makeText(context, "Hitung total harga terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil session email warga yang login dari SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val emailWarga = sharedPreferences.getString("EMAIL_USER", "Anonim")

            // Menyusun objek data untuk diunggah ke Firebase
            val transaksiMap = hashMapOf(
                "emailWarga" to emailWarga,
                "namaSampah" to namaSampah,
                "jenisSampah" to jenisSampah,
                "beratSampah" to beratStr.toDouble(),
                "totalHarga" to totalHargaKalkulasi,
                "bankSampahTujuan" to namaBank,
                "status" to "menunggu" // Indikator antrean agar bisa terbaca di aplikasi kurir
            )

            // Mengirim data baru ke koleksi "transaksi" di Cloud Firestore
            db.collection("transaksi").add(transaksiMap)
                .addOnSuccessListener {
                    Toast.makeText(context, "Transaksi penjemputan berhasil dibuat!", Toast.LENGTH_LONG).show()

                    // Mengosongkan formulir setelah data berhasil disimpan
                    etNamaSampah.text.clear()
                    etJenisKategori.text.clear()
                    etBeratSampah.text.clear()
                    etNamaBankSampah.text.clear()
                    tvTotalHargaEstimasi.text = "Rp. -"
                    totalHargaKalkulasi = 0
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Gagal mengirim data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }
}