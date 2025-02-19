package com.example.laundry.Layanan



import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.Pelanggan.adapter_data_layanan
import com.example.laundry.R
import com.example.laundry.modeldata.ModelLayanan
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


// Penamaan kelas sebaiknya menggunakan PascalCase
class DataLayanan : AppCompatActivity() {

    // Inisialisasi Firebase
    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("layanan")

    private lateinit var rvdatalayanan: RecyclerView
    private lateinit var fab_tambah_layanan: FloatingActionButton
    private var listLayanan = arrayListOf<ModelLayanan>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_layanan)

        // Enable edge-to-edge untuk tampilan modern
        enableEdgeToEdge()

        initViews()
        setupRecyclerView()
        setupListeners()
        getData()

        // Atur padding agar tidak terhalang oleh system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initViews() {
        rvdatalayanan = findViewById(R.id.rvdatalayanan)
        fab_tambah_layanan = findViewById(R.id.fab_tambah_layanan)
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        rvdatalayanan.layoutManager = layoutManager
        rvdatalayanan.setHasFixedSize(true)
    }

    private fun setupListeners() {
        fab_tambah_layanan.setOnClickListener {
            // Pastikan kelas activity untuk tambah pelanggan sudah terdaftar di AndroidManifest.xml
            val intent = Intent(this, TambahLayanan::class.java)
            startActivity(intent)
        }
    }

    private fun getData() {
        // Ambil data berdasarkan idPelanggan dan batasi 100 data terakhir
        val query = myRef.orderByChild("idLayanan").limitToLast(100)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listLayanan.clear()
                if (snapshot.exists()){
                    for (dataSnapshot in snapshot.children) {
                        val layanan = dataSnapshot.getValue(ModelLayanan::class.java)
                        layanan?.let { listLayanan.add(it) }
                    }
                    // Perbarui adapter RecyclerView
                    val adapter = adapter_data_layanan(listLayanan)
                    rvdatalayanan.adapter = adapter
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DataLayanan, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}