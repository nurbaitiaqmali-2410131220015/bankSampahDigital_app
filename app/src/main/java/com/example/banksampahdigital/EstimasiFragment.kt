package com.example.banksampahdigital

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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
        val etAlamatJemput = view.findViewById<EditText>(R.id.etAlamatJemput)
        val etNamaBankSampah = view.findViewById<EditText>(R.id.etNamaBankSampah)
        val btnSetorJemput = view.findViewById<Button>(R.id.btnSetorJemput)

        btnSetorJemput.setOnClickListener {
            val namaSampah = etNamaSampah.text.toString().trim()
            val jenisInput = etJenisKategori.text.toString().trim()
            val alamatManual = etAlamatJemput.text.toString().trim()
            val namaBank = etNamaBankSampah.text.toString().trim()

            if (namaSampah.isEmpty() || jenisInput.isEmpty() || alamatManual.isEmpty() || namaBank.isEmpty()) {
                Toast.makeText(context, "Mohon lengkapi semua data formulir!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val emailWarga = sharedPreferences.getString("EMAIL_USER", "")

            if (emailWarga.isNullOrEmpty()) {
                Toast.makeText(context, "Sesi login kedaluwarsa!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSetorJemput.isEnabled = false

            // Ambil nama dari profil user
            db.collection("users").document(emailWarga).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val namaLengkapWarga = document.getString("nama") ?: "Warga"

                        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                        val tanggalHariIni = sdf.format(Date())

                        // Mapping data penjemputan awal
                        val transaksiMap = hashMapOf(
                            "emailWarga" to emailWarga,
                            "namaWarga" to namaLengkapWarga,
                            "alamatWarga" to alamatManual,
                            "namaSampah" to namaSampah,
                            "jenisSampah" to jenisInput.lowercase(), // Dipaksa lowercase untuk ID dokumen database
                            "beratSampah" to 0.0,  // Diisi oleh pengangkut nanti
                            "totalHarga" to 0,     // Dikalkulasi oleh pengangkut nanti
                            "poin" to 0,           // Dikalkulasi oleh pengangkut nanti
                            "bankSampahTujuan" to namaBank,
                            "status" to "Menunggu Kurir",
                            "tanggal" to tanggalHariIni
                        )

                        db.collection("transaksi").add(transaksiMap)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Request penjemputan berhasil dikirim ke Kurir!", Toast.LENGTH_LONG).show()

                                // Bersihkan Form
                                etNamaSampah.text.clear()
                                etJenisKategori.text.clear()
                                etAlamatJemput.text.clear()
                                etNamaBankSampah.text.clear()
                                btnSetorJemput.isEnabled = true
                            }
                            .addOnFailureListener { e ->
                                btnSetorJemput.isEnabled = true
                                Toast.makeText(context, "Gagal mengirim data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    btnSetorJemput.isEnabled = true
                }
        }

        return view
    }
}