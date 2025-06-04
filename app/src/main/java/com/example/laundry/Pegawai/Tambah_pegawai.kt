package com.example.laundry.Pegawai

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.laundry.R
import com.example.laundry.modeldata.ModelPegawai
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class Tambah_pegawai : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")

    private lateinit var etNamaP: EditText
    private lateinit var etAlamatP: EditText
    private lateinit var etNoHPP: EditText
    private lateinit var etCabangP: EditText
    private lateinit var btSimpanP: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val idPegawai = intent.getStringExtra("idPegawai")
        val isEdit = idPegawai != null

        // Set layout sesuai mode dengan error handling
        try {
            if (isEdit) {
                setContentView(R.layout.activity_edit_pegawai)
            } else {
                setContentView(R.layout.activity_tambah_pegawai)
            }
        } catch (e: Exception) {
            // Jika layout edit tidak ada, gunakan layout tambah
            setContentView(R.layout.activity_tambah_pegawai)
        }

        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Bind view dengan error handling
        try {
            if (isEdit) {
                // Coba gunakan ID untuk layout edit
                etNamaP = findViewById(R.id.et_edit_nama_pegawai)
                etAlamatP = findViewById(R.id.et_edit_alamat_pegawai)
                etNoHPP = findViewById(R.id.et_edit_hp_pegawai)
                etCabangP = findViewById(R.id.et_edit_cabang_pegawai)
                btSimpanP = findViewById(R.id.btn_simpan)
            } else {
                // Gunakan ID untuk layout tambah
                etNamaP = findViewById(R.id.etnama)
                etAlamatP = findViewById(R.id.etalamat)
                etNoHPP = findViewById(R.id.etnohp_pegawai)
                etCabangP = findViewById(R.id.etcabang)
                btSimpanP = findViewById(R.id.bttambah)
            }
        } catch (e: Exception) {
            // Fallback ke ID layout tambah jika ID edit tidak ada
            etNamaP = findViewById(R.id.etnama)
            etAlamatP = findViewById(R.id.etalamat)
            etNoHPP = findViewById(R.id.etnohp_pegawai)
            etCabangP = findViewById(R.id.etcabang)
            btSimpanP = findViewById(R.id.bttambah)
        }

        if (isEdit) {
            etNamaP.setText(intent.getStringExtra("namaPegawai"))
            etAlamatP.setText(intent.getStringExtra("alamatPegawai"))
            etNoHPP.setText(intent.getStringExtra("noHPPegawai"))
            etCabangP.setText(intent.getStringExtra("cabangPegawai"))
            btSimpanP.text = "Update"
        } else {
            btSimpanP.text = "Simpan"
        }

        btSimpanP.setOnClickListener {
            validasi(isEdit, idPegawai)
        }
    }

    private fun validasi(isEdit: Boolean, idPegawai: String?) {
        val nama = etNamaP.text.toString().trim()
        val alamat = etAlamatP.text.toString().trim()
        val noHP = etNoHPP.text.toString().trim()
        val cabang = etCabangP.text.toString().trim()

        if (nama.isEmpty()) {
            etNamaP.error = "Nama tidak boleh kosong"
            etNamaP.requestFocus()
            return
        }
        if (alamat.isEmpty()) {
            etAlamatP.error = "Alamat tidak boleh kosong"
            etAlamatP.requestFocus()
            return
        }
        if (noHP.isEmpty()) {
            etNoHPP.error = "Nomor HP tidak boleh kosong"
            etNoHPP.requestFocus()
            return
        }
        if (!noHP.matches(Regex("\\d{10,13}"))) {
            etNoHPP.error = "Nomor HP harus terdiri dari 10-13 angka"
            etNoHPP.requestFocus()
            return
        }
        if (cabang.isEmpty()) {
            etCabangP.error = "Cabang tidak boleh kosong"
            etCabangP.requestFocus()
            return
        }

        simpan(nama, alamat, noHP, cabang, isEdit, idPegawai)
    }

    private fun simpan(nama: String, alamat: String, noHP: String, cabang: String, isEdit: Boolean, idPegawai: String?) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val tanggalSekarang = sdf.format(Date())

        if (isEdit && idPegawai != null) {
            val dataUpdate = mapOf<String, Any>(
                "namaPegawai" to nama,
                "alamatPegawai" to alamat,
                "noHPPegawai" to noHP,
                "Cabang" to cabang,
                "terdaftar" to tanggalSekarang
            )

            myRef.child(idPegawai)
                .updateChildren(dataUpdate)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil diupdate", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal update data", Toast.LENGTH_SHORT).show()
                }

        } else {
            val pegawaiBaru = myRef.push()
            val pegawaiId = pegawaiBaru.key ?: return

            val dataBaru = ModelPegawai(
                idPegawai = pegawaiId,
                namaPegawai = nama,
                alamatPegawai = alamat,
                noHPPegawai = noHP,
                Cabang = cabang,
                terdaftar = tanggalSekarang
            )

            pegawaiBaru.setValue(dataBaru)
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