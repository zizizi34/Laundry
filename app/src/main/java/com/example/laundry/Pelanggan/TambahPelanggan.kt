package com.example.laundry.Pelanggan

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
import com.example.laundry.modeldata.ModelPelanggan
import com.google.firebase.database.FirebaseDatabase

class TambahPelanggan : AppCompatActivity() {
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("pelanggan")
    lateinit var tvJudul: TextView
    lateinit var etNama: EditText
    lateinit var etAlamat: EditText
    lateinit var etNoHP: EditText
    lateinit var etCabang: EditText
    lateinit var btSimpan: Button

    var isEdit = false

    var id_pelanggan:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_pelanggan)
        enableEdgeToEdge()
        init()
        btSimpan.setOnClickListener {
            simpan()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        tvJudul = findViewById(R.id.tv_title)
        etNama = findViewById(R.id.et_nama)
        etAlamat = findViewById(R.id.et_alamat)
        etNoHP = findViewById(R.id.et_hp)
        etCabang = findViewById(R.id.etcabang)
        btSimpan = findViewById(R.id.btn_simpan)

    }

    fun hidup (){
        etNama.isEnabled=true
        etAlamat.isEnabled=true
        etNoHP.isEnabled=true
        etCabang.isEnabled=true

    }
    fun update (){
        val pelangganRef = database.getReference("pelanggan").child(id_pelanggan)
        val updateData = mutableMapOf <String, Any>()
        updateData["namaPelanggan"]= etNama.text.toString()
        updateData["alamatPelanggan"]= etAlamat.text.toString()
        updateData["noHPPelanggan"]= etNoHP.text.toString()
        updateData["cabangPelanggan"]= etCabang.text.toString()

        pelangganRef.updateChildren(updateData).addOnSuccessListener {
            Toast.makeText(this@TambahPelanggan, "Data Pelanggan Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                finish()
        }.addOnFailureListener{
            Toast.makeText(this@TambahPelanggan, "Data Pelanggan Gagal Diperbarui", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cekValidasi(): Boolean {
        when {
            etNama.text.isEmpty() -> {
                etNama.error = getString(R.string.validasi_nama_pelanggan)
                etNama.requestFocus()
                return false
            }

            etAlamat.text.isEmpty() -> {
                etAlamat.error = getString(R.string.validasi_alamat_pelanggan)
                etAlamat.requestFocus()
                return false
            }

            etNoHP.text.isEmpty() -> {
                etNoHP.error = getString(R.string.validasi_nohp_pelanggan)
                etNoHP.requestFocus()
                return false
            }

            etCabang.text.isEmpty() -> {
                etCabang.error = getString(R.string.validasi_cabang_pelanggan)
                etCabang.requestFocus()
                return false
            }
        }
        return true

        if (btSimpan.text.equals("Simpan")) {
            simpan()
        } else if (btSimpan.text.equals("Edit")) {
            hidup()
            etNama.requestFocus()
            btSimpan.text = "Perbarui"
        } else if (btSimpan.text.equals("Perbarui")) {
            update()
        }

    }


    private fun simpan() {
        if (!cekValidasi()) return

        val pelangganBaru = myRef.push()
        val pelangganId = pelangganBaru.key ?: return
        val timestamp = System.currentTimeMillis().toString()

        val data = ModelPelanggan(
            id_pelanggan = pelangganId,
            namaPelanggan = etNama.text.toString(),
            alamatPelanggan = etAlamat.text.toString(),
            noHpPelanggan = etNoHP.text.toString(),
            cabang = etCabang.text.toString(),
            timestamp
        )

        pelangganBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.sukses_simpan_pelanggan), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.gagal_simpan_pelanggan), Toast.LENGTH_SHORT).show()
            }
    }
}
