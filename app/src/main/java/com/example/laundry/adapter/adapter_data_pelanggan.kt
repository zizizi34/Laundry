package com.example.laundry.Pelanggan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.laundry.R
import com.example.laundry.modeldata.ModelPelanggan
import org.w3c.dom.Text


class adapter_data_pelanggan(private val listPelanggan: ArrayList<ModelPelanggan>) :
    RecyclerView.Adapter<adapter_data_pelanggan.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pelanggan, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPelanggan[position]
        holder.tvID.text = item.id_pelanggan
        holder.tvNama.text = item.namaPelanggan
        holder.tvAlamat.text = item.alamatPelanggan
        holder.tvNoHP.text = item.noHpPelanggan
        holder.tvCabang.text = item.cabang
        holder.cvCARD.setOnClickListener(){
        }
        holder.btHubungi.setOnClickListener{
        }
        holder.btLihat.setOnClickListener{
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARD = itemView.findViewById<View>(R.id.CVDATA_PELANGGAN)
        val tvID = itemView.findViewById<TextView>(R.id.tvDataIDPelanggan)
        val tvNama = itemView.findViewById<TextView>(R.id.tvDataNamaPelanggan)
        val tvAlamat = itemView.findViewById<TextView>(R.id.tvDataAlamatPelanggan)
        val tvNoHP = itemView.findViewById<TextView>(R.id.tvDataNoHpPelanggan)
        val tvCabang = itemView.findViewById<TextView>(R.id.tvCabang)
        val btHubungi = itemView.findViewById<Button>(R.id.btnHubungi)
        val btLihat = itemView.findViewById<Button>(R.id.btnLihat)
    }

    override fun getItemCount(): Int {
        return listPelanggan.size
    }
}

