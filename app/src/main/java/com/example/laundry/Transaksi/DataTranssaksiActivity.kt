package com.example.laundry.Transaksi

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.modeldata.ModeltransaksiTambahan
import com.google.firebase.FirebaseApp

class DataTranssaksiActivity : AppCompatActivity() {

    private lateinit var tvPelangganNama: TextView
    private lateinit var tvPelangganNoHP: TextView
    private lateinit var tvLayananNama: TextView
    private lateinit var tvLayananHarga: TextView
    private lateinit var rvLayananTambahan: RecyclerView
    private lateinit var btnPilihPelanggan: Button
    private lateinit var btnPilihLayanan: Button
    private lateinit var btnTambahan: Button
    private lateinit var btn_proses: Button

    private val dataList = mutableListOf<ModeltransaksiTambahan>()

    private val pilihPelanggan = 1
    private val pilihLayanan = 2
    private val pilihLayananTambahan = 3

    private var idPelanggan: String = ""
    private var idCabang: String = ""
    private var namaPelanggan: String = ""
    private var noHP: String = ""
    private var idLayanan: String = ""
    private var namaLayanan: String = ""
    private var hargaLayanan: String = ""
    private var idPegawai: String = ""

    private lateinit var sharedPref: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_data_transsaksi)

        sharedPref = getSharedPreferences("user_data", MODE_PRIVATE)
        idCabang = sharedPref.getString("idCabang", null).toString()
        idPegawai = sharedPref.getString("idPegawai", null).toString()

        init()
        FirebaseApp.initializeApp(this)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        rvLayananTambahan.layoutManager = layoutManager
        rvLayananTambahan.setHasFixedSize(true)

        btnPilihPelanggan.setOnClickListener {
            val intent = Intent(this, PilihPelanggan::class.java)
            startActivityForResult(intent, pilihPelanggan)
        }

        btnPilihLayanan.setOnClickListener {
            val intent = Intent(this, PilihLayanan::class.java)
            startActivityForResult(intent, pilihLayanan)
        }

        btnTambahan.setOnClickListener {
            val intent = Intent(this, PilihTambahan::class.java)
            startActivityForResult(intent, pilihLayananTambahan)
        }

        btn_proses.setOnClickListener {
            val intent = Intent(this, KonfirmasiTransaksi::class.java)
            intent.putExtra("namaPelanggan", namaPelanggan)
            intent.putExtra("noHP", noHP)
            intent.putExtra("namaLayanan", namaLayanan)
            intent.putExtra("hargaLayanan", hargaLayanan)
            intent.putExtra("listTambahan", ArrayList(dataList))
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        try {
            tvPelangganNama = findViewById(R.id.tvNamaPelanggan)
            tvPelangganNoHP = findViewById(R.id.tvPelangganNoHP)
            tvLayananNama = findViewById(R.id.tvNamaLayanan)
            tvLayananHarga = findViewById(R.id.tvLayananHarga)
            rvLayananTambahan = findViewById(R.id.recycler_tambahan)
            btnPilihPelanggan = findViewById(R.id.btn_pilih_pelanggan)
            btnPilihLayanan = findViewById(R.id.btn_pilih_layanan)
            btnTambahan = findViewById(R.id.btn_tambahan)
            btn_proses = findViewById(R.id.btn_proses)
        } catch (e: Exception) {
            Log.e("DataTransaksi", "Error initializing views: ${e.message}")
        }
    }

    @SuppressLint("StringFormatInvalid")
    @Deprecated("Use Activity Result API instead")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            pilihPelanggan -> {
                if (resultCode == RESULT_OK && data != null) {
                    idPelanggan = data.getStringExtra("idPelanggan") ?: ""
                    val nama = data.getStringExtra("namaPelanggan") ?: ""
                    val nomorHP = data.getStringExtra("noHPPelanggan") ?: ""

                    tvPelangganNama.text = getString(R.string.customer_name_label, nama)
                    tvPelangganNoHP.text = getString(R.string.customer_phone_label, nomorHP)

                    namaPelanggan = nama
                    noHP = nomorHP
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, getString(R.string.cancel_customer_selection), Toast.LENGTH_SHORT).show()
                }
            }

            pilihLayanan -> {
                if (resultCode == RESULT_OK && data != null) {
                    idLayanan = data.getStringExtra("idLayanan") ?: ""
                    val nama = data.getStringExtra("namaLayanan") ?: ""
                    val harga = data.getStringExtra("hargaLayanan") ?: ""

                    tvLayananNama.text = getString(R.string.service_name_label, nama)
                    tvLayananHarga.text = getString(R.string.service_price_label, harga)

                    namaLayanan = nama
                    hargaLayanan = harga
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, getString(R.string.cancel_service_selection), Toast.LENGTH_SHORT).show()
                }
            }

            pilihLayananTambahan -> {
                if (resultCode == RESULT_OK && data != null) {
                    val id = data.getStringExtra("idLayanan") ?: ""
                    val nama = data.getStringExtra("namaLayanan") ?: ""
                    val harga = data.getStringExtra("hargaLayanan") ?: ""

                    val tambahan = ModeltransaksiTambahan(id, nama, harga)
                    dataList.add(tambahan)

                    rvLayananTambahan.adapter = com.example.laundry.adapter.adapter_pilih_tambahan(dataList) {
                        // Klik item tambahan jika perlu
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, getString(R.string.cancel_additional_service_selection), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
