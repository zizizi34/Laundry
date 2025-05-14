package com.example.laundry.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.laundry.R
import com.example.laundry.modeldata.ModelPelanggan
import com.example.laundry.Transaksi.DataTranssaksiActivity
import com.google.firebase.database.DatabaseReference


class adapter_pilih_pelanggan(
    private val pelangganList: ArrayList<ModelPelanggan>) :
    RecyclerView.Adapter<adapter_pilih_pelanggan.ViewHolder>() {
    lateinit var appContext: Context
    lateinit var databaseReference: DatabaseReference
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_data_pegawai, parent, false)
        appContext = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val nomor = position + 1
        val item = pelangganList[position]
        holder.tvID.text = "[$nomor]"
        holder.tvNama.text = item.namaPelanggan
        holder.tvAlamat.text = "Alamat : ${item.alamatPelanggan}"
        holder.tvNoHP.text = "No HP : ${item.noHpPelanggan}"

        holder.cvCARD.setOnClickListener {
            val intent = Intent(appContext, DataTranssaksiActivity::class.java)
            intent.putExtra("idPelanggan", item.id_pelanggan)
            intent.putExtra("nama", item.namaPelanggan)
            intent.putExtra("noHP", item.noHpPelanggan)
            (appContext as Activity).setResult(Activity.RESULT_OK, intent)
            (appContext as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return pelangganList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvID = itemView.findViewById<TextView>(R.id.tvDataIDPelanggan)
        val tvNama = itemView.findViewById<TextView>(R.id.tvDataNamaPelanggan)
        val tvAlamat = itemView.findViewById<TextView>(R.id.tvDataAlamatPelanggan)
        val tvNoHP = itemView.findViewById<TextView>(R.id.tvDataNoHpPelanggan)
        val cvCARD = itemView.findViewById<View>(R.id.CVDATA_PELANGGAN)
       }

}
