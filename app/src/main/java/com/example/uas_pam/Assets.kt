package com.example.uas_pam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_pam.databinding.ActivityAssetsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Callback
import retrofit2.Response

interface ApiService {
    @GET("asset/{alamat}")
    fun getAssetByAlamat(@Path("alamat") alamat: String): Call<ResponseData>
}

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

        ambilData(this)
    }

    fun ambilData(context: Context){
        val alamat = alamat // Replace with the desired post ID
        val call = ApiClient.apiService.getAssetByAlamat(alamat)

        call.enqueue(object : Callback<ResponseData> {
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if (response.isSuccessful) {
                    val dats = response.body()
                    if (dats != null && dats.status == "ok") {
                        println("dt berhasil ${dats.harga_ada}")

                        val itemList = mutableListOf<Item>()
                        for (item in dats.data) {
                            itemList.add(Item(item.nama, item.jumlah))
                        }
                        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewAsset)
                        recyclerView.layoutManager = LinearLayoutManager(context)
                        val adapter = RecyclerViewAdapterAsset(itemList)
                        recyclerView.adapter = adapter

                    }else{

                    }

                } else {
                    // Handle error
                    println("dt gagal")
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                // Handle failure
                println("dt gagals")
            }
        })
    }

    private fun pindahKeLogin(){
        val intentLogin = Intent(this, Login::class.java)
        startActivity(intentLogin)
    }
}

private fun <T> Call<T>.enqueue(any: Any) {

}

object RetrofitClient {
    private const val BASE_URL = "https://uaspam.caridosen.online/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}

data class ResponseData(
    val status: String,
    val harga_ada: String,
    val data: List<Item>
)

data class Item(
    val nama: String,
    val jumlah: Double
)

class ViewHolderAsset(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textViewNama: TextView = itemView.findViewById(R.id.textViewNama)
    val textViewJumlah: TextView = itemView.findViewById(R.id.textViewJumlah)
}

class RecyclerViewAdapterAsset(private val dataList: List<Item>) : RecyclerView.Adapter<ViewHolderAsset>() {
    private lateinit var auth: FirebaseAuth
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAsset {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout_asset, parent, false)
        return ViewHolderAsset(view)
    }

    override fun onBindViewHolder(holder: ViewHolderAsset, position: Int) {
        val document = dataList[position]
        holder.textViewNama.text = document.nama.toString()
        holder.textViewJumlah.text = document.jumlah.toString()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

