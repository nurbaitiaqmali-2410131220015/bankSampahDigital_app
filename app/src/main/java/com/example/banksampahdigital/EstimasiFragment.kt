package com.example.banksampahdigital

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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
                            "jenisSampah" to jenisInput.lowercase(),
                            "beratSampah" to 0.0,
                            "totalHarga" to 0,
                            "poin" to 0,
                            "bankSampahTujuan" to namaBank,
                            "status" to "Menunggu Kurir",
                            "tanggal" to tanggalHariIni
                        )

                        db.collection("transaksi").add(transaksiMap)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Request penjemputan berhasil dikirim ke Kurir!", Toast.LENGTH_LONG).show()

                                // 🔥 MEMANGGIL NOTIFIKASI LOKAL SAAT BERHASIL
                                tampilkanNotifikasiLokal(
                                    "Penjemputan Diajukan!",
                                    "Kurir akan segera datang ke lokasi Anda. Mohon siapkan sampah Anda."
                                )

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

    // ==================== LOGIKA FUNGSI NOTIFIKASI LOKAL ====================

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notifikasi Bank Sampah"
            val descriptionText = "Channel untuk status penjemputan sampah"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("BANK_SAMPAH_CHANNEL", name, importance).apply {
                description = descriptionText
            }

            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun tampilkanNotifikasiLokal(judul: String, pesan: String) {
        // Daftarkan channel ke sistem Android
        createNotificationChannel()

        val builder = NotificationCompat.Builder(requireContext(), "BANK_SAMPAH_CHANNEL")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icon sistem bawaan sementara
            .setContentTitle(judul)
            .setContentText(pesan)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(requireContext())) {
                // Generate ID acak unik berbasis timestamp agar notifikasi tidak saling menimpa
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}