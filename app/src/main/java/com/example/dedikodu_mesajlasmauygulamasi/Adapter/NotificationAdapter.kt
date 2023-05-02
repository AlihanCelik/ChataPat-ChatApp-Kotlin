package com.example.dedikodu_mesajlasmauygulamasi.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.dedikodu_mesajlasmauygulamasi.R
import com.example.dedikodu_mesajlasmauygulamasi.User
import com.example.dedikodu_mesajlasmauygulamasi.alluser.userProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso


import kotlinx.android.synthetic.main.item_notification_raw.view.*

class NotificationAdapter(val context: Context, val notiList:ArrayList<User>): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mUserRef: DatabaseReference
    private lateinit var requestRef: DatabaseReference
    private lateinit var friendRef: DatabaseReference
    private lateinit var notirequestsRef:DatabaseReference
    var MyName:String?=null
    var myProfileImage:String?=null
    class NotificationViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_notification_raw,parent,false)
        return NotificationViewHolder(view)
    }



    override fun getItemCount(): Int {
        return notiList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        holder.itemView.notification_name.text=notiList[position].name
        holder.itemView.notification_email.text=notiList[position].email
        Picasso.get().load(notiList[position].image).into(holder.itemView.notification_image)
        var userId=notiList[position].uid!!
        var name=notiList[position].name
        var image=notiList[position].image
        holder.itemView.acceptfriendButton.setOnClickListener{

            mUserRef= FirebaseDatabase.getInstance().reference.child("user")
            requestRef= FirebaseDatabase.getInstance().reference.child("Requests")
            friendRef= FirebaseDatabase.getInstance().reference.child("Friends")
            notirequestsRef=FirebaseDatabase.getInstance().reference.child("Notification Requests")
            mAuth=FirebaseAuth.getInstance()
            mUser=mAuth.currentUser!!
            notirequestsRef.child(mUser.uid).child(userId).removeValue()
            loadMyProfile()
            requestRef.child((userId)).child(mUser.uid).removeValue().addOnCompleteListener{
                if(it.isSuccessful){
                    val hashMap : HashMap<String,String>
                            = HashMap<String, String> ()
                    hashMap["status"] = "friend"
                    hashMap["username"] = name!!
                    hashMap["profileImageUrl"] = image!!

                    val hashMap1 : HashMap<String,String>
                            = HashMap<String, String> ()
                    hashMap1["status"] = "friend"
                    hashMap1["username"] = MyName!!
                    hashMap1["profileImageUrl"] = myProfileImage!!


                    friendRef.child(mUser.uid).child(userId).updateChildren(hashMap as Map<String, Any>).addOnCompleteListener{task->
                        if(task.isSuccessful){


                            friendRef.child(userId).child(mUser.uid).updateChildren(hashMap1 as Map<String, Any>).addOnCompleteListener{
                                Toast.makeText(context,"You added friend", Toast.LENGTH_LONG).show()
                                holder.itemView.DeclineButton.visibility=View.GONE
                                holder.itemView.acceptfriendButton.visibility=View.GONE



                            }

                        }
                    }
                }
            }

        }
        holder.itemView.DeclineButton.setOnClickListener {
            notirequestsRef.child(mUser.uid).child(userId).removeValue()
            var hashMap : HashMap<String,String>
                    = HashMap<String, String> ()
            hashMap["status"] = "decline"
            requestRef.child(userId).child(mUser.uid).updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {
                if(it.isSuccessful){

                    Toast.makeText(context,"You have Decline Friend",Toast.LENGTH_LONG).show()

                    holder.itemView.acceptfriendButton.visibility=View.GONE
                    holder.itemView.DeclineButton.visibility=View.GONE
                }
            }
        }
        holder.itemView.setOnClickListener {
            val intent= Intent(context, userProfileActivity::class.java)
            intent.putExtra("name",notiList[position].name)
            intent.putExtra("ReceiverImage",notiList[position].image)
            intent.putExtra("status",notiList[position].status)
            intent.putExtra("email",notiList[position].email)
            intent.putExtra("id",notiList[position].uid)
            context.startActivity(intent)
        }
    }

    fun loadMyProfile(){
        mUserRef= FirebaseDatabase.getInstance().reference.child("user")
        mUserRef.child(mUser.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    myProfileImage=snapshot.child("image").value.toString()
                    MyName=snapshot.child("name").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

}