package com.example.laundry

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.laundry.DataPelanggan.DataPelanggan
import com.example.laundry.DataPegawai.Data_pegawai
import com.example.laundry.Layanan.DataLayanan
import com.example.laundry.Laporan.DataLaporan
import com.example.laundry.Profile.Akun
import com.example.laundry.R
import com.example.laundry.Tambahan.DataTambahan
import com.example.laundry.Transaksi.DataTranssaksiActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // redirect to pelanggan
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

        val appDataTransaksi = findViewById<ConstraintLayout>(R.id.clTransaksi)
        appDataTransaksi.setOnClickListener{
            val intent = Intent(this, DataTranssaksiActivity::class.java)
            startActivity(intent)
        }

        val appTambahan = findViewById<CardView>(R.id.cardtambahan)
        appTambahan.setOnClickListener{
            val intent = Intent(this, DataTambahan::class.java)
            startActivity(intent)
        }

        val appAkun = findViewById<CardView>(R.id.cardAkun)
        appAkun.setOnClickListener{
            val intent = Intent(this, Akun::class.java)
            startActivity(intent)
        }
        val greetingTextView: TextView = findViewById(R.id.tvhello)
        greetingTextView.text = getGreetingMessage()

        val dateTextView: TextView = findViewById(R.id.date)
        dateTextView.text = getCurrentDate()

    }
    private fun getGreetingMessage(): String {
        val sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val nama = sharedPref.getString("userName", "Pengguna") ?: "Pengguna"
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (currentHour) {
            in 5..11 -> getString(R.string.greeting_morning, nama)
            in 12..14 -> getString( R.string.greeting_afternoon, nama)
            in 15..17 -> getString(R.string.greeting_evening, nama)
            else -> getString(R.string.greeting_night, nama)
        }
    }



    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        return dateFormat.format(calendar.time)
    }


}



