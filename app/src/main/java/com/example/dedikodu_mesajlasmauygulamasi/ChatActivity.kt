package com.example.dedikodu_mesajlasmauygulamasi

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.text.method.TextKeyListener
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dedikodu_mesajlasmauygulamasi.Adapter.MessagesAdapter
import com.example.dedikodu_mesajlasmauygulamasi.Adapter.chatUserAdapter
import com.example.dedikodu_mesajlasmauygulamasi.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {
    var binding:ActivityChatBinding?=null
    var adapter:MessagesAdapter?=null
    var messages:ArrayList<Message>?=null
    var senderRoom:String?=null
    var receiverRoom:String?=null
    var database:FirebaseDatabase?=null
    var storage:FirebaseStorage?=null
    var dialog:ProgressDialog?=null

    var senderUid:String?=null
    var receiverUid:String?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding!!.root)



        database=FirebaseDatabase.getInstance()
        storage= FirebaseStorage.getInstance()
        dialog= ProgressDialog(this@ChatActivity)
        dialog!!.setMessage("Uploading image...")
        dialog!!.setCancelable(false)
        messages= ArrayList()
        val name=intent.getStringExtra("name")
        val profile=intent.getStringExtra("image")
        binding!!.chatName.text=name
        Picasso.get().load(profile).placeholder(R.drawable.placeholder).into(binding!!.chatProfileImage)
        binding!!.BackButton.setOnClickListener{
            finish()
        }
        receiverUid=intent.getStringExtra("uid")
        senderUid=FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence").child(receiverUid!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val status=snapshot.getValue(String::class.java)
                        if(status=="offline"){
                            binding!!.chatOnline.visibility= View.GONE
                        }
                        else{
                            binding!!.chatOnline.text = status
                            binding!!.chatOnline.visibility=View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        senderRoom= "$senderUid+$receiverUid"
        receiverRoom= "$receiverUid+$senderUid"
        adapter= MessagesAdapter(this@ChatActivity, messages!!, senderRoom!!,receiverRoom!!)
        binding!!.Recyclerview.layoutManager=LinearLayoutManager(this@ChatActivity)
        binding!!.Recyclerview.adapter=adapter

        database!!.reference.child("chats").child(senderRoom!!).child("message").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messages!!.clear()
                for(snapshott in snapshot.children){
                    val message:Message?=snapshott.getValue(Message::class.java)
                    message!!.messageId=snapshott.key

                    messages!!.add(message)

                }
                if(messages!!.size!=0){
                    binding!!.Recyclerview.smoothScrollToPosition(messages!!.size)
                }else{

                }

                adapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        binding!!.sendMessage.setOnClickListener{
            val messageTxt:String=binding!!.messageBox.text.toString()
            val date=Date()
            val message=Message(messageTxt,senderUid,date.time)
            binding!!.messageBox.text.clear()


            val randomKey=database!!.reference.push().key
            val lastMsgObj=HashMap<String,Any>()
            lastMsgObj["lastMsg"]=message.message!!
            lastMsgObj["lastMsgTime"]=date.time

            database!!.reference.child("chats").child(senderRoom!!).updateChildren(lastMsgObj)
            database!!.reference.child("chats").child(receiverRoom!!).updateChildren(lastMsgObj)
            database!!.reference.child("chats").child(senderRoom!!).child("message").child(randomKey!!).setValue(message)
                .addOnSuccessListener {
                    database!!.reference.child("chats").child(receiverRoom!!).child("message").child(randomKey).setValue(message)
                        .addOnSuccessListener {

                        }
                }

        }
        binding!!.attachment.setOnClickListener {
            val intent= Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(intent,25)
        }




        val handler=Handler()
        binding!!.messageBox.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                database!!.reference.child("Presence").child(senderUid!!).setValue("typing...")
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed(userStoppedTyping,1000)
            }
            var userStoppedTyping = Runnable {
                database!!.reference.child("Presence").child(senderUid!!).setValue("Online")
            }

        })




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==25){
            if(data!=null){
                if (data.data!=null){
                    val selectedImage=data.data
                    val calendar= Calendar.getInstance()
                    val reference=storage!!.reference.child("chats").child(calendar.timeInMillis.toString()+"")
                    dialog!!.show()
                    reference.putFile(selectedImage!!).addOnCompleteListener {task->
                        dialog!!.dismiss()
                        if(task.isSuccessful){
                            reference.downloadUrl.addOnSuccessListener { uri->
                                val filePath=uri.toString()
                                val messageTxt:String=binding!!.messageBox.text.toString()
                                val date=Date()
                                val message=Message(messageTxt,senderUid,date.time)
                                message.message="photo"
                                message.imageUrl=filePath
                                binding!!.messageBox.setText("")
                                val randomkey=database!!.reference.push().key
                                val lastMsgObj=HashMap<String,Any>()
                                lastMsgObj["lastMsg"]=message.message!!
                                lastMsgObj["lastMsgTime"]=date.time

                                database!!.reference.child("chats").child(receiverRoom!!).updateChildren(lastMsgObj)
                                database!!.reference.child("chats").child(senderRoom!!).child("message").child(randomkey!!)
                                    .setValue(message).addOnSuccessListener {
                                        database!!.reference.child("chats").child(receiverRoom!!).child("message").child(randomkey)
                                            .setValue(message).addOnSuccessListener {  }
                                    }

                            }
                        }
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        val currentId=FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence").child(currentId!!).setValue("Online")
    }

    override fun onPause() {
        super.onPause()
        val currentId=FirebaseAuth.getInstance().uid
        database!!.reference.child("Presence").child(currentId!!).setValue("offline")
    }



}