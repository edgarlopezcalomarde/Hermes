package com.edloca.hermes.ui.chat

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edloca.hermes.R


class RecyclerPhothos(val context:Context, val itemClickListener: ItemListener)
    :RecyclerView.Adapter<RecyclerPhothos.PhothosViewHolder>() {

    interface ItemListener{
      fun  onDeleteItemClick(positionItem:Int)
    }

    private var phothoList: List<Uri> = listOf()

    fun setlistData(listData: List<Uri>?) {
        if (listData != null) this.phothoList = listData
    }

    inner class PhothosViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var selectedImg = itemView.findViewById<ImageView>(R.id.selectedPhoto)
        var btnQuitList = itemView.findViewById<ImageView>(R.id.btnQuitList)


        fun bind(posicion:Int){
            btnQuitList.setOnClickListener { itemClickListener.onDeleteItemClick(posicion) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhothosViewHolder {
       var itemView = LayoutInflater.from(context).inflate(R.layout.photo_frame, parent, false)
        return PhothosViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhothosViewHolder, position: Int) {
        Glide.with(context).load(phothoList[position]).into(holder.selectedImg)
        holder.bind(position)
    }

    override fun getItemCount(): Int = phothoList.size
}