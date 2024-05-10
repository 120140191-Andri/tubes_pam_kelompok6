package com.example.uas_pam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uas_pam.databinding.ActivityAssetsBinding
import com.example.uas_pam.databinding.ActivityWalletBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class wallet : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityWalletBinding.inflate(layoutInflater)
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
                        println("Data: $data")

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

        binding.logoutBtn.setOnClickListener {
            logout()
        }

        binding.btnTambah1.setOnClickListener {
            binding.masukanAlamat.visibility = View.VISIBLE
            binding.btnTambah2.visibility = View.VISIBLE
            binding.btnTambah1.visibility = View.GONE
            binding.btnTutup.visibility = View.VISIBLE
        }

        binding.btnTutup.setOnClickListener {
            binding.masukanAlamat.visibility = View.GONE
            binding.btnTambah2.visibility = View.GONE
            binding.btnTambah1.visibility = View.VISIBLE
            binding.btnTutup.visibility = View.GONE
        }

        binding.btnTambah2.setOnClickListener {
            val alamat = binding.masukanAlamat.text.toString()
            tambahDataAlamat(alamat)
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

    private fun tambahDataAlamat(alamat: String){
        val firestore = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        val alamatData = hashMapOf(
            "alamat" to alamat,
        )
        user?.let {
            firestore.collection("wallet-${it.uid}")
                .add(alamatData)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Berhasil Tambah Alamat Wallet",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.masukanAlamat.visibility = View.GONE
                    binding.btnTambah2.visibility = View.GONE
                    binding.btnTambah1.visibility = View.VISIBLE
                    binding.btnTutup.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    recreate()
                    Log.i("Gagal", "${e.message}")
                    Toast.makeText(
                        baseContext, "Tambah Alamat Wallet Gagal . ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.masukanAlamat.visibility = View.GONE
                    binding.btnTambah2.visibility = View.GONE
                    binding.btnTambah1.visibility = View.VISIBLE
                    binding.btnTutup.visibility = View.GONE
                }
        }
    }
}