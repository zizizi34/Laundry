package com.example.laundry.adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.modeldata.ModelPelanggan
import com.google.android.material.button.MaterialButton

class adapter_data_pelanggan(
    private val pelangganList: ArrayList<ModelPelanggan>,
    private val onItemClick: (ModelPelanggan) -> Unit,
    private val onDeleteClick: ((ModelPelanggan) -> Unit)? = null
) : RecyclerView.Adapter<adapter_data_pelanggan.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDataIDPelanggan: TextView = itemView.findViewById(R.id.tvDataIDPelanggan)
        val tvDataNamaPelanggan: TextView = itemView.findViewById(R.id.tvDataNamaPelanggan)
        val tvDataAlamatPelanggan: TextView = itemView.findViewById(R.id.tvDataAlamatPelanggan)
        val tvDataNoHpPelanggan: TextView = itemView.findViewById(R.id.tvDataNoHpPelanggan)
        val tvDataCabangPelanggan: TextView = itemView.findViewById(R.id.tvCabang)
        val tvDataTerdaftarPelanggan: TextView = itemView.findViewById(R.id.tvTerdaftar)
        val btDataHubungiPelanggan: Button = itemView.findViewById(R.id.btnHubungi)
        val btnDataLihatPelanggan: Button = itemView.findViewById(R.id.btnLihat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pelanggan = pelangganList[position]

        holder.tvDataIDPelanggan.text = pelanggan.id_pelanggan ?: "N/A"
        holder.tvDataNamaPelanggan.text = pelanggan.namaPelanggan ?: "Nama tidak tersedia"
        holder.tvDataAlamatPelanggan.text = pelanggan.alamatPelanggan ?: "Alamat tidak tersedia"
        holder.tvDataNoHpPelanggan.text = pelanggan.noHpPelanggan ?: "No HP tidak tersedia"
        holder.tvDataCabangPelanggan.text = pelanggan.cabang ?: "Cabang tidak tersedia"
        holder.tvDataTerdaftarPelanggan.text = pelanggan.terdaftar_pelanggan ?: "Tanggal tidak tersedia"

        // Tombol Hubungi - membuka WhatsApp atau dialer
        holder.btDataHubungiPelanggan.setOnClickListener {
            val phoneNumber = pelanggan.noHpPelanggan
            if (!phoneNumber.isNullOrEmpty()) {
                try {
                    // Format nomor untuk WhatsApp
                    val formattedNumber = formatPhoneNumberForWhatsApp(phoneNumber)

                    // Coba buka WhatsApp terlebih dahulu
                    val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://wa.me/$formattedNumber")
                    }
                    holder.itemView.context.startActivity(whatsappIntent)
                } catch (e: Exception) {
                    // Jika WhatsApp tidak tersedia, buka dialer
                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phoneNumber")
                    }
                    holder.itemView.context.startActivity(dialIntent)
                }
            } else {
                Toast.makeText(holder.itemView.context, "Nomor telepon tidak tersedia", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol Lihat - menampilkan dialog detail
        holder.btnDataLihatPelanggan.setOnClickListener {
            showDetailDialog(holder.itemView.context, pelanggan)
        }

        // Click pada item untuk edit
        holder.itemView.setOnClickListener {
            onItemClick(pelanggan)
        }
    }

    override fun getItemCount(): Int = pelangganList.size

    // Fungsi untuk format nomor telepon ke format WhatsApp
    private fun formatPhoneNumberForWhatsApp(phoneNumber: String): String {
        var cleanNumber = phoneNumber.replace("[^0-9]".toRegex(), "") // Hapus karakter non-digit

        return when {
            // Jika dimulai dengan 62, gunakan langsung
            cleanNumber.startsWith("62") -> cleanNumber

            // Jika dimulai dengan 08, ganti dengan 628
            cleanNumber.startsWith("08") -> "628${cleanNumber.substring(2)}"

            // Jika dimulai dengan 8 (tanpa 0), tambahkan 62
            cleanNumber.startsWith("8") -> "62$cleanNumber"

            // Default: tambahkan 62 di depan
            else -> "62$cleanNumber"
        }
    }

    private fun showDetailDialog(context: Context, pelanggan: ModelPelanggan) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.activity_dialog_lihat_pelanggan)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Bind data ke dialog
        val tvIdPelanggan = dialog.findViewById<TextView>(R.id.tv_ID_PELANGGAN)
        val tvNama = dialog.findViewById<TextView>(R.id.tv_NAMA)
        val tvAlamat = dialog.findViewById<TextView>(R.id.tv_ALAMAT)
        val tvNoHP = dialog.findViewById<TextView>(R.id.tv_NoHP)
        val tvCabang = dialog.findViewById<TextView>(R.id.tv_Cabang)
        val tvTerdaftar = dialog.findViewById<TextView>(R.id.tv_Terdaftar)
        val btnEdit = dialog.findViewById<MaterialButton>(R.id.btn_sunting)
        val btnDelete = dialog.findViewById<MaterialButton>(R.id.btn_delete)

        // Set data
        tvIdPelanggan.text = pelanggan.id_pelanggan ?: "N/A"
        tvNama.text = pelanggan.namaPelanggan ?: "Nama tidak tersedia"
        tvAlamat.text = pelanggan.alamatPelanggan ?: "Alamat tidak tersedia"
        tvNoHP.text = pelanggan.noHpPelanggan ?: "No HP tidak tersedia"
        tvCabang.text = pelanggan.cabang ?: "Cabang tidak tersedia"
        tvTerdaftar.text = "● Aktif"

        // Tombol Edit
        btnEdit.setOnClickListener {
            dialog.dismiss()
            onItemClick(pelanggan) // Memanggil fungsi edit yang sudah ada
        }

        // Tombol Delete
        btnDelete.setOnClickListener {
            dialog.dismiss()
            // Panggil callback delete jika tersedia
            onDeleteClick?.invoke(pelanggan) ?: run {
                Toast.makeText(context, "Fitur hapus belum diimplementasi", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(context: Context, pelanggan: ModelPelanggan) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Apakah Anda yakin ingin menghapus data pelanggan ${pelanggan.namaPelanggan ?: "ini"}?")

        builder.setPositiveButton("Ya") { _, _ ->
            // Implementasi delete ke Firebase
            // Anda perlu menambahkan callback untuk delete di adapter
            Toast.makeText(context, "Fitur hapus belum diimplementasi", Toast.LENGTH_SHORT).show()
        }

        builder.setNegativeButton("Tidak") { dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }
}