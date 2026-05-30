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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EstimasiFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estimasi, container, false)

        val etNamaSampah = view.findViewById<EditText>(R.id.etNamaSampah)
        val etJenisKategori = view.findViewById<EditText>(R.id.etJenisKategori)
        val etBeratSampah = view.findViewById<EditText>(R.id.etBeratSampah)
        val tvTotalHargaEstimasi = view.findViewById<TextView>(R.id.tvTotalHargaEstimasi)
        val etNamaBankSampah = view.findViewById<EditText>(R.id.etNamaBankSampah)

        val btnHitung = view.findViewById<Button>(R.id.btnHitung)
        val btnSetorJemput = view.findViewById<Button>(R.id.btnSetorJemput)

        val hargaPerKg = 3000
        var totalHargaKalkulasi = 0

        btnHitung.setOnClickListener {
            val beratStr = etBeratSampah.text.toString().trim()

            if (beratStr.isEmpty()) {
                Toast.makeText(context, "Masukkan berat sampah dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val beratValue = beratStr.toInt()
            totalHargaKalkulasi = beratValue * hargaPerKg

            tvTotalHargaEstimasi.text = "Rp. $totalHargaKalkulasi"
            Toast.makeText(context, "Total harga berhasil dihitung!", Toast.LENGTH_SHORT).show()
        }

        btnSetorJemput.setOnClickListener {
            val namaSampah = etNamaSampah.text.toString().trim()
            val jenisSampah = etJenisKategori.text.toString().trim()
            val beratStr = etBeratSampah.text.toString().trim()
            val namaBank = etNamaBankSampah.text.toString().trim()

            if (namaSampah.isEmpty() || jenisSampah.isEmpty() || beratStr.isEmpty() || namaBank.isEmpty()) {
                Toast.makeText(context, "Mohon lengkapi seluruh kolom input!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (totalHargaKalkulasi == 0) {
                Toast.makeText(context, "Hitung total harga terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val emailWarga = sharedPreferences.getString("EMAIL_USER", "")

            if (emailWarga.isNullOrEmpty()) {
                Toast.makeText(context, "Sesi pengguna tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("users").document(emailWarga).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val namaLengkapWarga = document.getString("nama") ?: "Warga Anonim"
                        val alamatWarga = document.getString("alamat") ?: "Alamat tidak diisi"

                        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                        val tanggalHariIni = sdf.format(Date())
                        val estimasiPoin = totalHargaKalkulasi / 100

                        val transaksiMap = hashMapOf(
                            "emailWarga" to emailWarga,
                            "namaWarga" to namaLengkapWarga,
                            "alamatWarga" to alamatWarga,
                            "namaSampah" to namaSampah,
                            "jenisSampah" to jenisSampah,
                            "beratSampah" to beratStr.toDouble(),
                            "totalHarga" to totalHargaKalkulasi,
                            "poin" to estimasiPoin,
                            "bankSampahTujuan" to namaBank,
                            "status" to "Menunggu Kurir",
                            "tanggal" to tanggalHariIni
                        )

                        db.collection("transaksi").add(transaksiMap)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Transaksi penjemputan berhasil dibuat!", Toast.LENGTH_LONG).show()

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
                    } else {
                        Toast.makeText(context, "Data profil warga tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Gagal mengambil data profil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }
}