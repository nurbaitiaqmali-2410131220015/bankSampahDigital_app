package com.example.banksampahdigital

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class LokasiFragment : Fragment(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var db: FirebaseFirestore
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hubungkan ke fragment_lokasi.xml
        val view = inflater.inflate(R.layout.fragment_lokasi, container, false)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        db = FirebaseFirestore.getInstance()

        // Memanggil fragment peta agar muncul di UI
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        // Menghubungkan tombol lacak dari layout kamu
        val btnLacak = view.findViewById<Button>(R.id.btnLacak)
        btnLacak.setOnClickListener {
            mulaiPelacakanLokasi()
        }

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Fokus awal kamera mengarah ke daerah Banjarmasin sebelum dilacak
        val posisiBanjarmasin = LatLng(-3.316694, 114.590111)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(posisiBanjarmasin, 11f))
    }

    private fun mulaiPelacakanLokasi() {
        // Memeriksa izin GPS HP pengguna
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            mMap?.isMyLocationEnabled = true

            // Mengambil titik koordinat GPS dari HP
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val koordinatSaya = LatLng(location.latitude, location.longitude)

                    mMap?.clear() // Bersihkan peta dari pin lama
                    mMap?.addMarker(MarkerOptions().position(koordinatSaya).title("Lokasi Saya"))
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(koordinatSaya, 14f))

                    // PROSES GEOCODER: Mengubah koordinat GPS menjadi nama Kota/Kabupaten (100% Gratis)
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val listAlamat = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (!listAlamat.isNullOrEmpty()) {
                            val namaKotaUser = listAlamat[0].locality ?: listAlamat[0].subAdminArea ?: ""

                            if (namaKotaUser.isNotEmpty()) {
                                Toast.makeText(requireContext(), "Mencari Bank Sampah di $namaKotaUser...", Toast.LENGTH_SHORT).show()
                                // Ambil data bank sampah yang kotanya sama di Firestore
                                ambilBankSampahTerdekat(namaKotaUser)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Backup aman jika geocoder sedang offline: ambil semua data bank sampah
                        ambilBankSampahTerdekat("")
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal membaca GPS. Pastikan lokasi HP Anda sudah aktif.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // Memunculkan pop-up izin lokasi bawaan Android jika belum diizinkan
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun ambilBankSampahTerdekat(namaKota: String) {
        // 1. Ambil lokasi GPS terakhir dari HP user untuk patokan mengukur jarak
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        fusedLocationClient.lastLocation.addOnSuccessListener { userLocation: Location? ->
            if (userLocation == null) return@addOnSuccessListener

            // Ambil koordinat HP saat ini
            val userLat = userLocation.latitude
            val userLng = userLocation.longitude

            // 2. Tarik semua data dari Firestore untuk difilter berdasarkan jarak terdekat
            db.collection("bank_sampah").get()
                .addOnSuccessListener { querySnapshot ->
                    mMap?.clear() // Bersihkan pin lama

                    // Pasang kembali pin lokasi saya
                    val koordinatSaya = LatLng(userLat, userLng)
                    mMap?.addMarker(MarkerOptions().position(koordinatSaya).title("Lokasi Saya"))

                    var adaDataDekat = false
                    val RADIUS_MAKSIMAL_KM = 10.0 // Hanya tampilkan yang jaraknya di bawah 10 KM

                    for (document in querySnapshot) {
                        val nama = document.getString("nama") ?: "Bank Sampah"
                        val alamat = document.getString("alamat") ?: ""
                        val lat = document.getDouble("latitude")
                        val lng = document.getDouble("longitude")

                        if (lat != null && lng != null) {
                            // 3. Hitung jarak antara HP user dengan posisi Bank Sampah
                            val lokasiBank = Location("").apply {
                                latitude = lat
                                longitude = lng
                            }

                            // userLocation.distanceTo mengembalikan satuan METER, kita bagi 1000 jadi KM
                            val jarakKeBankKm = userLocation.distanceTo(lokasiBank) / 1000

                            // 4. Filter: Jika jaraknya masuk radius (di bawah 10 KM), munculkan di peta!
                            if (jarakKeBankKm <= RADIUS_MAKSIMAL_KM) {
                                adaDataDekat = true
                                val posisiBank = LatLng(lat, lng)

                                // Format jarak menjadi 1 angka di belakang koma (misal: 2.5 KM)
                                val jarakFormat = String.format(Locale.getDefault(), "%.1f KM", jarakKeBankKm)

                                mMap?.addMarker(
                                    MarkerOptions()
                                        .position(posisiBank)
                                        .title(nama)
                                        .snippet("$alamat ($jarakFormat)")
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                                )
                            }
                        }
                    }

                    if (!adaDataDekat) {
                        Toast.makeText(requireContext(), "Tidak ada Bank Sampah dalam radius $RADIUS_MAKSIMAL_KM KM.", Toast.LENGTH_SHORT).show()
                    }

                    // Fokuskan kamera ke lokasi user saat ini
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(koordinatSaya, 13f))
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Gagal memuat database: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mulaiPelacakanLokasi()
        }
    }
}