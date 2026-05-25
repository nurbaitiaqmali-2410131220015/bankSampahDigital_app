package com.example.banksampahdigital

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TugasAdapter(private val listTugas: List<TugasModel>) : // OPTIMALISASI: Mengubah ArrayList menjadi List biasa agar lebih ringan
    RecyclerView.Adapter<TugasAdapter.TugasViewHolder>() {

    class TugasViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.itemNamaWarga)
        val tvAlamat: TextView = view.findViewById(R.id.itemAlamatWarga)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TugasViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tugas, parent, false)
        return TugasViewHolder(view)
    }

    override fun onBindViewHolder(holder: TugasViewHolder, position: Int) {
        val data = listTugas[position]

        holder.tvNama.text = data.namaWarga
        holder.tvAlamat.text = data.alamatWarga

        // AKSI KLIK: Berpindah ke halaman Detail Penjemputan untuk Kurir
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailPenjemputanActivity::class.java).apply {
                putExtra("NAMA_WARGA", data.namaWarga)
                putExtra("ALAMAT_WARGA", data.alamatWarga)
                putExtra("DETAIL_SAMPAH", data.detailSampah)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = listTugas.size
}