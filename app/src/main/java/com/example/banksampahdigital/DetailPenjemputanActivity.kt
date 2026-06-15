package com.example.banksampahdigital

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DetailPenjemputanActivity : AppCompatActivity() {

    // Menghubungkan aplikasi dengan Firebase Firestore.
    private val db = FirebaseFirestore.getInstance()

    // List untuk menyimpan data timbangan sementara sebelum transaksi diselesaikan.
    private val listTimbanganSementara = ArrayList<ItemTimbangan>()

    // Adapter untuk menampilkan daftar timbangan sementara ke RecyclerView.
    private lateinit var itemTimbanganAdapter: ItemTimbanganAdapter

    // Variabel untuk menyimpan total harga seluruh sampah yang ditimbang.
    private var totalHargaKolektif = 0

    // Fungsi utama yang pertama kali dijalankan saat halaman detail penjemputan dibuka.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Menghubungkan Activity dengan layout tampilan tugas penjemputan.
        setContentView(R.layout.layout_tugas_penjemputan)

        // Menghubungkan TextView dari XML untuk menampilkan data warga dan total harga.
        val tvNamaWarga = findViewById<TextView>(R.id.tvNamaWarga)
        val tvAlamat = findViewById<TextView>(R.id.tvAlamat)
        val tvDetailSampah = findViewById<TextView>(R.id.tvDetailSampah)
        val tvKurirTotalHarga = findViewById<TextView>(R.id.tvKurirTotalHarga)

        // Menghubungkan EditText dari XML untuk input jenis dan berat sampah oleh kurir.
        val etKurirJenisSampah = findViewById<EditText>(R.id.etKurirJenisSampah)
        val etKurirBeratSampah = findViewById<EditText>(R.id.etKurirBeratSampah)

        // Menghubungkan Button dari XML untuk tambah item dan menyelesaikan angkut.
        val btnKurirTambahItem = findViewById<Button>(R.id.btnKurirTambahItem)
        val btnSelesaiAngkut = findViewById<Button>(R.id.btnSelesaiAngkut)

        // Mengambil ID transaksi yang dikirim dari halaman daftar tugas.
        val idTransaksi = intent.getStringExtra("ID_TRANSAKSI") ?: ""

        // Mengambil email warga sebagai ID dokumen user di Firestore.
        val emailWarga = intent.getStringExtra("EMAIL_WARGA") ?: ""

        // Mengambil nama warga dari Intent.
        val namaWarga = intent.getStringExtra("NAMA_WARGA") ?: ""

        // Mengambil alamat warga dari Intent.
        val alamatWarga = intent.getStringExtra("ALAMAT_WARGA") ?: ""

        // Mengambil catatan nama sampah awal dari warga.
        val namaSampah = intent.getStringExtra("NAMA_SAMPAH") ?: ""

        // Mengambil jenis sampah awal dari warga.
        val jenisSampah = intent.getStringExtra("JENIS_SAMPAH") ?: ""

        // Menampilkan nama warga ke halaman detail penjemputan.
        tvNamaWarga.text = "Warga: $namaWarga"

        // Menampilkan alamat warga ke halaman detail penjemputan.
        tvAlamat.text = "Alamat: $alamatWarga"

        // Menampilkan catatan sampah dari warga.
        tvDetailSampah.text = "Catatan Warga: $namaSampah ($jenisSampah)"

        // Menghubungkan RecyclerView untuk daftar timbangan sementara.
        val rvTimbanganKurir = findViewById<RecyclerView>(R.id.rvTimbanganKurir)

        // Mengatur RecyclerView agar tampil dalam bentuk daftar vertikal.
        rvTimbanganKurir.layoutManager = LinearLayoutManager(this)

        // Membuat adapter timbangan sementara beserta aksi edit dan hapus item.
        itemTimbanganAdapter = ItemTimbanganAdapter(
            listTimbanganSementara,

            // Aksi ketika tombol edit item ditekan.
            onEditClick = { position ->

                // Mengambil item berdasarkan posisi yang dipilih.
                val item = listTimbanganSementara[position]

                // Mengisi kembali input jenis sampah dengan data item yang diedit.
                etKurirJenisSampah.setText(item.jenis)

                // Mengisi kembali input berat sampah dengan data item yang diedit.
                etKurirBeratSampah.setText(item.berat.toString())

                // Menghapus item lama agar bisa diganti dengan data baru.
                listTimbanganSementara.removeAt(position)

                // Menghitung ulang total harga setelah item dihapus dari daftar.
                hitungUlangTotal(tvKurirTotalHarga)
            },

            // Aksi ketika tombol hapus item ditekan.
            onDeleteClick = { position ->

                // Menghapus item dari daftar timbangan sementara.
                listTimbanganSementara.removeAt(position)

                // Menghitung ulang total harga setelah item dihapus.
                hitungUlangTotal(tvKurirTotalHarga)

                // Menampilkan pesan bahwa item berhasil dihapus.
                Toast.makeText(this, "Item dihapus", Toast.LENGTH_SHORT).show()
            }
        )

        // Memasang adapter ke RecyclerView agar daftar timbangan muncul di layar.
        rvTimbanganKurir.adapter = itemTimbanganAdapter

        // Menjalankan proses tambah item saat tombol "Tambah ke Daftar" ditekan.
        btnKurirTambahItem.setOnClickListener {

            // Mengambil input jenis sampah, menghapus spasi, dan mengubah ke huruf kecil.
            val jenisId = etKurirJenisSampah.text.toString().trim().lowercase()

            // Mengambil input berat sampah dari EditText.
            val beratStr = etKurirBeratSampah.text.toString().trim()

            // Mengecek apakah input jenis atau berat masih kosong.
            if (jenisId.isEmpty() || beratStr.isEmpty()) {

                // Menampilkan pesan agar kurir melengkapi input.
                Toast.makeText(this, "Lengkapi jenis dan berat sampah riil!", Toast.LENGTH_SHORT).show()

                // Menghentikan proses klik agar tidak lanjut ke bawah.
                return@setOnClickListener
            }

            // Mengubah input berat dari String menjadi Double.
            val beratValue = beratStr.toDouble()

            // Mengambil data harga kategori sampah dari Firestore berdasarkan jenisId.
            db.collection("edukasi").document(jenisId).get()
                .addOnSuccessListener { doc ->

                    // Mengecek apakah dokumen kategori sampah ditemukan di Firestore.
                    if (doc.exists()) {

                        // Mengambil harga per kg dari Firestore, jika kosong memakai default 2000.
                        val hargaPerKg = doc.getLong("hargaKategori")?.toInt() ?: 2000

                        // Menghitung harga item berdasarkan berat dikali harga per kg.
                        val kalkulasiHargaItem = (beratValue * hargaPerKg).toInt()

                        // Membuat objek item timbangan baru.
                        val timbanganBaru = ItemTimbangan(
                            jenisId.replaceFirstChar { it.uppercase() },
                            jenisId,
                            beratValue,
                            kalkulasiHargaItem
                        )

                        // Menambahkan item timbangan baru ke daftar sementara.
                        listTimbanganSementara.add(timbanganBaru)

                        // Menghitung ulang total seluruh timbangan.
                        hitungUlangTotal(tvKurirTotalHarga)

                        // Mengosongkan input jenis sampah setelah item ditambahkan.
                        etKurirJenisSampah.text.clear()

                        // Mengosongkan input berat sampah setelah item ditambahkan.
                        etKurirBeratSampah.text.clear()

                    } else {

                        // Menampilkan pesan jika jenis kategori tidak ditemukan di database.
                        Toast.makeText(
                            this,
                            "ID Kategori '$jenisId' tidak valid di database!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener { e ->

                    // Menampilkan pesan jika gagal mengambil data dari Firestore.
                    Toast.makeText(
                        this,
                        "Koneksi database gagal: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        // Menjalankan proses final transaksi saat tombol selesai angkut ditekan.
        btnSelesaiAngkut.setOnClickListener {

            // Mengecek apakah daftar timbangan masih kosong.
            if (listTimbanganSementara.isEmpty()) {

                // Menampilkan pesan bahwa kurir harus menambahkan timbangan dulu.
                Toast.makeText(this, "Daftar timbangan masih kosong!", Toast.LENGTH_SHORT).show()

                // Menghentikan proses klik agar tidak lanjut.
                return@setOnClickListener
            }

            // Menonaktifkan tombol agar tidak ditekan berkali-kali.
            btnSelesaiAngkut.isEnabled = false

            // Menghitung total berat semua sampah.
            val totalBeratKolektif = listTimbanganSementara.sumOf { it.berat }

            // Menghitung poin berdasarkan total harga.
            val totalPoinKolektif = totalHargaKolektif / 100

            // Menggabungkan nama sampah dan berat menjadi satu teks.
            val gabunganNamaDanBerat =
                listTimbanganSementara.joinToString(", ") {
                    "${it.nama} (${it.berat} kg)"
                }

            // Menggabungkan semua jenis sampah menjadi satu teks.
            val gabunganJenis =
                listTimbanganSementara.joinToString(", ") {
                    it.jenis
                }

            // Referensi dokumen user warga di Firestore.
            val userRef = db.collection("users").document(emailWarga)

            // Referensi dokumen transaksi yang sedang diproses.
            val transaksiRef = db.collection("transaksi").document(idTransaksi)

            // Menjalankan transaksi Firestore agar update saldo dan transaksi berjalan aman.
            db.runTransaction { transaction ->

                // Mengambil data user warga saat ini.
                val userSnapshot = transaction.get(userRef)

                // Mengambil saldo lama warga, jika kosong dianggap 0.
                val saldoLama = userSnapshot.getLong("saldo") ?: 0L

                // Mengambil poin lama warga, jika kosong dianggap 0.
                val poinLama = userSnapshot.getLong("poin") ?: 0L

                // Menambahkan saldo lama dengan total harga hasil timbangan.
                transaction.update(userRef, "saldo", saldoLama + totalHargaKolektif)

                // Menambahkan poin lama dengan poin baru.
                transaction.update(userRef, "poin", poinLama + totalPoinKolektif)

                // Menyimpan nama sampah hasil timbangan riil ke dokumen transaksi.
                transaction.update(transaksiRef, "namaSampah", gabunganNamaDanBerat)

                // Menyimpan jenis sampah hasil timbangan riil ke dokumen transaksi.
                transaction.update(transaksiRef, "jenisSampah", gabunganJenis)

                // Menyimpan total berat hasil timbangan.
                transaction.update(transaksiRef, "beratSampah", totalBeratKolektif)

                // Menyimpan total harga transaksi.
                transaction.update(transaksiRef, "totalHarga", totalHargaKolektif)

                // Menyimpan total poin transaksi.
                transaction.update(transaksiRef, "poin", totalPoinKolektif)

                // Mengubah status transaksi menjadi selesai diangkut.
                transaction.update(transaksiRef, "status", "Selesai Diangkut")

                // Mengakhiri transaksi Firestore.
                null
            }.addOnSuccessListener {

                // Menampilkan pesan bahwa tugas berhasil diselesaikan.
                Toast.makeText(
                    this,
                    "Tugas sukses! Rp $totalHargaKolektif ditransfer ke warga.",
                    Toast.LENGTH_LONG
                ).show()

                // Menampilkan notifikasi lokal di HP kurir.
                tampilkanNotifikasiKurir(
                    "Tugas Selesai!",
                    "Data timbangan berhasil dikirim dan saldo warga telah diperbarui."
                )

                // Menutup halaman detail setelah transaksi selesai.
                finish()

            }.addOnFailureListener { e ->

                // Mengaktifkan kembali tombol jika transaksi gagal.
                btnSelesaiAngkut.isEnabled = true

                // Menampilkan pesan error jika transaksi gagal.
                Toast.makeText(
                    this,
                    "Gagal memproses transaksi: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Fungsi untuk menghitung ulang total harga semua item timbangan sementara.
    private fun hitungUlangTotal(tvTotal: TextView) {

        // Memberi tahu adapter bahwa data daftar timbangan berubah.
        itemTimbanganAdapter.notifyDataSetChanged()

        // Menghitung total harga dari seluruh item yang ada di list.
        totalHargaKolektif = listTimbanganSementara.sumOf { it.totalHargaItem }

        // Menampilkan total harga terbaru ke TextView.
        tvTotal.text = "Rp $totalHargaKolektif"
    }

    // Fungsi untuk membuat channel notifikasi khusus pengangkut.
    private fun createNotificationChannel() {

        // Channel hanya wajib dibuat untuk Android Oreo/API 26 ke atas.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Nama channel yang akan terlihat di pengaturan notifikasi.
            val name = "Notifikasi Pengangkut"

            // Deskripsi channel notifikasi.
            val descriptionText = "Channel untuk status penyelesaian tugas kurir"

            // Tingkat prioritas notifikasi.
            val importance = NotificationManager.IMPORTANCE_HIGH

            // Membuat channel notifikasi dengan ID PENGANGKUT_CHANNEL.
            val channel = NotificationChannel("PENGANGKUT_CHANNEL", name, importance).apply {

                // Menambahkan deskripsi ke channel.
                description = descriptionText
            }

            // Mengambil service NotificationManager dari sistem Android.
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Mendaftarkan channel ke sistem Android.
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Fungsi untuk menampilkan notifikasi lokal di HP kurir.
    private fun tampilkanNotifikasiKurir(judul: String, pesan: String) {

        // Membuat atau memastikan channel notifikasi sudah tersedia.
        createNotificationChannel()

        // Membuat isi notifikasi menggunakan NotificationCompat.
        val builder = NotificationCompat.Builder(this, "PENGANGKUT_CHANNEL")

            // Mengatur ikon kecil notifikasi.
            .setSmallIcon(android.R.drawable.ic_dialog_info)

            // Mengatur judul notifikasi.
            .setContentTitle(judul)

            // Mengatur isi pesan notifikasi.
            .setContentText(pesan)

            // Mengatur prioritas notifikasi agar muncul dengan jelas.
            .setPriority(NotificationCompat.PRIORITY_HIGH)

            // Menutup notifikasi otomatis ketika ditekan.
            .setAutoCancel(true)

        try {

            // Mengirim notifikasi ke sistem Android.
            with(NotificationManagerCompat.from(this)) {

                // ID notifikasi dibuat dari timestamp agar tidak saling menimpa.
                notify(System.currentTimeMillis().toInt(), builder.build())
            }

        } catch (e: SecurityException) {

            // Menangani error jika izin notifikasi belum diberikan.
            e.printStackTrace()
        }
    }
}