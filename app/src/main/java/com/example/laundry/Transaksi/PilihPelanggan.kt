package com.example.laundry.Transaksi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.adapter.adapter_pilih_pelanggan
import com.example.laundry.modeldata.ModelPelanggan
import com.google.firebase.database.*

class PilihPelanggan : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private val listPelanggan = mutableListOf<ModelPelanggan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_pelanggan)

        recyclerView = findViewById(R.id.rvDATA_TRANSAKSI_PELANGGAN)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbRef = FirebaseDatabase.getInstance().getReference("pelanggan")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listPelanggan.clear()
                for (data in snapshot.children) {
                    val pelanggan = data.getValue(ModelPelanggan::class.java)
                    if (pelanggan != null) {
                        listPelanggan.add(pelanggan)
                    }
                }

                val adapter = adapter_pilih_pelanggan(listPelanggan) { selectedPelanggan ->
                    val intent = Intent()
                    intent.putExtra("idPelanggan", selectedPelanggan.id_pelanggan)
                    intent.putExtra("namaPelanggan", selectedPelanggan.namaPelanggan)
                    intent.putExtra("noHPPelanggan", selectedPelanggan.noHpPelanggan)
                    setResult(RESULT_OK, intent)
                    finish()
                }


                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PilihPelanggan, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
            })
        }
}
