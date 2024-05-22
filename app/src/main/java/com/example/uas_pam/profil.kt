package com.example.uas_pam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uas_pam.databinding.ActivityProfilBinding
import com.example.uas_pam.databinding.ActivityWalletBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class profil : AppCompatActivity() {

    private lateinit var binding: ActivityProfilBinding
    private lateinit var auth: FirebaseAuth
    var id_edit = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProfilBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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

                        binding.nama.text = "Nama: ${document.getString("nama")}"

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

        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intentDas = Intent(this, wallet::class.java)
                    startActivity(intentDas)
                    true
                }
                else -> false
            }
        }

        binding.logoutBtn.setOnClickListener {
            logout()
        }

    }

    private fun pindahKeLogin(){
        val intentLogin = Intent(this, Login::class.java)
        startActivity(intentLogin)
    }

    private  fun logout(){
        auth.signOut()
        pindahKeLogin()
    }
}