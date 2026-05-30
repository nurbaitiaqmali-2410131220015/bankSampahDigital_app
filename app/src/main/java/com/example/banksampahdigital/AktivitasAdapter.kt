package com.example.banksampahdigital

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// File baru khusus untuk mengatur daftar riwayat transaksi di dashboard warga
class AktivitasAdapter(private val listTransaksi: List<TransaksiModel>) :
    RecyclerView.Adapter<AktivitasAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Menghubungkan ke ID TextView yang ada di file XML cetakan riwayat milikmu
        val tvAlamat: TextView = view.findViewById(R.id.tvRiwayatAlamat)
        val tvKeterangan: TextView = view.findViewById(R.id.tvRiwayatKeterangan)
        val tvStatus: TextView = view.findViewById(R.id.tvRiwayatStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // PENTING: Ganti "item_aktivitas" di bawah ini dengan nama file XML cetakan riwayat milikmu (misal: item_riwayat atau sejenisnya)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = listTransaksi[position]

        // Mengisi data dari Firestore ke dalam desain kartu riwayat warga
        holder.tvAlamat.text = data.bankSampahTujuan
        holder.tvKeterangan.text = "${data.namaSampah} (${data.beratSampah} kg) - Rp ${data.totalHarga}"

        // Logika pewarnaan status teks berdasarkan kondisi di Firestore
        if (data.status == "selesai") {
            holder.tvStatus.text = "Selesai Diangkut"
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#2ECC71")) // Warna Hijau
        } else {
            holder.tvStatus.text = "Menunggu Kurir"
            holder.tvStatus.setTextColor(android.graphics.Color.parseColor("#F39C12")) // Warna Jingga/Kuning
        }
    }

    override fun getItemCount(): Int = listTransaksi.size

}