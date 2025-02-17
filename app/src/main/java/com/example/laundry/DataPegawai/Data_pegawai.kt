package com.example.laundry.DataPelanggan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Pelanggan.tambah_pegawai
import com.example.laundry.R
import com.example.laundry.adapter.adapter_data_pegawai
import com.example.laundry.modeldata.ModelPegawai
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Data_pegawai : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")
    lateinit var rvDATA_PEGAWAI: RecyclerView
    lateinit var fabDATA_PEGAWAI_TAMBAH: FloatingActionButton
    lateinit var pegawaiList: ArrayList<ModelPegawai>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pegawai)
        init()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvDATA_PEGAWAI.layoutManager = layoutManager
        rvDATA_PEGAWAI.setHasFixedSize(true)
        pegawaiList = arrayListOf<ModelPegawai>()
        getData()
        enableEdgeToEdge()
        // Redirect ke halaman pegawai
        val tambahPegawai = findViewById<FloatingActionButton>(R.id.fab_tambah)
        tambahPegawai.setOnClickListener {
            val intent = Intent(this, tambah_pegawai::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun init() {
        rvDATA_PEGAWAI = findViewById(R.id.rvdatapelanggan)
        fabDATA_PEGAWAI_TAMBAH = findViewById(R.id.fab_tambah)
    }

    fun getData() {
        val query = myRef.orderByChild("idPegawai").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    pegawaiList.clear()
                    for (dataSnapshot in snapshot.children) {
                        val pegawai = dataSnapshot.getValue(ModelPegawai::class.java)
                        pegawaiList.add(pegawai!!)
                    }
                    val adapter = adapter_data_pegawai(pegawaiList)
                    rvDATA_PEGAWAI.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Data_pegawai, error.message, Toast.LENGTH_SHORT)
            }
            })
      }
}