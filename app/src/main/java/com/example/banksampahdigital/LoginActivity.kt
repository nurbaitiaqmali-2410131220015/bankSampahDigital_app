package com.example.banksampahdigital

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    // Inisialisasi Firestore untuk mengambil data dari database
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignUp = findViewById<TextView>(R.id.tvSignUp)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // 1. Validasi input kosong
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. Mengambil data pengguna berdasarkan Email dari koleksi "users"
            db.collection("users").document(email).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Ambil data password asli dari database
                        val dbPassword = document.getString("password")

                        // 3. Cocokkan password yang diinput dengan yang ada di database
                        if (password == dbPassword) {

                            // =======================================================
                            // BAGIAN BARU: MENYIMPAN EMAIL USER YANG LOGIN KE SESI HP
                            // =======================================================
                            val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("EMAIL_USER", email) // Menyimpan email untuk dipakai di halaman Dashboard
                            editor.apply()
                            // =======================================================

                            // AMBIL DATA ROLE: warga atau pengangkut
                            val role = document.getString("role")

                            Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                            // LOGIKA PENGALIHAN YANG BENAR
                            if (role == "pengangkut") {
                                // Jika akunnya pengangkut, buka DashboardPengangkutActivity
                                val intent = Intent(this, DashboardPengangkutActivity::class.java)
                                startActivity(intent)
                            } else {
                                // JIKA WARGA: Buka MainActivity! (Jangan panggil Fragment-nya langsung)
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            finish() // Menutup LoginActivity agar tidak bisa di-back kembali
                        } else {
                            Toast.makeText(this, "Password salah!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Akun tidak ditemukan! Silakan daftar dahulu.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saat login: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Aksi menuju ke halaman RegisterActivity jika teks "Sign Up" diklik
        tvSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}