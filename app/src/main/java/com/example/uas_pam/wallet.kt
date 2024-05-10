package com.example.uas_pam

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.uas_pam.databinding.ActivityWalletBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class FirestoreDocument(
    val id: String,
    val data: Map<String, Any>
)

class wallet : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding
    private lateinit var auth: FirebaseAuth
    var id_edit = ""

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

        ambilDataWallet()

        binding.logoutBtn.setOnClickListener {
            logout()
        }

        binding.btnTambah1.setOnClickListener {
            binding.masukanAlamat.visibility = View.VISIBLE
            binding.btnTambah2.visibility = View.VISIBLE
            binding.btnTambah1.visibility = View.GONE
            binding.btnTutup.visibility = View.VISIBLE
            binding.masukanAlamatEdit.visibility = View.GONE
            binding.btnUbah.visibility = View.GONE
        }

        binding.btnTutup.setOnClickListener {
            binding.masukanAlamat.visibility = View.GONE
            binding.masukanAlamatEdit.visibility = View.GONE
            binding.btnUbah.visibility = View.GONE
            binding.btnTambah2.visibility = View.GONE
            binding.btnTambah1.visibility = View.VISIBLE
            binding.btnTutup.visibility = View.GONE
        }

        binding.btnTambah2.setOnClickListener {
            val alamat = binding.masukanAlamat.text.toString()
            tambahDataAlamat(alamat)
        }

        binding.btnUbah.setOnClickListener {
            val alamatUbah = binding.masukanAlamatEdit.text.toString()
            ubahDataAlamat(id_edit, alamatUbah)
        }

    }

    fun ubahDataAlamat(id: String, alamat: String){
        val firestore = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        val alamatData = hashMapOf(
            "alamat" to alamat,
        )
        user?.let {
            firestore.collection("wallet-${it.uid}").document(id)
                .set(alamatData)
                .addOnSuccessListener {
                    ambilDataWallet()
                    Toast.makeText(
                        this,
                        "Berhasil Ubah Alamat Wallet",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.masukanAlamat.visibility = View.GONE
                    binding.btnTambah2.visibility = View.GONE
                    binding.btnTambah1.visibility = View.VISIBLE
                    binding.masukanAlamatEdit.visibility = View.GONE
                    binding.btnUbah.visibility = View.GONE
                    binding.btnTutup.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    recreate()
                    Log.i("Gagal", "${e.message}")
                    Toast.makeText(
                        baseContext, "Ubah Alamat Wallet Gagal . ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.masukanAlamat.visibility = View.GONE
                    binding.btnTambah2.visibility = View.GONE
                    binding.btnTambah1.visibility = View.VISIBLE
                    binding.btnTutup.visibility = View.GONE
                    binding.masukanAlamatEdit.visibility = View.GONE
                    binding.btnUbah.visibility = View.GONE
                    binding.btnTutup.visibility = View.GONE
                }
        }
    }

    fun bukaEdit(id: String, alamat: String){
        binding.masukanAlamatEdit.visibility = View.VISIBLE
        binding.btnUbah.visibility = View.VISIBLE
        binding.btnTutup.visibility = View.VISIBLE

        id_edit = id
        binding.masukanAlamatEdit.setText(alamat)
    }

    fun pindehKeAsset(alamat: String){
        val intent = Intent(this, Assets::class.java)
        intent.putExtra("alamat", alamat)
        startActivity(intent)
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
                    ambilDataWallet()
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

    fun ambilDataWallet(){

        val db = FirebaseFirestore.getInstance()
        val user = auth.currentUser

        user?.let {
            db.collection("wallet-${it.uid}")
                .get()
                .addOnSuccessListener { documents ->
                    if (documents != null) {

                        val dataList: MutableList<FirestoreDocument> = mutableListOf()
                        for (document in documents) {
                            Log.d(TAG, "${document.id} => ${document.getString("alamat")}")
                            val id = document.id
                            val data = document.data
                            dataList.add(FirestoreDocument(id, data))
                        }
                        Log.d(TAG, "Data List: ${dataList}")

                        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewAsset)
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        val adapter = RecyclerViewAdapter(dataList, this)
                        recyclerView.adapter = adapter

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
        }

    }

    fun hapusDataWallet(id: String){
        val db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        user?.let {
            val docRef = db.collection("wallet-${it.uid}").document(id)
            docRef.delete()
                .addOnSuccessListener {
                    Log.d(TAG, "Dokumen berhasil dihapus.")
                    Toast.makeText(
                        baseContext, "Data Wallet Berhasil Dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                    ambilDataWallet()
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                    Toast.makeText(
                        baseContext, "Tambah Alamat Wallet Gagal . ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textViewAlamat: TextView = itemView.findViewById(R.id.textViewAlamat)
    val imageViewDelete: ImageView = itemView.findViewById(R.id.imageViewDelete)
    val imageViewEdit: ImageView = itemView.findViewById(R.id.imageViewEdit)
}

class RecyclerViewAdapter(private val dataList: List<FirestoreDocument>, private val wallet: wallet) : RecyclerView.Adapter<ViewHolder>() {
    private lateinit var auth: FirebaseAuth
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val document = dataList[position]
        holder.textViewAlamat.text = document.data["alamat"].toString()

        holder.itemView.setOnClickListener {
            println("ini id: ${document.id}")
            wallet.pindehKeAsset(document.data["alamat"].toString())
        }

        holder.imageViewEdit.setOnClickListener {
            wallet.bukaEdit(document.id, document.data["alamat"].toString())
        }

        holder.imageViewDelete.setOnClickListener {
              wallet.hapusDataWallet(document.id)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}