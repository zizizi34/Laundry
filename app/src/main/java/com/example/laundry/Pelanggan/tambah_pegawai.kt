package com.example.laundry.Pelanggan
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.example.laundry.R
import com.example.laundry.modeldata.ModelPegawai

class tambah_pegawai : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("pegawai")

    private lateinit var etNamaP: EditText
    private lateinit var etAlamatP: EditText
    private lateinit var etNoHPP: EditText
    private lateinit var etCabangP: EditText
    private lateinit var btSimpanP: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_pegawai)

        init()
        btSimpanP.setOnClickListener { simpan() }
    }

    private fun init() {
        etNamaP = findViewById(R.id.etnama)
        etAlamatP = findViewById(R.id.etcabang)
        etNoHPP = findViewById(R.id.etnohp_pegawai)
        etCabangP = findViewById(R.id.etstatus)
        btSimpanP = findViewById(R.id.bttambah)
    }

    private fun cekValidasi(): Boolean {
        when {
            etNamaP.text.isEmpty() -> {
                etNamaP.error = getString(R.string.validasi_nama_pelanggan)
                etNamaP.requestFocus()
                return false
            }
            etAlamatP.text.isEmpty() -> {
                etAlamatP.error = getString(R.string.validasi_alamat_pelanggan)
                etAlamatP.requestFocus()
                return false
            }
            etNoHPP.text.isEmpty() -> {
                etNoHPP.error = getString(R.string.validasi_nohp_pelanggan)
                etNoHPP.requestFocus()
                return false
            }
            etCabangP.text.isEmpty() -> {
                etCabangP.error = getString(R.string.validasi_cabang_pelanggan)
                etCabangP.requestFocus()
                return false
            }
        }
        return true
    }

    private fun simpan() {
        if (!cekValidasi()) return

        val pegawaiBaru = myRef.push()
        val pegawaiId = pegawaiBaru.key ?: return
        val timestamp = System.currentTimeMillis().toString()

        val data = ModelPegawai(
            pegawaiId,
            etNamaP.text.toString(),
            etAlamatP.text.toString(),
            etNoHPP.text.toString(),
            etCabangP.text.toString(),
            timestamp
        )

        pegawaiBaru.setValue(data)
            .addOnSuccessListener {
                Toast.makeText(this, getString(R.string.sukses_simpan_pegawai), Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, getString(R.string.gagal_simpan_pegawai), Toast.LENGTH_SHORT).show()
            }
      }
}