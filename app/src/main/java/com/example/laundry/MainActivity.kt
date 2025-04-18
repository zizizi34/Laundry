package com.example.laundry

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.laundry.DataPelanggan.DataPelanggan
import com.example.laundry.DataPelanggan.Data_pegawai
import com.example.laundry.Layanan.DataLayanan
import com.example.laundry.Laporan.DataLaporan
import com.example.laundry.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


//        redirect to pelanggan
        val appDataPelanggan = findViewById<ConstraintLayout>(R.id.clPelanggan)
        appDataPelanggan.setOnClickListener{
            val intent = Intent(this, DataPelanggan::class.java)
            startActivity(intent)
        }

        val appDataPegawai = findViewById<CardView>(R.id.cardpegawai)
        appDataPegawai.setOnClickListener{
            val intent = Intent(this, Data_pegawai::class.java)
            startActivity(intent)
        }

        val appDataLayanan = findViewById<CardView>(R.id.cardlayanan)
        appDataLayanan.setOnClickListener{
            val intent = Intent(this, DataLayanan::class.java)
            startActivity(intent)
        }

        val appDataLaporan = findViewById<ConstraintLayout>(R.id.cardlaporan)
        appDataLaporan.setOnClickListener{
            val intent = Intent(this, DataLaporan::class.java)
            startActivity(intent)
        }




        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}