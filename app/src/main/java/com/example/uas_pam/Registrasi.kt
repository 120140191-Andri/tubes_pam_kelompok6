package com.example.uas_pam

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.uas_pam.databinding.ActivityLoginBinding
import com.example.uas_pam.databinding.ActivityRegistrasiBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Registrasi : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrasiBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegistrasiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        binding.keLogin.setOnClickListener {
            pindahKeLogin()
        }

        binding.registrasiBtn.setOnClickListener {
            val nama = binding.inputNama.text.toString()
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()

            val inputs = mapOf(
                "nama" to nama,
                "email" to email,
                "password" to password,
            )

            val kosong = inputs.filterValues { it.isEmpty() }.keys
            if(kosong.isEmpty()){
                tambahAkun(inputs)
            }else{
                recreate()
                Toast.makeText(this, "Semua Field Wajib Diisi!", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun pindahKeLogin(){
        val intentLogin = Intent(this, Login::class.java)
        startActivity(intentLogin)
    }

    private fun tambahAkun(inputs: Map<String, String>) {

        auth.createUserWithEmailAndPassword(inputs["email"]!!, inputs["password"]!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    tambahDataLain(inputs)
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

    private fun tambahDataLain(inputs: Map<String, String>){
        val firestore = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        val userData = hashMapOf(
            "nama" to inputs["nama"],
        )
        user?.let {
            firestore.collection("users").document(it.uid)
                .set(userData)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Registration Berhasil, Silahkan Login",
                        Toast.LENGTH_SHORT
                    ).show()
                    pindahKeLogin()
                }
                .addOnFailureListener { e ->
                    recreate()
                    Log.i("Gagal", "${e.message}")
                    Toast.makeText(
                        baseContext, "Registration failed. ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

}