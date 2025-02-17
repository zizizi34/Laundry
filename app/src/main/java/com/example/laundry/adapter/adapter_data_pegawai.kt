package com.example.laundry.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.modeldata.ModelPegawai

class adapter_data_pegawai(private val listPegawai: List<ModelPegawai>) :
    RecyclerView.Adapter<adapter_data_pegawai.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pegawai, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listPegawai[position]
        holder.tvID.text = item.idPegawai
        holder.tvNama.text = item.namaPegawai
        holder.tvAlamat.text = item.alamatPegawai
        holder.tvNoHP.text = item.noHPPegawai
        holder.tvTerdaftar.text = item.terdaftar

        holder.cvCARDPEGAWAI.setOnClickListener {
        }
        holder.btHubungi.setOnClickListener {

        }
        holder.btLihatPegawai.setOnClickListener {

        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cvCARDPEGAWAI: View = itemView.findViewById(R.id.CVDATA_PEGAWAI)
        val tvID: TextView = itemView.findViewById(R.id.tvDataIDPegawai)
        val tvNama: TextView = itemView.findViewById(R.id.tvDataNamaPegawai)
        val tvAlamat: TextView = itemView.findViewById(R.id.tvDataAlamatPegawai)
        val tvNoHP: TextView = itemView.findViewById(R.id.tvDataNoHpPegawai)
        val tvTerdaftar: TextView = itemView.findViewById(R.id.tvDataTerdaftarPegawai)
        val btHubungi: Button = itemView.findViewById(R.id.btDataHubungiPegawai)
        val btLihatPegawai: Button = itemView.findViewById(R.id.btDataLihatPegawai)
    }
    override fun getItemCount(): Int {
        return listPegawai.size
       }
}