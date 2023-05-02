package com.example.dedikodu_mesajlasmauygulamasi.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dedikodu_mesajlasmauygulamasi.ChatActivity
import com.example.dedikodu_mesajlasmauygulamasi.R
import com.example.dedikodu_mesajlasmauygulamasi.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView



class chatUserAdapter(val context: Context,val chatuserList: ArrayList<User>) : RecyclerView.Adapter<chatUserAdapter.ViewHolder>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view= LayoutInflater.from(parent.context).inflate(R.layout.item_chatuser,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return chatuserList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user=chatuserList[position]
        holder.chatprofilename.text=user.name

        FirebaseDatabase.getInstance().reference.child("chats").child(FirebaseAuth.getInstance().uid+"+"+user.uid)
            .child("message").orderByChild("timestamp").limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren()){
                        for (snapshott in snapshot.children){
                            holder.txtStatus.text=snapshott.child("message").value.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })





        Picasso.get().load(user.image).into(holder.imgUser)
        holder.itemView.setOnClickListener {

            val intent= Intent(context,ChatActivity::class.java)
            intent.putExtra("name",user.name)
            intent.putExtra("image",user.image)
            intent.putExtra("uid",user.uid)

            context.startActivity(intent)


        }
    }

    class ViewHolder (view : View):RecyclerView.ViewHolder(view){

        val chatprofilename:TextView=view.findViewById(R.id.chatprofilename)
        val txtStatus:TextView=view.findViewById(R.id.chatprofilestatus)

        val imgUser:CircleImageView=view.findViewById(R.id.chatprofileimage)


    }

}




