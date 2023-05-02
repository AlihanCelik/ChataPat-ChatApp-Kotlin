package com.example.dedikodu_mesajlasmauygulamasi.Adapter

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog

import androidx.recyclerview.widget.RecyclerView
import com.example.dedikodu_mesajlasmauygulamasi.Message
import com.example.dedikodu_mesajlasmauygulamasi.R
import com.example.dedikodu_mesajlasmauygulamasi.databinding.DeleteLayoutBinding
import com.example.dedikodu_mesajlasmauygulamasi.databinding.ReceiveMsgBinding


import com.example.dedikodu_mesajlasmauygulamasi.databinding.SendMsgBinding
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.FirebaseDatabase

import com.squareup.picasso.Picasso

import java.util.*
import kotlin.collections.ArrayList



class MessagesAdapter(var context: Context,messages:ArrayList<Message>?,senderRoom:String,receiverRoom:String):
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    lateinit var messages: ArrayList<Message>
    val ITEM_SENT=1
    val ITEM_RECEİVE=2
    val senderRoom:String
    val receiverRoom:String





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType==ITEM_SENT){

            val view: View=LayoutInflater.from(parent.context).inflate(R.layout.send_msg,parent,false)
            SentMsgHolder(view)
        }else{
            val view:View=LayoutInflater.from(parent.context).inflate(R.layout.receive_msg,parent,false)
            ReceiveMsgHolder(view)
        }
    }
    override fun getItemViewType(position: Int): Int {

        val messages=messages[position]

        return if(FirebaseAuth.getInstance().currentUser?.uid==messages.senderId){

            ITEM_SENT
        }else{

            ITEM_RECEİVE

        }
    }

     override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {




        val message=messages[position]
        if(holder.javaClass==SentMsgHolder::class.java){

            val viewHolder=holder as SentMsgHolder
            if(message.message.equals("photo")) {
                viewHolder.binding.chatImage.visibility = View.VISIBLE
                viewHolder.binding.sentmessage.visibility = View.GONE
                viewHolder.binding.mLinear.visibility = View.GONE
                Picasso.get().load(message.imageUrl).placeholder(R.drawable.placeholder)
                    .into(viewHolder.binding.chatImage)
            }
            viewHolder.binding.sentmessage.text=message.message

            viewHolder.binding.textDateTime.text= getShortDate(message.timeStamp)
            viewHolder.itemView.setOnLongClickListener{
                val view=LayoutInflater.from(context).inflate(R.layout.delete_layout,null)
                val binding:DeleteLayoutBinding=DeleteLayoutBinding.bind(view)
                val dialog=AlertDialog.Builder(context).setTitle("Delete Message").setView(binding.root).create()
                binding.everyone.setOnClickListener {

                    message.message="This message is removed"

                    message.messageId.let {it1->
                        FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom).child("message")
                            .child(it1!!).setValue(message)

                    }
                    message.messageId.let {it2->

                        FirebaseDatabase.getInstance().reference.child("chats").child(receiverRoom).child("message")
                            .child(it2!!).setValue(message)
                    }
                    dialog.dismiss()
                }
                binding.delete.setOnClickListener {
                    message.messageId.let{task->
                        FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom).child("message")
                            .child(task!!).setValue(null)
                    }
                    dialog.dismiss()


            }
                binding.cancel.setOnClickListener { dialog.dismiss()}
                dialog.show()
                false
            }


        }else{
            val viewHolder=holder as ReceiveMsgHolder

            if(message.message.equals("photo")){
                viewHolder.binding.chatImage.visibility=View.VISIBLE
                viewHolder.binding.receivemessage.visibility=View.GONE
                viewHolder.binding.mLinear.visibility=View.GONE
                Picasso.get().load(message.imageUrl).placeholder(R.drawable.placeholder).into(viewHolder.binding.chatImage)



            }
            viewHolder.binding.receivemessage.text=message.message
            viewHolder.binding.textDateTime.text=getShortDate(message.timeStamp)
            viewHolder.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog = AlertDialog.Builder(context).setTitle("Delete Message")
                    .setView(binding.root).create()
                    binding.everyone.visibility=View.GONE
""
                binding.delete.setOnClickListener {
                        message.messageId.let { task ->
                            FirebaseDatabase.getInstance().reference.child("chats")
                                .child(senderRoom).child("message")
                                .child(task!!).setValue(null)
                        }
                        dialog.dismiss()


                    }
                binding.cancel.setOnClickListener { dialog.dismiss() }
                dialog.show()
                false
            }



        }
    }
    fun getShortDate(ts:Long?):String{
        if(ts == null) return ""

        val calendar = Calendar.getInstance(Locale.getDefault())

        calendar.timeInMillis = ts

        return android.text.format.DateFormat.format("E, dd MMM,HH:mm a", calendar).toString()
    }
    override fun getItemCount(): Int {
        return messages.size
    }
    inner class SentMsgHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var binding : SendMsgBinding= SendMsgBinding.bind(itemView)



    }
    inner class ReceiveMsgHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var binding: ReceiveMsgBinding = ReceiveMsgBinding.bind(itemView)

    }
    init {
        if(messages!=null){
            this.messages=messages
        }
        this.senderRoom=senderRoom
        this.receiverRoom=receiverRoom
    }
}