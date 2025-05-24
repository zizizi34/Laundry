package com.example.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.modeldata.ModeltransaksiTambahan


class adapter_pilih_tambahan(
    private val list: List<ModeltransaksiTambahan>,
    private val onItemClick: (ModeltransaksiTambahan) -> Unit
) : RecyclerView.Adapter<adapter_pilih_tambahan.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama_tambahan)
        val tvHarga: TextView = itemView.findViewById(R.id.tv_harga_tambahan)

        fun bind(tambahan: ModeltransaksiTambahan) {
            tvNama.text = tambahan.namaLayanan
            tvHarga.text = "Harga: ${tambahan.hargaLayanan}"

            itemView.setOnClickListener {
                onItemClick(tambahan)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}
