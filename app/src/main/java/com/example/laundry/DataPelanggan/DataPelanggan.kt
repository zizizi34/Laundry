package com.example.laundry.DataPelanggan

import android.content.Intent
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Pelanggan.TambahPelanggan
import com.example.laundry.R
import com.example.laundry.adapter.adapter_data_pelanggan
import com.example.laundry.modeldata.ModelPelanggan
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DataPelanggan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var rvDATA_PELANGGAN: RecyclerView
    lateinit var fabDATA_PELANGGAN_TAMBAH: FloatingActionButton
    lateinit var pelangganList: ArrayList<ModelPelanggan>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pelanggan)
        init()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDATA_PELANGGAN.layoutManager=layoutManager
        rvDATA_PELANGGAN.setHasFixedSize(true)
        pelangganList = arrayListOf<ModelPelanggan>()
        getData()

        enableEdgeToEdge()

        // Redirect ke halaman pelanggan
        val tambahPelanggan = findViewById<FloatingActionButton>(R.id.fabDATA_PENGGUNA_TAMBAH)
        tambahPelanggan.setOnClickListener {
            val intent = Intent(this, TambahPelanggan::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    fun init(){
        rvDATA_PELANGGAN = findViewById(R.id.rvDATA_PELANGGAN)
        fabDATA_PELANGGAN_TAMBAH = findViewById(R.id.fabDATA_PENGGUNA_TAMBAH)
    }
    fun getData() {
        val query = myRef.orderByChild("idPelanggan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    pelangganList.clear()
                    for (dataSnapshot in snapshot.children){
                        val pegawai = dataSnapshot.getValue(ModelPelanggan::class.java)
                        pelangganList.add(pegawai!!)
                    }
                    val adapter = adapter_data_pelanggan(pelangganList)
                    rvDATA_PELANGGAN.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataPelanggan,error.message,Toast.LENGTH_SHORT)
            }
           })
       }
}