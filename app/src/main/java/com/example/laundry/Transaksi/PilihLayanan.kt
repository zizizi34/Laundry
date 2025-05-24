package com.example.laundry.Transaksi

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.adapter.adapter_pilih_layanan
import com.example.laundry.adapter.adapter_pilih_pelanggan
import com.example.laundry.modeldata.ModelLayanan
import com.example.laundry.modeldata.ModelPelanggan
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PilihLayanan: AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private val listLayanan = mutableListOf<ModelLayanan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_layanan)

        recyclerView = findViewById(R.id.rvDATA_TRANSAKSI_LAYANAN)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbRef = FirebaseDatabase.getInstance().getReference("layanan")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listLayanan.clear()
                for (data in snapshot.children) {
                    val layanan = data.getValue(ModelLayanan::class.java)
                    if (layanan != null) {
                        listLayanan.add(layanan)
                    }
                }

                val adapter = adapter_pilih_layanan(listLayanan) { selectedLayanan ->
                    val intent = Intent()
                    intent.putExtra("idLayanan", selectedLayanan.id_layanan)
                    intent.putExtra("namaLayanan", selectedLayanan.nama_layanan)
                    intent.putExtra("hargaLayanan", selectedLayanan.harga_layanan)
                    setResult(RESULT_OK, intent)
                    finish()
                }


                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PilihLayanan, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}