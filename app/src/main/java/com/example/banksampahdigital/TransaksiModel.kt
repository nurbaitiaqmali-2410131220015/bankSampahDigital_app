package com.example.banksampahdigital

// Cetakan data khusus untuk menampilkan riwayat di dashboard warga
data class TransaksiModel(
    var idTransaksi: String = "",
    val emailWarga: String = "",
    val namaSampah: String = "",
    val jenisSampah: String = "",
    val beratSampah: Double = 0.0,
    val totalHarga: Int = 0,
    val bankSampahTujuan: String = "",
    val status: String = ""
)