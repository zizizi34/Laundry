package com.example.laundry.Transaksi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.adapter.adapter_pilih_tambahan
import com.example.laundry.modeldata.ModeltransaksiTambahan
import com.google.firebase.database.*

class PilihTambahan : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private val listTambahan = mutableListOf<ModeltransaksiTambahan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pilih_tambahan)

        recyclerView = findViewById(R.id.rvDATA_TAMBAHAN)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dbRef = FirebaseDatabase.getInstance().getReference("tambahan")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listTambahan.clear()
                for (data in snapshot.children) {
                    val tambahan = data.getValue(ModeltransaksiTambahan::class.java)
                    if (tambahan != null) {
                        listTambahan.add(tambahan)
                    }
                }

                val adapter = adapter_pilih_tambahan(listTambahan) { selectedTambahan ->
                    val intent = Intent()
                    intent.putExtra("idLayanan", selectedTambahan.idLayanan)
                    intent.putExtra("namaLayanan", selectedTambahan.namaLayanan)
                    intent.putExtra("hargaLayanan", selectedTambahan.hargaLayanan)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }

                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PilihTambahan, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
