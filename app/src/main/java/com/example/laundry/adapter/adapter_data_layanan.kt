package com.example.laundry.Pelanggan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.modeldata.ModelLayanan

class adapter_data_layanan(private val listLayanan: ArrayList<ModelLayanan>) :
    RecyclerView.Adapter<adapter_data_layanan.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_layanan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listLayanan[position]
        holder.tvID.text = item.id_layanan
        holder.tvNama.text = item.nama_layanan
        holder.tvCabang.text = item.cabang
        holder.tvHarga.text = item.harga_layanan

        holder.cvCARD.setOnClickListener {
            // aksi ketika CardView ditekan
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD: CardView = itemView.findViewById(R.id.card_data_layanan)
        val tvID: TextView = itemView.findViewById(R.id.tvidlayanan)
        val tvNama: TextView = itemView.findViewById(R.id.tvnama_layanan)
        val tvHarga: TextView = itemView.findViewById(R.id.tvharga_layanan)  // Ganti sesuai XML
        val tvCabang: TextView = itemView.findViewById(R.id.tvnama_cabang_layanan)  // Ganti sesuai XML
    }


    override fun getItemCount(): Int {
        return listLayanan.size
    }
}