package com.example.laundry.Authorize

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.laundry.MainActivity
import com.example.laundry.R
import com.example.laundry.modeldata.ModelUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Login : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var database: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private val TAG = "LoginActivity"

    // Constants
    private val PREF_NAME = "LoginPrefs"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"
    private val KEY_LOGIN_TIME = "loginTime"
    private val KEY_USER_NAME = "userName"
    private val KEY_USER_PHONE = "userPhone"
    private val SESSION_DURATION_MS = 7 * 24 * 60 * 60 * 1000L // 7 hari dalam milliseconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Cek status login sebelum menampilkan UI login
        if (checkAutoLogin()) {
            return // Jika auto-login berhasil, keluar dari onCreate
        }

        setContentView(R.layout.activity_login)

        editTextName = findViewById(R.id.editTextName)
        editTextPassword = findViewById(R.id.editTextPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvRegister = findViewById(R.id.tv_register)

        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance().getReference("users")

        btnLogin.setOnClickListener {
            Log.d(TAG, "Login button clicked")
            val nama = editTextName.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            Log.d(TAG, "Name: $nama, Password length: ${password.length}")

            if (nama.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi Nama dan Password!", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "Empty name or password")
            } else {
                loginUser(nama, password)
            }
        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    /**
     * Cek apakah user sudah login dan session masih valid
     * @return true jika auto-login berhasil, false jika perlu login manual
     */
    private fun checkAutoLogin(): Boolean {
        val isLoggedIn = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        val loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0L)
        val currentTime = System.currentTimeMillis()
        val sessionExpired = (currentTime - loginTime) > SESSION_DURATION_MS

        Log.d(TAG, "Auto-login check: isLoggedIn=$isLoggedIn, sessionExpired=$sessionExpired")
        Log.d(TAG, "Login time: $loginTime, Current time: $currentTime")
        Log.d(TAG, "Time difference: ${currentTime - loginTime}ms, Session duration: ${SESSION_DURATION_MS}ms")

        if (isLoggedIn && !sessionExpired) {
            Log.d(TAG, "Auto-login: Session valid, redirecting to MainActivity")

            // Update login time untuk memperpanjang session
            updateLoginTime()

            redirectToMainActivity()
            return true
        } else if (isLoggedIn && sessionExpired) {
            Log.d(TAG, "Auto-login: Session expired, clearing login data")
            clearLoginSession()
            Toast.makeText(this, "Sesi telah berakhir, silakan login kembali", Toast.LENGTH_SHORT).show()
        } else {
            Log.d(TAG, "Auto-login: User not logged in")
        }

        return false
    }

    /**
     * Update waktu login untuk memperpanjang session
     */
    private fun updateLoginTime() {
        val editor = sharedPreferences.edit()
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
        editor.apply()
        Log.d(TAG, "Login time updated")
    }

    private fun redirectToMainActivity() {
        val userName = sharedPreferences.getString(KEY_USER_NAME, "")
        val userPhone = sharedPreferences.getString(KEY_USER_PHONE, "")

        Log.d(TAG, "Redirecting to MainActivity with user: $userName, phone: $userPhone")

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("nama", userName)
        intent.putExtra("phone", userPhone)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun saveLoginSession(userName: String, userPhone: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
        editor.putString(KEY_USER_NAME, userName)
        editor.putString(KEY_USER_PHONE, userPhone)
        editor.apply()

        Log.d(TAG, "Login session saved for user: $userName")
    }

    private fun clearLoginSession() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        Log.d(TAG, "Login session cleared")
    }

    private fun loginUser(nama: String, password: String) {
        Log.d(TAG, "Attempting to login user with name: $nama")

        database.child(nama).get().addOnSuccessListener { snapshot ->
            Log.d(TAG, "Database query successful")
            Log.d(TAG, "Snapshot exists: ${snapshot.exists()}")

            if (snapshot.exists()) {
                val user = snapshot.getValue(ModelUser::class.java)

                if (user != null) {
                    Log.d(TAG, "User object: $user")

                    if (user.password == password) {
                        Log.d(TAG, "Password match - Login successful")
                        Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()

                        // Simpan session login dengan durasi yang lebih lama
                        saveLoginSession(user.nama ?: "", nama)

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("nama", user.nama)
                        intent.putExtra("phone", nama)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w(TAG, "Password mismatch")
                        Toast.makeText(this, "Password salah", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e(TAG, "User object is null or failed to parse")
                    Toast.makeText(this, "Data pengguna tidak valid", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.w(TAG, "User with name $nama not found")
                Toast.makeText(this, "Nama belum terdaftar", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Database error: ${exception.message}")
            Toast.makeText(this, "Terjadi kesalahan: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Method untuk logout (panggil dari MainActivity atau activity lain jika diperlukan)
     */
    fun logout() {
        clearLoginSession()
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Method untuk memaksa logout dari activity lain
     */
    companion object {
        fun forceLogout(context: Context) {
            val sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            val intent = Intent(context, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}