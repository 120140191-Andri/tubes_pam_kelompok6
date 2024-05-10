package com.example.uas_pam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_pam.databinding.ActivityLoginBinding
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            pindahMenuWallet()
        }

        binding.keRegis.setOnClickListener {
            pindahKeRegis()
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.inputEmail.text.toString()
            val password = binding.inputPassword.text.toString()

            if(email != "" && password != ""){
                authLogin(email, password)
            }else{
                Toast.makeText(this, "Email dan Password Field Wajib Diisi", Toast.LENGTH_SHORT).show()
                recreate()
            }
        }

    }

    private fun pindahKeRegis(){
        val intentRegis = Intent(this, Registrasi::class.java)
        startActivity(intentRegis)
    }

    private fun pindahMenuWallet(){
        val intentDas = Intent(this, wallet::class.java)
        startActivity(intentDas)
    }

    private fun authLogin(email: String, password: String){
        Toast.makeText(
            this,
            "Loading....",
            Toast.LENGTH_SHORT,
        ).show()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Login Berhasil.",
                        Toast.LENGTH_SHORT,
                    ).show()

                    pindahMenuWallet()
                } else {
                    Toast.makeText(
                        this,
                        "Login Gagal.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    recreate()
                }
            }
    }

}