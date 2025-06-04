package com.example.laundry.Authorize

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundry.R
import com.example.laundry.modeldata.ModelUser
import com.google.firebase.database.FirebaseDatabase

class Register : AppCompatActivity() {

    private lateinit var editTextNama: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views sesuai dengan ID di layout XML
        editTextNama = findViewById(R.id.editTextNama)
        editTextPassword = findViewById(R.id.editTextPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLogin = findViewById(R.id.tv_login_link)

        btnRegister.setOnClickListener {
            val nama = editTextNama.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (nama.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi Nama dan Password!", Toast.LENGTH_SHORT).show()
            } else {
                registerUser(nama, password)
            }
        }

        // Handle click untuk navigasi ke Login
        tvLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(nama: String, password: String) {
        val db = FirebaseDatabase.getInstance().getReference("users")

        // Check if user already exists
        db.child(nama).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                Toast.makeText(this, "Nama sudah terdaftar, silakan gunakan nama lain", Toast.LENGTH_SHORT).show()
            } else {
                // Create new user - menggunakan nama sebagai key
                val user = ModelUser(nama = nama, password = password)

                db.child(nama).setValue(user)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Register berhasil!", Toast.LENGTH_SHORT).show()

                        // Navigate to Login activity
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Gagal: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Terjadi kesalahan: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}