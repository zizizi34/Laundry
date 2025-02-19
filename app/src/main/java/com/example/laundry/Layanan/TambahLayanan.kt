package com.example.laundry.Layanan

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.laundry.R
import com.example.laundry.modeldata.ModelLayanan
import com.google.firebase.database.FirebaseDatabase

class TambahLayanan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("layanan")

    // Inisialisasi View
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etnama_layanan: EditText
    lateinit var etharga_layanan: EditText
    lateinit var etnama_cabang: EditText
    lateinit var btSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_layanan)
        enableEdgeToEdge()

        init()
        simpan()

        // Mengatur Inset untuk StatusBar & NavigationBar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tambah_layanan)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Inisialisasi View dari XML
    fun init() {
        tvJudul = findViewById(R.id.tvjudul_layanan)
        etNama = findViewById(R.id.etnama_layanan)
        etnama_layanan= findViewById(R.id.etnama_layanan)
        etharga_layanan = findViewById(R.id.etharga_layanan)
        etnama_cabang = findViewById(R.id.etnama_cabang)
        btSimpan = findViewById(R.id.bttambah)
    }

    // Fungsi Simpan Data ke Firebase
    fun simpan() {
        btSimpan.setOnClickListener {
            val nama = etNama.text.toString()
            val namalayanan = etnama_layanan.text.toString()
            val etharga = etharga_layanan.text.toString()
            val cabang = etnama_cabang.text.toString()

            if (nama.isEmpty() || namalayanan.isEmpty() || etharga.isEmpty() || cabang.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val layananBaru = myRef.push()
            val layananId = layananBaru.key ?: "Unknown"
            val data = ModelLayanan(layananId, nama, etharga, cabang)

            layananBaru.setValue(data)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        this.getString(R.string.berhasil_tambah_layanan),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        this.getString(R.string.layanan_tambah_gagal),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}