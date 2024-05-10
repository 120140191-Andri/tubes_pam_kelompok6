package com.example.uas_pam

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uas_pam.databinding.ActivityAssetsBinding
import com.example.uas_pam.databinding.ActivityWalletBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Assets : AppCompatActivity() {

    private lateinit var binding: ActivityAssetsBinding
    private lateinit var auth: FirebaseAuth
    var alamat = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAssetsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        alamat = intent.getStringExtra("alamat").toString()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {

            val db = FirebaseFirestore.getInstance()

            db.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Dokumen ditemukan, Anda dapat mengakses data di sini
                        val data = document.data
                        // Lakukan apa pun yang perlu Anda lakukan dengan data di sini

                        binding.nama.text = "Selamat Datang ${document.getString("nama")}"

                    } else {
                        // Dokumen tidak ditemukan
                        println("Document not found")
                    }
                }
                .addOnFailureListener { exception ->
                    // Kegagalan saat mengambil data
                    println("Error getting documents: $exception")
                    pindahKeLogin()
                }

        }else{
            pindahKeLogin()
        }

    }

    private fun pindahKeLogin(){
        val intentLogin = Intent(this, Login::class.java)
        startActivity(intentLogin)
    }
}