package com.example.uas_pam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val intentSplash = Intent(this, Login::class.java)
        startActivity(intentSplash)
    }

    private fun tambahAkun() {

        auth.createUserWithEmailAndPassword("tes@gmail.com", "1234567")
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.i("Berhasil", "Registrasi Berhasil")
                    Toast.makeText(
                        baseContext, "Registration Berhasil",
                        Toast.LENGTH_SHORT
                    ).show()
                    recreate()
                } else {
                    Log.i("Gagal", "${task.exception?.message}")
                    Toast.makeText(
                        baseContext, "Registration failed. ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    recreate()
                }
            }

    }

    private fun cekLogin(): Boolean {
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            Log.i("TAG", currentUser.uid)
            return true
        }else{
            Log.i("TAG", "kosong")
            tambahAkun()
            return false
        }
    }
}