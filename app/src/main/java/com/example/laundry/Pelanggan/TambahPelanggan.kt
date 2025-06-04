package com.example.laundry.Pelanggan

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.laundry.R
import com.example.laundry.modeldata.ModelPelanggan
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class TambahPelanggan : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pelanggan")

    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNoHP: EditText
    private lateinit var etCabang: EditText
    private lateinit var btSimpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val idPelanggan = intent.getStringExtra("idPelanggan")
        val isEdit = idPelanggan != null

        // Set layout sesuai mode
        if (isEdit) {
            setContentView(R.layout.activity_edit_pelanggan)
        } else {
            setContentView(R.layout.activity_tambah_pelanggan)
        }

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bind view sesuai layout
        etNama = findViewById(if (isEdit) R.id.et_edit_nama else R.id.et_nama)
        etAlamat = findViewById(if (isEdit) R.id.et_edit_alamat else R.id.et_alamat)
        etNoHP = findViewById(if (isEdit) R.id.et_edit_hp else R.id.et_hp)
        etCabang = findViewById(if (isEdit) R.id.et_editcabang else R.id.etcabang)
        btSimpan = findViewById(R.id.btn_simpan)

        if (isEdit) {
            etNama.setText(intent.getStringExtra("namaPelanggan"))
            etAlamat.setText(intent.getStringExtra("alamatPelanggan"))
            etNoHP.setText(intent.getStringExtra("noHPPelanggan"))
            etCabang.setText(intent.getStringExtra("cabangPelanggan"))
        }

        btSimpan.setOnClickListener {
            validasi(isEdit, idPelanggan)
        }
    }

    private fun validasi(isEdit: Boolean, idPelanggan: String?) {
        val nama = etNama.text.toString().trim()
        val alamat = etAlamat.text.toString().trim()
        val noHP = etNoHP.text.toString().trim()
        val cabang = etCabang.text.toString().trim()

        if (nama.isEmpty()) {
            etNama.error = "Nama tidak boleh kosong"
            etNama.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamat.error = "Alamat tidak boleh kosong"
            etAlamat.requestFocus()
            return
        }
        if (noHP.isEmpty()) {
            etNoHP.error = "Nomor HP tidak boleh kosong"
            etNoHP.requestFocus()
            return
        }
        if (!noHP.matches(Regex("\\d{10,13}"))) {
            etNoHP.error = "Nomor HP harus terdiri dari 10-13 angka"
            etNoHP.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabang.error = "Cabang tidak boleh kosong"
            etCabang.requestFocus()
            return
        }

        simpan(nama, alamat, noHP, cabang, isEdit, idPelanggan)
    }

    private fun simpan(nama: String, alamat: String, noHP: String, cabang: String, isEdit: Boolean, idPelanggan: String?) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val tanggalSekarang = sdf.format(Date())

        if (isEdit && idPelanggan != null) {
            val dataUpdate = mapOf<String, Any>(
                "namaPelanggan" to nama,
                "alamatPelanggan" to alamat,
                "noHPPelanggan" to noHP,
                "cabangPelanggan" to cabang,
                "tanggalTerdaftar" to tanggalSekarang
            )

            myRef.child(idPelanggan)
                .updateChildren(dataUpdate)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal update data", Toast.LENGTH_SHORT).show()
                }

        } else {
            val pelangganBaru = myRef.push()
            val pelangganId = pelangganBaru.key ?: return

            val dataBaru = ModelPelanggan(
                id_pelanggan = pelangganId,
                namaPelanggan = nama,
                alamatPelanggan = alamat,
                noHpPelanggan = noHP,
                cabang = cabang,
                terdaftar_pelanggan = tanggalSekarang
            )

            pelangganBaru.setValue(dataBaru)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
