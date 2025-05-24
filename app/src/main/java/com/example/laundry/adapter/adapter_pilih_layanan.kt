package com.example.laundry.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.Display.Mode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.Transaksi.DataTranssaksiActivity
import com.example.laundry.modeldata.ModelLayanan
import com.google.firebase.database.DatabaseReference

class adapter_pilih_layanan(
    private val list: List<ModelLayanan>,
    private val onItemClick: (ModelLayanan) -> Unit
) : RecyclerView.Adapter<adapter_pilih_layanan.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvnama_layanan)
        val tvID: TextView = itemView.findViewById(R.id.tvidlayanan)
        val tvHarga: TextView = itemView.findViewById(R.id.tvharga_layanan)

        fun bind(layanan: ModelLayanan) {
            tvNama.text = layanan.nama_layanan
            tvID.text = "ID: ${layanan.id_layanan}"
            tvHarga.text = "Harga: ${layanan.harga_layanan}"

            itemView.setOnClickListener {
                onItemClick(layanan)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_layanan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }
}