package com.example.laundry.DataPelanggan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.modeldata.ModelPelanggan
import com.example.laundry.Pelanggan.TambahPelanggan
import com.example.laundry.adapter.adapter_data_pelanggan
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class DataPelanggan : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pelanggan")

    private lateinit var rvDataPelanggan: RecyclerView
    private lateinit var fabTambahPelanggan: FloatingActionButton
    private lateinit var pelangganList: ArrayList<ModelPelanggan>
    private lateinit var adapter: adapter_data_pelanggan
    private var valueEventListener: ValueEventListener? = null

    private fun init() {
        rvDataPelanggan = findViewById(R.id.rvDATA_PELANGGAN)
        fabTambahPelanggan = findViewById(R.id.fabDATA_PENGGUNA_TAMBAH)

        pelangganList = ArrayList()
        adapter = adapter_data_pelanggan(
            pelangganList,
            onItemClick = { pelanggan ->
                // Untuk edit pelanggan
                val intent = Intent(this@DataPelanggan, TambahPelanggan::class.java).apply {
                    putExtra("idPelanggan", pelanggan.id_pelanggan)
                    putExtra("namaPelanggan", pelanggan.namaPelanggan)
                    putExtra("alamatPelanggan", pelanggan.alamatPelanggan)
                    putExtra("noHPPelanggan", pelanggan.noHpPelanggan)
                    putExtra("cabangPelanggan", pelanggan.cabang)
                    putExtra("tanggalTerdaftar", pelanggan.terdaftar_pelanggan)
                }
                startActivity(intent)
            },
            onDeleteClick = { pelanggan ->
                // Untuk delete pelanggan dengan konfirmasi
                showDeleteConfirmation(pelanggan)
            }
        )

        rvDataPelanggan.layoutManager = LinearLayoutManager(this)
        rvDataPelanggan.adapter = adapter
    }

    private fun getDATA() {
        valueEventListener?.let { myRef.removeEventListener(it) } // hapus listener lama

        val query = myRef.orderByChild("idPelanggan").limitToLast(100)
        valueEventListener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pelangganList.clear()
                for (dataSnapshot in snapshot.children) {
                    val pelanggan = dataSnapshot.getValue(ModelPelanggan::class.java)
                    pelanggan?.let { pelangganList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataPelanggan, "Gagal ambil data: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("DataPelanggan", "Firebase Error: ${error.toException()}")
            }
        })
    }

    // Fungsi untuk menampilkan konfirmasi delete
    private fun showDeleteConfirmation(pelanggan: ModelPelanggan) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Apakah Anda yakin ingin menghapus data pelanggan ${pelanggan.namaPelanggan ?: "ini"}?")

        builder.setPositiveButton("Ya") { _, _ ->
            deletePelanggan(pelanggan)
        }

        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    // Fungsi untuk menghapus pelanggan
    fun deletePelanggan(pelanggan: ModelPelanggan) {
        val idPelanggan = pelanggan.id_pelanggan
        if (!idPelanggan.isNullOrEmpty()) {
            val pelangganRef = myRef.child(idPelanggan)
            pelangganRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Data pelanggan berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal menghapus data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("DataPelanggan", "Delete Error: ${exception}")
                }
        } else {
            Toast.makeText(this, "ID Pelanggan tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pelanggan)

        init()
        getDATA()

        fabTambahPelanggan.setOnClickListener {
            val intent = Intent(this, TambahPelanggan::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        getDATA() // Refresh data setiap balik ke activity ini
    }

    override fun onDestroy() {
        super.onDestroy()
        valueEventListener?.let { myRef.removeEventListener(it)}
        }
}
