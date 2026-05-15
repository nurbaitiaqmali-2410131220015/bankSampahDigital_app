package com.example.banksampahdigital

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        btnRegister.setOnClickListener {
            Toast.makeText(this, "Akun berhasil dibuat! Silakan Login", Toast.LENGTH_LONG).show()

            finish()
        }

        tvLoginLink.setOnClickListener {
            finish()
        }
    }
}