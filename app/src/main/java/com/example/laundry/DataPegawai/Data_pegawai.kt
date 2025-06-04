package com.example.laundry.DataPegawai

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
import com.example.laundry.modeldata.ModelPegawai
import com.example.laundry.Pegawai.Tambah_pegawai
import com.example.laundry.adapter.adapter_data_pegawai
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class Data_pegawai : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")

    private lateinit var rvDataPegawai: RecyclerView
    private lateinit var fabTambahPegawai: FloatingActionButton
    private lateinit var pegawaiList: ArrayList<ModelPegawai>
    private lateinit var adapter: adapter_data_pegawai
    private var valueEventListener: ValueEventListener? = null

    private fun init() {
        rvDataPegawai = findViewById(R.id.rvdatapelanggan)
        fabTambahPegawai = findViewById(R.id.fab_tambah)

        pegawaiList = ArrayList()
        adapter = adapter_data_pegawai(
            pegawaiList,
            onItemClick = { pegawai ->
                // Untuk edit pegawai
                val intent = Intent(this@Data_pegawai, Tambah_pegawai::class.java).apply {
                    putExtra("idPegawai", pegawai.idPegawai)
                    putExtra("namaPegawai", pegawai.namaPegawai)
                    putExtra("alamatPegawai", pegawai.alamatPegawai)
                    putExtra("noHPPegawai", pegawai.noHPPegawai)
                    putExtra("cabangPegawai", pegawai.Cabang)
                    putExtra("tanggalTerdaftar", pegawai.terdaftar)
                }
                startActivity(intent)
            },
            onDeleteClick = { pegawai ->
                // Untuk delete pegawai dengan konfirmasi
                showDeleteConfirmation(pegawai)
            }
        )

        rvDataPegawai.layoutManager = LinearLayoutManager(this)
        rvDataPegawai.adapter = adapter
    }

    private fun getData() {
        valueEventListener?.let { myRef.removeEventListener(it) } // hapus listener lama

        val query = myRef.orderByChild("idPegawai").limitToLast(100)
        valueEventListener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pegawaiList.clear()
                for (dataSnapshot in snapshot.children) {
                    val pegawai = dataSnapshot.getValue(ModelPegawai::class.java)
                    pegawai?.let { pegawaiList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Data_pegawai, "Gagal ambil data: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("DataPegawai", "Firebase Error: ${error.toException()}")
            }
        })
    }

    // Fungsi untuk menampilkan konfirmasi delete
    private fun showDeleteConfirmation(pegawai: ModelPegawai) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Apakah Anda yakin ingin menghapus data pegawai ${pegawai.namaPegawai ?: "ini"}?")

        builder.setPositiveButton("Ya") { _, _ ->
            deletePegawai(pegawai)
        }

        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    // Fungsi untuk menghapus pegawai
    private fun deletePegawai(pegawai: ModelPegawai) {
        val idPegawai = pegawai.idPegawai
        if (!idPegawai.isNullOrEmpty()) {
            val pegawaiRef = myRef.child(idPegawai)
            pegawaiRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Data pegawai berhasil dihapus", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal menghapus data: ${exception.message}", Toast.LENGTH_SHORT).show()
                    Log.e("DataPegawai", "Delete Error: ${exception}")
                }
        } else {
            Toast.makeText(this, "ID Pegawai tidak valid", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_pegawai)

        init()
        getData()

        fabTambahPegawai.setOnClickListener {
            val intent = Intent(this, Tambah_pegawai::class.java)
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
        getData() // Refresh data setiap balik ke activity ini
    }

    override fun onDestroy() {
        super.onDestroy()
        valueEventListener?.let { myRef.removeEventListener(it) }
    }
}