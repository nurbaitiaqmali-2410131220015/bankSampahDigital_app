package com.example.banksampahdigital

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AktivitasAdapter(private val listTransaksi: List<TransaksiModel>) :
    RecyclerView.Adapter<AktivitasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAlamat: TextView = view.findViewById(R.id.tvRiwayatAlamat)
        val tvKeterangan: TextView = view.findViewById(R.id.tvRiwayatKeterangan)
        val tvStatus: TextView = view.findViewById(R.id.tvRiwayatStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listTransaksi[position]
        val context = holder.itemView.context
        val db = FirebaseFirestore.getInstance()

        // Mengisi data dari Firestore ke dalam desain kartu riwayat warga
        holder.tvAlamat.text = data.bankSampahTujuan
        holder.tvKeterangan.text = "${data.namaSampah} (${data.beratSampah} kg) - Rp ${data.totalHarga}"

        // Logika pewarnaan status teks berdasarkan kondisi di Firestore
        if (data.status == "Selesai Diangkut") {
            holder.tvStatus.text = "Selesai Diangkut"
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#2ECC71")) // Warna Hijau
        } else {
            holder.tvStatus.text = "Menunggu Kurir"
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#F39C12")) // Warna Jingga/Kuning
        }

        // 🛠️ LOGIKA BARU: Tekan lama item untuk membatalkan atau menghapus transaksi
        holder.itemView.setOnLongClickListener {
            if (data.status == "Menunggu Kurir") {
                // Tampilkan dialog konfirmasi pembatalan setoran sampah
                AlertDialog.Builder(context)
                    .setTitle("Batalkan Setoran?")
                    .setMessage("Apakah Anda ingin membatalkan transaksi penjemputan sampah ini?")
                    .setPositiveButton("Ya, Batalkan") { _, _ ->
                        db.collection("transaksi").document(data.idTransaksi)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Transaksi berhasil dibatalkan!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Gagal membatalkan: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Kembali", null)
                    .show()
            } else if (data.status == "Selesai Diangkut") {
                // Tampilkan dialog konfirmasi hapus riwayat selesai diangkut
                AlertDialog.Builder(context)
                    .setTitle("Hapus Riwayat?")
                    .setMessage("Hapus riwayat ini dari daftar aktivitas Anda? (Tidak akan mengurangi total saldo tabungan)")
                    .setPositiveButton("Hapus") { _, _ ->
                        db.collection("transaksi").document(data.idTransaksi)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Riwayat transaksi dihapus", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Gagal menghapus riwayat: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("Batal", null)
                    .show()
            }
            true
        }
    }

    override fun getItemCount(): Int = listTransaksi.size
}