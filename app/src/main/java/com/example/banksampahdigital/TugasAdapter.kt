package com.example.banksampahdigital

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// =======================================================
// 1. TUGAS MODEL: Struktur pembungkus paket data warga
// =======================================================
data class TugasModel(
    val namaWarga: String,
    val alamatWarga: String,
    val detailSampah: String
)

// =======================================================
// 2. TUGAS ADAPTER: Jembatan penghubung data ke layout XML
// =======================================================
class TugasAdapter(private val listTugas: ArrayList<TugasModel>) :
    RecyclerView.Adapter<TugasAdapter.TugasViewHolder>() {

    // ViewHolder: Mengenali komponen TextView yang ada di dalam item_tugas.xml
    class TugasViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.itemNamaWarga)
        val tvAlamat: TextView = view.findViewById(R.id.itemAlamatWarga)
    }

    // LANGKAH A: Menghubungkan adapter dengan file layout cetakan (item_tugas.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tugas, parent, false)
        return TugasViewHolder(view)
    }

    // LANGKAH B: Memasukkan data asli ke dalam komponen teks kartu di layar
    override fun onBindViewHolder(holder: TugasViewHolder, position: Int) {
        val data = listTugas[position]

        holder.tvNama.text = data.namaWarga
        holder.tvAlamat.text = data.alamatWarga

        // AKSI KLIK: Jika kartu baris tugas diklik oleh Kurir/Pengangkut
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context

            // Berpindah ke halaman Detail Penjemputan
            val intent = Intent(context, DetailPenjemputanActivity::class.java)

            // Titipkan data spesifik warga tersebut ke halaman detail
            intent.putExtra("NAMA_WARGA", data.namaWarga)
            intent.putExtra("ALAMAT_WARGA", data.alamatWarga)
            intent.putExtra("DETAIL_SAMPAH", data.detailSampah)

            context.startActivity(intent)
        }
    }

    // LANGKAH C: Menghitung total jumlah item yang ada di daftar
    override fun getItemCount(): Int = listTugas.size
}