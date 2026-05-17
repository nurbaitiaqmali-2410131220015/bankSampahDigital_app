package com.example.banksampahdigital

import android.app.Application
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Memastikan Firebase menyala otomatis sejak aplikasi pertama kali booting
        FirebaseApp.initializeApp(this)
    }
}