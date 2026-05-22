package com.example.banksampahdigital

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardPengangkutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_pengangkut)

        // 1. Hubungkan dengan RecyclerView yang ada di activity_dashboard_pengangkut.xml
        val rvDaftarTugas = findViewById<RecyclerView>(R.id.rvDaftarTugas)

        // 2. Atur LayoutManager agar daftar kartu tersusun berurutan ke bawah (vertikal)
        rvDaftarTugas.layoutManager = LinearLayoutManager(this)

        // 3. DATA DUMMY: Daftar tugas penjemputan untuk simulasi testing aplikasi
        val listTugasDummy = arrayListOf(
            TugasModel(
                "Bpk. Budi Santoso",
                "Griya Harmoni Blok C No. 5",
                "Plastik PET (~4 Kg), Kardus (~8 Kg)"
            ),
            IbuSitiAminahDummy(), // Memanggil data otomatis dari fungsi pembantu di bawah
            TugasModel(
                "Bpk. Joko Widodo",
                "Jl. Ahmad Yani No. 45",
                "Logam Baut (~5 Kg)"
            )
        )

        // 4. PASANG ADAPTER: Kirim data dummy ke TugasAdapter sebagai jembatan layout
        val adapter = TugasAdapter(listTugasDummy)
        rvDaftarTugas.adapter = adapter
    }

    // Fungsi pembantu untuk membuat data dummy tambahan agar kode di atas lebih rapi
    private fun IbuSitiAminahDummy(): TugasModel {
        return TugasModel(
            "Ibu Siti Aminah",
            "Jl. Mawar Raya No. 12",
            "Minyak Jelantah (~3 Liter)"
        )
    }
}