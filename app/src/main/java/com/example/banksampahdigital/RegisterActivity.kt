package com.example.banksampahdigital

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etNama = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)

        // TAMBAHAN: Inisialisasi komponen RadioGroup & RadioButton
        val rgRoleRegister = findViewById<RadioGroup>(R.id.rgRoleRegister)
        val rbWarga = findViewById<RadioButton>(R.id.rbWarga)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        btnRegister.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validasi kelengkapan input
            if (nama.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua kolom wajib diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validasi tambahan: Memastikan password dan konfirmasi password sama
            if (password != confirmPassword) {
                Toast.makeText(this, "Password dan Konfirmasi Password tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TAMBAHAN: Logika menentukan string role berdasarkan RadioButton yang dipilih
            val roleSelected = if (rbWarga.isChecked) {
                "warga"
            } else {
                "pengangkut"
            }

            // UPDATE: Memasukkan field "role" ke dalam HashMap data pengguna
            val userMap = hashMapOf(
                "nama" to nama,
                "email" to email,
                "password" to password,
                "role" to roleSelected // Data ini yang akan dibaca saat Login nanti
            )

            // Menyimpan data ke Cloud Firestore dalam koleksi "users"
            db.collection("users").document(email).set(userMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Pendaftaran Berhasil!", Toast.LENGTH_SHORT).show()

                    // Otomatis diarahkan kembali ke halaman LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Gagal mendaftar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Aksi ketika teks "Login" diklik untuk kembali ke halaman login
        tvLoginLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}