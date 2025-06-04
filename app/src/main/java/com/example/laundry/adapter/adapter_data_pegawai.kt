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
import com.example.laundry.modeldata.ModelPegawai
import com.google.android.material.button.MaterialButton

class adapter_data_pegawai(
    private val pegawaiList: ArrayList<ModelPegawai>,
    private val onItemClick: (ModelPegawai) -> Unit,
    private val onDeleteClick: ((ModelPegawai) -> Unit)? = null
) : RecyclerView.Adapter<adapter_data_pegawai.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARDPEGAWAI: View = itemView.findViewById(R.id.CVDATA_PEGAWAI)
        val tvDataIDPegawai: TextView = itemView.findViewById(R.id.tvDataIDPegawai)
        val tvDataNamaPegawai: TextView = itemView.findViewById(R.id.tvDataNamaPegawai)
        val tvDataAlamatPegawai: TextView = itemView.findViewById(R.id.tvDataAlamatPegawai)
        val tvDataNoHpPegawai: TextView = itemView.findViewById(R.id.tvDataNoHpPegawai)
        val tvDataCabangPegawai: TextView = itemView.findViewById(R.id.tvDataCabangPegawai)
        val tvDataTerdaftarPegawai: TextView = itemView.findViewById(R.id.tvDataTerdaftarPegawai)
        val btDataHubungiPegawai: Button = itemView.findViewById(R.id.btDataHubungiPegawai)
        val btnDataLihatPegawai: Button = itemView.findViewById(R.id.btDataLihatPegawai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pegawai, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pegawai = pegawaiList[position]

        holder.tvDataIDPegawai.text = pegawai.idPegawai ?: "N/A"
        holder.tvDataNamaPegawai.text = pegawai.namaPegawai ?: "Nama tidak tersedia"
        holder.tvDataAlamatPegawai.text = pegawai.alamatPegawai ?: "Alamat tidak tersedia"
        holder.tvDataNoHpPegawai.text = pegawai.noHPPegawai ?: "No HP tidak tersedia"
        holder.tvDataCabangPegawai.text = pegawai.Cabang ?: "Cabang tidak tersedia"
        holder.tvDataTerdaftarPegawai.text = pegawai.terdaftar ?: "Tanggal tidak tersedia"

        // Tombol Hubungi - membuka WhatsApp atau dialer
        holder.btDataHubungiPegawai.setOnClickListener {
            val phoneNumber = pegawai.noHPPegawai
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
        holder.btnDataLihatPegawai.setOnClickListener {
            showDetailDialog(holder.itemView.context, pegawai)
        }

        // Click pada item untuk edit
        holder.itemView.setOnClickListener {
            onItemClick(pegawai)
        }
    }

    override fun getItemCount(): Int = pegawaiList.size

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

    private fun showDetailDialog(context: Context, pegawai: ModelPegawai) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.activity_dialog_lihat_pegawai)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Bind data ke dialog
        val tvIdPegawai = dialog.findViewById<TextView>(R.id.tv_ID_PEGAWAI)
        val tvNama = dialog.findViewById<TextView>(R.id.tv_NAMAPEGAWAI)
        val tvAlamat = dialog.findViewById<TextView>(R.id.tv_ALAMAT)
        val tvNoHP = dialog.findViewById<TextView>(R.id.tv_NoHP)
        val tvCabang = dialog.findViewById<TextView>(R.id.tv_Cabang)
        val tvTerdaftar = dialog.findViewById<TextView>(R.id.tv_Terdaftar)
        val btnEdit = dialog.findViewById<MaterialButton>(R.id.btn_sunting)
        val btnDelete = dialog.findViewById<MaterialButton>(R.id.btn_delete)

        // Set data
        tvIdPegawai.text = pegawai.idPegawai ?: "N/A"
        tvNama.text = pegawai.namaPegawai ?: "Nama tidak tersedia"
        tvAlamat.text = pegawai.alamatPegawai ?: "Alamat tidak tersedia"
        tvNoHP.text = pegawai.noHPPegawai ?: "No HP tidak tersedia"
        tvCabang.text = pegawai.Cabang ?: "Cabang tidak tersedia"
        tvTerdaftar.text = "● Aktif"

        // Tombol Edit
        btnEdit.setOnClickListener {
            dialog.dismiss()
            onItemClick(pegawai) // Memanggil fungsi edit yang sudah ada
        }

        // Tombol Delete
        btnDelete.setOnClickListener {
            dialog.dismiss()
            // Panggil callback delete jika tersedia
            onDeleteClick?.invoke(pegawai) ?: run {
                Toast.makeText(context, "Fitur hapus belum diimplementasi", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(context: Context, pegawai: ModelPegawai) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.setTitle("Konfirmasi Hapus")
        builder.setMessage("Apakah Anda yakin ingin menghapus data pegawai ${pegawai.namaPegawai ?: "ini"}?")

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