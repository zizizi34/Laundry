package com.example.laundry.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.modeldata.ModelPelanggan

class adapter_pilih_pelanggan(
    private val list: List<ModelPelanggan>,
    private val onItemClick: (ModelPelanggan) -> Unit
) : RecyclerView.Adapter<adapter_pilih_pelanggan.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvDataNamaPelanggan)
        val tvID: TextView = itemView.findViewById(R.id.tvDataIDPelanggan)
        val tvNoHp: TextView = itemView.findViewById(R.id.tvDataNoHpPelanggan)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvDataAlamatPelanggan)

        @SuppressLint("StringFormatInvalid")
        fun bind(pelanggan: ModelPelanggan) {
            val context = itemView.context

            tvNama.text = pelanggan.namaPelanggan
            tvID.text = context.getString(R.string.format_id, pelanggan.id_pelanggan)
            tvNoHp.text = context.getString(R.string.format_no_hp, pelanggan.noHpPelanggan)
            tvAlamat.text = context.getString(R.string.format_alamat, pelanggan.alamatPelanggan)

            itemView.setOnClickListener {
                onItemClick(pelanggan)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_transaksi_pelanggan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}