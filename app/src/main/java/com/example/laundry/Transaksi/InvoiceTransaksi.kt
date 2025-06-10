package com.example.laundry.Transaksi

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ColorSpace.Model
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.adapter.adapter_data_tambahan
import com.example.laundry.modeldata.ModelInvoice
import com.example.laundry.modeldata.ModeltransaksiTambahan
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class InvoiceTransaksi : AppCompatActivity() {

    private lateinit var tvTanggal: TextView
    private lateinit var tvIdTransaksi: TextView
    private lateinit var tvNamaPelanggan: TextView
    private lateinit var tvNoHP: TextView
    private var tvStatus: TextView? = null
    private lateinit var tvLayananUtama: TextView
    private lateinit var tvHargaLayanan: TextView
    private var rvTambahan: RecyclerView? = null
    private lateinit var tvSubtotalTambahan: TextView
    private lateinit var tvTotalBayar: TextView
    private lateinit var btnCetak: Button
    private lateinit var btnKirimWhatsapp: Button

    private val listTambahan = ArrayList<ModeltransaksiTambahan>()
    private var adapter: adapter_data_tambahan? = null

    // Property untuk menyimpan nomor HP pelanggan
    private var noHPPelanggan: String = ""

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothSocket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null

    private val printerMAC = "DC:0D:51:A7:FF:7A"
    private val printerUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSIONS = 100
        private const val TAG = "InvoiceTransaksi"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice_transaksi)

        // Debug semua extra yang diterima
        debugIntentExtras()

        initializeViews()
        initializeBluetooth()
        requestBluetoothPermissions()
        setupRecyclerView()
        loadDataFromIntent()
        setupButtons()
    }

    private fun debugIntentExtras() {
        val extras = intent.extras
        if (extras != null) {
            Log.d(TAG, "=== DEBUG INTENT EXTRAS ===")
            for (key in extras.keySet()) {
                val value = extras.get(key)
                Log.d(TAG, "Key: $key = Value: $value")
            }
            Log.d(TAG, "=== END DEBUG EXTRAS ===")
        } else {
            Log.d(TAG, "No extras found in intent")
        }
    }

    private fun initializeViews() {
        try {
            tvTanggal = findViewById(R.id.tv_tanggal_invoice)
            tvIdTransaksi = findViewById(R.id.tv_id_transaksi)
            tvNamaPelanggan = findViewById(R.id.tv_nama_pelanggan_invoice)
            tvNoHP = findViewById(R.id.tv_no_hp_invoice)
            tvLayananUtama = findViewById(R.id.tv_nama_layanan_invoice)
            tvHargaLayanan = findViewById(R.id.tv_harga_layanan_invoice)
            tvSubtotalTambahan = findViewById(R.id.tv_tambahan_invoice)
            btnCetak = findViewById(R.id.btn_cetak_invoice)
            btnKirimWhatsapp = findViewById(R.id.btn_kirim_wa)

            // Coba cari tvStatus, jika tidak ada biarkan null
            tvStatus = findViewById(R.id.tvStatus)

            // Coba cari RecyclerView, jika tidak ada biarkan null
            rvTambahan = findViewById(R.id.rv_tambahan_konfirmasi)

            // Cari tvTotalBayar dengan ID yang ada di layout saat ini
            tvTotalBayar = findViewById(R.id.tv_total_bayar)
                ?: findViewById(R.id.tv_metode_pembayaran_invoice)
                        ?: throw IllegalStateException("TextView untuk total bayar tidak ditemukan")

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            showToast("Error initializing views: ${e.message}")
            finish()
        }
    }

    private fun initializeBluetooth() {
        bluetoothAdapter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothManager.adapter
        } else {
            @Suppress("DEPRECATION")
            BluetoothAdapter.getDefaultAdapter()
        }
    }

    private fun hasBluetoothConnectPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN
            )
            val permissionsNeeded = permissions.filter {
                ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }
            if (permissionsNeeded.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), REQUEST_BLUETOOTH_PERMISSIONS)
            }
        }
    }

    private fun setupRecyclerView() {
        rvTambahan?.let { recyclerView ->
            adapter = adapter_data_tambahan(listTambahan)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }

    private fun loadDataFromIntent() {
        try {
            // Coba ambil data sebagai object invoice dulu (untuk backward compatibility)
            val invoice = intent.getSerializableExtra("invoice") as? ModelInvoice

            if (invoice != null) {
                Log.d(TAG, "Loading data from invoice object")
                loadFromInvoiceObject(invoice)
            } else {
                Log.d(TAG, "Loading data from individual extras")
                loadFromIndividualData()
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error loading data from intent", e)
            showToast("Error loading data: ${e.message}")
            finish()
        }
    }

    private fun loadFromInvoiceObject(invoice: ModelInvoice) {
        val tanggalTerdaftarStr = formatTimestamp(invoice.tanggalTerdaftar)

        tvTanggal.text = tanggalTerdaftarStr
        tvIdTransaksi.text = invoice.idTransaksi ?: "-"
        tvNamaPelanggan.text = invoice.namaPelanggan ?: "-"
        tvStatus?.text = invoice.status ?: "-"
        tvLayananUtama.text = invoice.namaLayanan ?: "-"

        // Ambil nomor HP dengan berbagai kemungkinan key
        noHPPelanggan = invoice.noHPPelanggan
            ?: intent.getStringExtra("noHP")
                    ?: intent.getStringExtra("noHPPelanggan")
                    ?: intent.getStringExtra("phoneNumber")
                    ?: intent.getStringExtra("phone")
                    ?: ""

        Log.d(TAG, "NoHP from invoice object: '$noHPPelanggan'")
        tvNoHP.text = "No. HP: ${if (noHPPelanggan.isNotEmpty()) noHPPelanggan else "Tidak tersedia"}"

        @Suppress("UNCHECKED_CAST")
        val tambahan = intent.getSerializableExtra("layanan") as? ArrayList<ModeltransaksiTambahan> ?: arrayListOf()

        listTambahan.clear()
        listTambahan.addAll(tambahan)
        adapter?.notifyDataSetChanged()

        val subtotalTambahan = tambahan.sumOf { layanan ->
            layanan.hargaLayanan?.toIntOrNull() ?: 0
        }

        val totalBayar = invoice.totalBayar ?: 0
        val hargaLayananUtama = totalBayar - subtotalTambahan

        tvHargaLayanan.text = formatCurrency(hargaLayananUtama)
        tvSubtotalTambahan.text = formatCurrency(subtotalTambahan)
        tvTotalBayar.text = formatCurrency(totalBayar)
    }

    private fun loadFromIndividualData() {
        // Ambil data individual dari KonfirmasiTransaksi dengan berbagai kemungkinan key
        val nama = intent.getStringExtra("namaPelanggan")
            ?: intent.getStringExtra("nama")
            ?: intent.getStringExtra("customerName")
            ?: "-"

        // Coba berbagai kemungkinan key untuk nomor HP
        noHPPelanggan = intent.getStringExtra("noHP")
            ?: intent.getStringExtra("noHPPelanggan")
                    ?: intent.getStringExtra("phoneNumber")
                    ?: intent.getStringExtra("phone")
                    ?: intent.getStringExtra("nomorHP")
                    ?: intent.getStringExtra("hp")
                    ?: ""

        Log.d(TAG, "NoHP from individual data: '$noHPPelanggan'")

        val layanan = intent.getStringExtra("namaLayanan")
            ?: intent.getStringExtra("layanan")
            ?: "-"

        val harga = intent.getStringExtra("hargaLayanan")
            ?: intent.getStringExtra("harga")
            ?: "0"

        val totalHarga = intent.getIntExtra("totalHarga", 0)
        val metodePembayaran = intent.getStringExtra("metodePembayaran")
            ?: intent.getStringExtra("metode")
            ?: "-"

        val tambahan = intent.getSerializableExtra("listTambahan") as? ArrayList<ModeltransaksiTambahan>
            ?: intent.getSerializableExtra("tambahan") as? ArrayList<ModeltransaksiTambahan>
            ?: arrayListOf()

        // Generate ID transaksi dan tanggal saat ini
        val currentTime = System.currentTimeMillis()
        val idTransaksi = "TRX${SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date(currentTime))}"
        val tanggalStr = formatTimestamp(currentTime)

        // Set data ke view
        tvTanggal.text = tanggalStr
        tvIdTransaksi.text = idTransaksi
        tvNamaPelanggan.text = nama

        // Tampilkan nomor HP dengan status yang jelas
        val displayHP = when {
            noHPPelanggan.isNotEmpty() && noHPPelanggan.isNotBlank() -> noHPPelanggan
            else -> "Tidak tersedia"
        }
        tvNoHP.text = "No. HP: $displayHP"

        tvStatus?.text = "Menunggu Pembayaran ($metodePembayaran)"
        tvLayananUtama.text = layanan

        // Update RecyclerView untuk tambahan
        listTambahan.clear()
        listTambahan.addAll(tambahan)
        adapter?.notifyDataSetChanged()

        // Hitung subtotal tambahan
        val subtotalTambahan = tambahan.sumOf { item ->
            val cleanHarga = item.hargaLayanan?.replace("[^\\d]".toRegex(), "") ?: "0"
            cleanHarga.toIntOrNull() ?: 0
        }

        // Hitung harga layanan utama
        val cleanHargaUtama = harga.replace("[^\\d]".toRegex(), "")
        val hargaLayananUtama = cleanHargaUtama.toIntOrNull() ?: 0

        // Set ke TextView
        tvHargaLayanan.text = formatCurrency(hargaLayananUtama)
        tvSubtotalTambahan.text = formatCurrency(subtotalTambahan)
        tvTotalBayar.text = formatCurrency(totalHarga)
    }

    private fun formatTimestamp(timestamp: Long?): String {
        return try {
            if (timestamp == null || timestamp == 0L) return "-"
            val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting timestamp", e)
            "-"
        }
    }

    private fun formatCurrency(value: Int): String {
        return try {
            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            formatter.format(value)
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting currency", e)
            "Rp $value"
        }
    }

    private fun setupButtons() {
        btnCetak.setOnClickListener {
            if (!hasBluetoothConnectPermission()) {
                showToast("Izin Bluetooth diperlukan untuk mencetak")
                return@setOnClickListener
            }

            val message = buildPrintMessage()
            printToBluetooth(message)
        }

        btnKirimWhatsapp.setOnClickListener {
            Log.d(TAG, "WhatsApp button clicked. noHPPelanggan = '$noHPPelanggan'")

            // Validasi nomor HP sebelum mengirim
            if (noHPPelanggan.isEmpty() || noHPPelanggan.isBlank() || noHPPelanggan == "Tidak tersedia") {
                showToast("Nomor HP pelanggan tidak tersedia atau tidak valid")
                Log.w(TAG, "WhatsApp failed: No valid phone number. Value: '$noHPPelanggan'")
                return@setOnClickListener
            }

            val message = buildWhatsappMessage()
            sendWhatsappMessage(message)
        }
    }

    private fun buildPrintMessage(): String {
        val receipt = StringBuilder()

        // ESC/POS Commands for formatting
        val ESC = "\u001B"
        val INIT = "$ESC@"
        val CENTER = "${ESC}a\u0001"
        val LEFT = "${ESC}a\u0000"
        val BOLD_ON = "${ESC}E\u0001"
        val BOLD_OFF = "${ESC}E\u0000"
        val CUT = "${ESC}i"

        receipt.apply {
            append(INIT) // Initialize printer
            append(CENTER)
            append(BOLD_ON)
            append("Laundry Speed\n")
            append("Alamat Laundry\n")
            append(BOLD_OFF)
            append("===============================\n")
            append(LEFT)

            // Transaction Details
            append("ID Transaksi: ${tvIdTransaksi.text}\n")
            append("Tanggal: ${tvTanggal.text}\n")
            append("Pelanggan: ${tvNamaPelanggan.text}\n")

            // Tampilkan nomor HP di struk jika tersedia
            if (noHPPelanggan.isNotEmpty() && noHPPelanggan != "Tidak tersedia") {
                append("No. HP: $noHPPelanggan\n")
            }

            tvStatus?.let { append("Status: ${it.text}\n") }
            append("-------------------------------\n")

            // Format layanan utama - dengan format yang sama seperti layanan tambahan
            val hargaUtamaClean = tvHargaLayanan.text.toString().replace("Rp", "").replace(".", "").replace(",", "").trim()

            append("Cuci Bersih Paket Lengkap\n")
            append("Rp${hargaUtamaClean},00\n")
            append("\n")

            if (listTambahan.isNotEmpty()) {
                append("Layanan Tambahan:\n")
                listTambahan.forEachIndexed { index, item ->
                    val namaLayanan = item.namaLayanan ?: ""
                    val hargaLayananClean = (item.hargaLayanan ?: "0").replace("Rp", "").replace(".", "").replace(",", "").trim()

                    // Format dengan harga di bawah nama layanan
                    append("${index + 1}. $namaLayanan\n")
                    append("   Rp$hargaLayananClean\n")
                }
                append("-------------------------------\n")
                append("Subtotal Tambahan: ${tvSubtotalTambahan.text}\n")
            }

            append(BOLD_ON)
            append("TOTAL BAYAR: ${tvTotalBayar.text}\n")
            append(BOLD_OFF)
            append("===============================\n")
            append(CENTER)
            append("Terima kasih telah memilih\n")
            append("Laundry Speed\n")
            append("\n\n\n")
            append(CUT) // Cut paper
        }

        return receipt.toString()
    }

    private fun buildWhatsappMessage(): String {
        return buildString {
            append("Hai ${tvNamaPelanggan.text} 👋\n\n")
            append("Berikut rincian laundry Anda:\n")
            append("• ID Transaksi: ${tvIdTransaksi.text}\n")
            append("• Tanggal: ${tvTanggal.text}\n")
            tvStatus?.let { append("• Status: ${it.text}\n") }
            append("\n")
            append("Layanan Utama:\n")
            append("• ${tvLayananUtama.text} - ${tvHargaLayanan.text}\n\n")

            if (listTambahan.isNotEmpty()) {
                append("Layanan Tambahan:\n")
                listTambahan.forEachIndexed { index, item ->
                    append("${index + 1}. ${item.namaLayanan ?: ""} - Rp ${item.hargaLayanan ?: "0"}\n")
                }
                append("\n")
            }

            append("Total Bayar: ${tvTotalBayar.text}\n\n")
            append("Terima kasih telah menggunakan Laundry Speed ⚡")
        }
    }

    private fun sendWhatsappMessage(message: String) {
        try {
            // Format nomor HP
            val formattedNumber = formatPhoneNumber(noHPPelanggan)

            Log.d(TAG, "Original number: '$noHPPelanggan', Formatted: '$formattedNumber'")

            if (formattedNumber.isEmpty()) {
                showToast("Nomor HP tidak valid atau tidak dapat diformat")
                return
            }

            // PERBAIKAN: Gunakan wa.me tanpa + di depan
            val whatsappUrl = "https://wa.me/$formattedNumber?text=${Uri.encode(message)}"
            Log.d(TAG, "WhatsApp URL: $whatsappUrl")

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))

            // Langsung start tanpa cek resolveActivity dulu
            startActivity(intent)
            showToast("Membuka WhatsApp...")

        } catch (e: Exception) {
            Log.e(TAG, "Error sending WhatsApp message", e)
            // Jika gagal, coba dengan cara lain
            try {
                val formattedNumber = formatPhoneNumber(noHPPelanggan)
                val fallbackUrl = "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}"
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
                startActivity(fallbackIntent)
                showToast("Membuka WhatsApp...")
            } catch (e2: Exception) {
                showToast("Tidak dapat membuka WhatsApp")
            }
        }
    }

    // Fungsi untuk memformat nomor HP dengan logging
    private fun formatPhoneNumber(phoneNumber: String): String {
        Log.d(TAG, "formatPhoneNumber called with: '$phoneNumber'")

        if (phoneNumber.isEmpty() || phoneNumber.isBlank() || phoneNumber == "Tidak tersedia") {
            Log.w(TAG, "Phone number is empty, blank, or 'Tidak tersedia'")
            return ""
        }

        // Hapus semua karakter non-digit
        val cleanNumber = phoneNumber.replace("[^\\d]".toRegex(), "")
        Log.d(TAG, "Clean number: '$cleanNumber'")

        if (cleanNumber.length < 8) {
            Log.w(TAG, "Phone number too short: $cleanNumber")
            return ""
        }

        val result = when {
            // Jika sudah dimulai dengan 62, gunakan langsung
            cleanNumber.startsWith("62") -> cleanNumber
            // Jika dimulai dengan 08, ganti dengan 628
            cleanNumber.startsWith("08") -> "62${cleanNumber.substring(1)}"
            // Jika dimulai dengan 8, tambahkan 62
            cleanNumber.startsWith("8") -> "62$cleanNumber"
            // Jika dimulai dengan 0, ganti dengan 62
            cleanNumber.startsWith("0") -> "62${cleanNumber.substring(1)}"
            // Lainnya, tambahkan 62
            else -> "62$cleanNumber"
        }

        Log.d(TAG, "Final formatted number: '$result'")
        return result
    }

    private fun closeBluetoothConnection() {
        try {
            outputStream?.close()
            bluetoothSocket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing bluetooth connection", e)
        } finally {
            outputStream = null
            bluetoothSocket = null
        }
    }

    private fun isBluetoothConnected(): Boolean {
        return bluetoothSocket?.isConnected == true
    }

    private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket? {
        if (!hasBluetoothConnectPermission()) {
            showToastOnMainThread("Izin Bluetooth diperlukan")
            return null
        }

        return try {
            device.createRfcommSocketToServiceRecord(printerUUID)
        } catch (e: SecurityException) {
            showToastOnMainThread("Izin Bluetooth diperlukan")
            null
        } catch (e: Exception) {
            try {
                val m = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                m.invoke(device, 1) as BluetoothSocket
            } catch (e2: Exception) {
                Log.e(TAG, "Failed to create bluetooth socket", e2)
                null
            }
        }
    }

    private fun printToBluetooth(text: String) {
        Thread {
            if (!hasBluetoothConnectPermission()) {
                showToastOnMainThread("Izin Bluetooth diperlukan untuk mencetak")
                return@Thread
            }

            var socket: BluetoothSocket? = null
            var stream: OutputStream? = null

            try {
                if (bluetoothAdapter == null) {
                    showToastOnMainThread("Bluetooth tidak tersedia")
                    return@Thread
                }

                if (!bluetoothAdapter!!.isEnabled) {
                    showToastOnMainThread("Bluetooth tidak aktif")
                    return@Thread
                }

                val device: BluetoothDevice? = try {
                    bluetoothAdapter?.getRemoteDevice(printerMAC)
                } catch (e: SecurityException) {
                    showToastOnMainThread("Izin Bluetooth diperlukan untuk mengakses perangkat")
                    return@Thread
                } catch (e: Exception) {
                    showToastOnMainThread("Printer tidak ditemukan: ${e.message}")
                    return@Thread
                }

                if (device == null) {
                    showToastOnMainThread("Printer tidak ditemukan")
                    return@Thread
                }

                socket = createBluetoothSocket(device)
                if (socket == null) {
                    showToastOnMainThread("Gagal membuat socket Bluetooth")
                    return@Thread
                }

                try {
                    bluetoothAdapter?.cancelDiscovery()
                } catch (e: SecurityException) {
                    showToastOnMainThread("Izin Bluetooth diperlukan")
                    return@Thread
                }

                showToastOnMainThread("Menghubungkan ke printer...")
                socket.connect()

                stream = socket.outputStream
                stream.write(text.toByteArray())
                stream.flush()

                showToastOnMainThread("Cetak berhasil")

            } catch (e: SecurityException) {
                showToastOnMainThread("Izin Bluetooth diperlukan")
                Log.e(TAG, "Bluetooth security exception", e)
            } catch (e: Exception) {
                showToastOnMainThread("Gagal mencetak: ${e.message}")
                Log.e(TAG, "Bluetooth print error", e)
            } finally {
                try {
                    stream?.close()
                    socket?.close()
                } catch (e: Exception) {
                    Log.e(TAG, "Error closing bluetooth resources", e)
                }
            }
        }.start()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showToastOnMainThread(message: String) {
        Handler(Looper.getMainLooper()).post {
            showToast(message)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allGranted) {
                showToast("Izin Bluetooth diberikan")
            } else {
                showToast("Izin Bluetooth diperlukan untuk fitur cetak")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        closeBluetoothConnection()
        }
}
