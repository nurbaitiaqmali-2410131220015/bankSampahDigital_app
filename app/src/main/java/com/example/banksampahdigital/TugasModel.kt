package com.example.banksampahdigital

data class TugasModel(
    var idTransaksi: String = "",
    val emailWarga: String = "",
    val namaWarga: String = "",
    val alamatWarga: String = "",
    val namaSampah: String = "",
    val jenisSampah: String = "",
    val beratSampah: Double = 0.0,
    val totalHarga: Int = 0,
    val poin: Int = 0
)