package com.example.banksampahdigital // SESUAIKAN dengan nama package aplikasi kamu ya, Mad

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SampahEdukasiAdapter(private val listSampah: List<SampahEdukasi>) :
    RecyclerView.Adapter<SampahEdukasiAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgSampah: ImageView = view.findViewById(R.id.imgSampah)
        val tvNamaSampah: TextView = view.findViewById(R.id.tvNamaSampah)
        val tvPenjelasan: TextView = view.findViewById(R.id.tvPenjelasan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sampah_edukasi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listSampah[position]
        holder.tvNamaSampah.text = item.nama
        holder.tvPenjelasan.text = item.penjelasan

        // Menggunakan library Glide untuk otomatis memuat gambar dari URL internet
        Glide.with(holder.itemView.context)
            .load(item.gambarUrl)
            .placeholder(android.R.color.darker_gray) // warna abu-abu sebagai loading sementara
            .error(android.R.color.darker_gray)       // warna jika link gambar eror/rusak
            .into(holder.imgSampah)
    }

    override fun getItemCount(): Int = listSampah.size
}