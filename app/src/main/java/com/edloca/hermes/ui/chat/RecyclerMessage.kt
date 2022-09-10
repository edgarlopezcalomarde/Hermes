package com.edloca.hermes.ui.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.edloca.hermes.R
import com.edloca.hermes.data.models.MessageModel
import java.text.SimpleDateFormat
import java.util.*

class RecyclerMessage(var context:Context , var currentUser:String, var listener: OnUserClickImage):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    interface OnUserClickImage{
        fun onImageClick(image:ImageView)
    }

    var listMessages: List<MessageModel>  = listOf()

    val ITEM_RECIEVE = 1
    val ITEM_SEND = 2

    fun setData(messages:List<MessageModel>){
        this.listMessages = messages
    }


    inner class  RecieveViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){
        var tvRecievetimestamp = itemview.findViewById<TextView>(R.id.tv_timestamp)
        var tvRecievemessage = itemview.findViewById<TextView>(R.id.tv_message)
        var ivPhoto = itemview.findViewById<ImageView>(R.id.iv_photo)

        fun bind(url:String){ ivPhoto.setOnClickListener { listener.onImageClick(ivPhoto)  } }

    }

    inner class  SendViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){
        var tvSendtimestamp = itemview.findViewById<TextView>(R.id.tv_timestamp)
        var tvSendmessage = itemview.findViewById<TextView>(R.id.tv_message)
        var ivPhoto = itemview.findViewById<ImageView>(R.id.iv_photo)

        fun bind(url:String){ ivPhoto.setOnClickListener { listener.onImageClick(ivPhoto)  } }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       if (viewType == 1){
           val itemview = LayoutInflater.from(context).inflate(R.layout.recieve_bubble, parent, false)
           return  RecieveViewHolder(itemview)

       }else{
           val itemview = LayoutInflater.from(context).inflate(R.layout.send_bubble, parent, false)
           return  SendViewHolder(itemview)
       }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = listMessages[position]

        if (holder.javaClass == SendViewHolder::class.java){

            val viewHolder = holder as SendViewHolder
            if (!currentMessage.message.isNullOrEmpty()) {
                viewHolder.ivPhoto.visibility = View.GONE
                viewHolder.tvSendmessage.visibility = View.VISIBLE
                viewHolder.tvSendmessage.text = currentMessage.message
            }
            viewHolder.tvSendtimestamp.text = getDateTime(currentMessage.timestamp!!.seconds)

            if (!currentMessage.photo.isNullOrEmpty() && currentMessage.photo != "null"){
                viewHolder.ivPhoto.visibility = View.VISIBLE
                viewHolder.tvSendmessage.visibility = View.GONE

                Glide.with(context).load(currentMessage.photo).centerCrop().into(viewHolder.ivPhoto)
                holder.bind(currentMessage.photo!!)
            }

        }else{

            val viewHolder = holder as RecieveViewHolder
            if (!currentMessage.message.isNullOrEmpty()) {
                viewHolder.ivPhoto.visibility = View.GONE
                viewHolder.tvRecievemessage.visibility = View.VISIBLE
                viewHolder.tvRecievemessage.text = currentMessage.message
            }
            viewHolder.tvRecievetimestamp.text = getDateTime(currentMessage.timestamp!!.seconds)

            if (!currentMessage.photo.isNullOrEmpty() && currentMessage.photo != "null"){
                viewHolder.ivPhoto.visibility = View.VISIBLE
                viewHolder.tvRecievemessage.visibility = View.GONE

                Glide.with(context).load(currentMessage.photo).centerCrop().into(viewHolder.ivPhoto)

                holder.bind(currentMessage.photo!!)
            }
        }

    }

    override fun getItemCount(): Int = listMessages?.size ?: 0


    override fun getItemViewType(position: Int): Int {

        val currentMessage = listMessages?.get(position)


        if (currentUser.equals(currentMessage.senderId)){
            return ITEM_SEND
        }else{
            return  ITEM_RECIEVE
        }

    }


    private fun getDateTime(s: Long): String? {
        try {
            val sdf = SimpleDateFormat("HH:mm")
            sdf.timeZone = TimeZone.getTimeZone("Europe/Madrid")

            val netDate = Date(s* 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }


}