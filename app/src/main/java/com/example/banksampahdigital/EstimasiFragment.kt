package com.example.banksampahdigital

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EstimasiFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    // Inisialisasi List Sementara dan Adapter
    private val listSampahSementara = ArrayList<ItemSampah>()
    private lateinit var itemSampahAdapter: ItemSampahAdapter

    private var totalHargaKalkulasiAll = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estimasi, container, false)

        // Inisialisasi View Komponen Input
        val etNamaSampah = view.findViewById<EditText>(R.id.etNamaSampah)
        val etJenisKategori = view.findViewById<EditText>(R.id.etJenisKategori)
        val etBeratSampah = view.findViewById<EditText>(R.id.etBeratSampah)
        val etAlamatJemput = view.findViewById<EditText>(R.id.etAlamatJemput)
        val etNamaBankSampah = view.findViewById<EditText>(R.id.etNamaBankSampah)
        val tvTotalHargaEstimasi = view.findViewById<TextView>(R.id.tvTotalHargaEstimasi)

        // Inisialisasi Tombol
        val btnTambahItem = view.findViewById<Button>(R.id.btnTambahItem)
        val btnSetorJemput = view.findViewById<Button>(R.id.btnSetorJemput)

        // Inisialisasi RecyclerView untuk menampung List Sementara
        val rvItemSampahSementara = view.findViewById<RecyclerView>(R.id.rvItemSampahSementara)
        rvItemSampahSementara.layoutManager = LinearLayoutManager(context)

        // Setup adapter dengan fungsi aksi lambda click Edit dan Delete
        itemSampahAdapter = ItemSampahAdapter(
            listSampahSementara,
            onEditClick = { position ->
                // Logika Edit: Kembalikan data dari list ke kolom input untuk diedit kembali
                val item = listSampahSementara[position]
                etNamaSampah.setText(item.nama)
                etJenisKategori.setText(item.jenis)
                etBeratSampah.setText(item.berat.toString())

                // Hapus item lama dari daftar sementara, lalu hitung ulang total harga
                listSampahSementara.removeAt(position)
                updateTotalHarga(tvTotalHargaEstimasi)
                Toast.makeText(context, "Silakan edit data pada kolom input di atas", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { position ->
                // Logika Hapus Item dari daftar list sementara
                listSampahSementara.removeAt(position)
                updateTotalHarga(tvTotalHargaEstimasi)
                Toast.makeText(context, "Item berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
        )
        rvItemSampahSementara.adapter = itemSampahAdapter

        // 1. Logika Tombol TAMBAH SAMPAH (Mengambil Harga Dinamis dari Firestore)
        btnTambahItem.setOnClickListener {
            val nama = etNamaSampah.text.toString().trim()
            val jenisInput = etJenisKategori.text.toString().trim()
            val jenisDocumentId = jenisInput.lowercase() // Menyamakan dengan ID dokumen di Firestore (huruf kecil)
            val beratStr = etBeratSampah.text.toString().trim()

            if (nama.isEmpty() || jenisInput.isEmpty() || beratStr.isEmpty()) {
                Toast.makeText(context, "Mohon lengkapi data sampah terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val beratValue = beratStr.toDouble()

            // Mengambil hargaKategori langsung dari collection 'edukasi' berdasarkan jenis yang diinput
            db.collection("edukasi").document(jenisDocumentId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Tarik field 'hargaKategori' asli dari database, jika gagal fallback ke 3000
                        val hargaDariDatabase = document.getLong("hargaKategori")?.toInt() ?: 3000

                        // Kalkulasi harga item dinamis
                        val totalHargaItem = (beratValue * hargaDariDatabase).toInt()

                        // Masukkan ke daftar list sementara
                        val itemBaru = ItemSampah(nama, jenisInput, beratValue, totalHargaItem)
                        listSampahSementara.add(itemBaru)

                        // Perbarui visual RecyclerView & Total Harga Akumulasi
                        updateTotalHarga(tvTotalHargaEstimasi)

                        // Bersihkan kolom input sampah agar bisa mengetik item yang lain
                        etNamaSampah.text.clear()
                        etJenisKategori.text.clear()
                        etBeratSampah.text.clear()

                        Toast.makeText(context, "Berhasil menambahkan ke daftar setoran!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Antisipasi jika pengguna mengetik nama kategori yang tidak sesuai di Firestore
                        Toast.makeText(context, "Kategori '$jenisInput' tidak terdaftar di database!", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Gagal mengecek tarif sampah: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // 2. Logika Tombol SETOR DAN JEMPUT (Kirim Final Data Kolektif ke Firestore)
        btnSetorJemput.setOnClickListener {
            val alamatManual = etAlamatJemput.text.toString().trim()
            val namaBank = etNamaBankSampah.text.toString().trim()

            if (listSampahSementara.isEmpty()) {
                Toast.makeText(context, "Daftar setoran Anda masih kosong! Tambahkan sampah dulu.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (alamatManual.isEmpty() || namaBank.isEmpty()) {
                Toast.makeText(context, "Mohon isi alamat penjemputan dan bank sampah tujuan!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sharedPreferences = requireActivity().getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val emailWarga = sharedPreferences.getString("EMAIL_USER", "")

            if (emailWarga.isNullOrEmpty()) {
                Toast.makeText(context, "Sesi pengguna tidak valid!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Ambil data nama lengkap dari profile user
            db.collection("users").document(emailWarga).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val namaLengkapWarga = document.getString("nama") ?: "Warga Anonim"

                        // Proses ekstraksi data list menjadi bentuk baris string terstruktur tunggal
                        val gabunganNama = listSampahSementara.joinToString(", ") { it.nama }
                        val gabunganJenis = listSampahSementara.joinToString(", ") { it.jenis }
                        val totalBeratKolektif = listSampahSementara.sumOf { it.berat }

                        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                        val tanggalHariIni = sdf.format(Date())
                        val estimasiPoin = totalHargaKalkulasiAll / 100

                        // Menyusun mapping data transaksi untuk disimpan ke Firestore
                        val transaksiMap = hashMapOf(
                            "emailWarga" to emailWarga,
                            "namaWarga" to namaLengkapWarga,
                            "alamatWarga" to alamatManual, // Menggunakan alamat dinamis yang diisi warga
                            "namaSampah" to gabunganNama,
                            "jenisSampah" to gabunganJenis,
                            "beratSampah" to totalBeratKolektif,
                            "totalHarga" to totalHargaKalkulasiAll,
                            "poin" to estimasiPoin,
                            "bankSampahTujuan" to namaBank,
                            "status" to "Menunggu Kurir",
                            "tanggal" to tanggalHariIni
                        )

                        // Simpan dokumen ke Firestore
                        db.collection("transaksi").add(transaksiMap)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Transaksi penjemputan berhasil dibuat!", Toast.LENGTH_LONG).show()

                                // Reset totalitas tampilan & list setelah sukses setor
                                listSampahSementara.clear()
                                updateTotalHarga(tvTotalHargaEstimasi)
                                etAlamatJemput.text.clear()
                                etNamaBankSampah.text.clear()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, "Gagal mengirim data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Data profil warga tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Gagal mengambil data profil: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }

    // Fungsi Pembantu untuk sinkronisasi nilai harga dan aksi RecyclerView secara real-time
    private fun updateTotalHarga(tvTotal: TextView) {
        itemSampahAdapter.notifyDataSetChanged()
        totalHargaKalkulasiAll = listSampahSementara.sumOf { it.totalHargaItem }
        tvTotal.text = "Rp. $totalHargaKalkulasiAll"
    }
}