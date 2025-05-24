package com.example.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.modeldata.ModeltransaksiTambahan

class adapter_data_tambahan(
    private val list: List<ModeltransaksiTambahan>
) : RecyclerView.Adapter<adapter_data_tambahan.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama: TextView = itemView.findViewById(R.id.tv_nama_tambahan)
        val harga: TextView = itemView.findViewById(R.id.tv_harga_tambahan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_tambahan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.nama.text = item.namaLayanan
        holder.harga.text = "Rp. ${item.hargaLayanan}"
    }

    override fun getItemCount(): Int = list.size
}
