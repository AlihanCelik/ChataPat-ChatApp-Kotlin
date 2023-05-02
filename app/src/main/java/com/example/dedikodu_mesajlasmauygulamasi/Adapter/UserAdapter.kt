package com.example.dedikodu_mesajlasmauygulamasi.Adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dedikodu_mesajlasmauygulamasi.R
import com.example.dedikodu_mesajlasmauygulamasi.User
import com.example.dedikodu_mesajlasmauygulamasi.alluser.userProfileActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user_row.view.*

class userAdapter(val context: Context, val friendList:ArrayList<User>) : RecyclerView.Adapter<userAdapter.UserViewHolder>() {
    class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_user_row,parent,false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
       return friendList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.itemView.user_name.text=friendList[position].name
        holder.itemView.user_status.text=friendList[position].status
        Picasso.get().load(friendList[position].image).into(holder.itemView.user_image)
        holder.itemView.setOnClickListener {
            val intent= Intent(context, userProfileActivity::class.java)
            intent.putExtra("name",friendList[position].name)
            intent.putExtra("ReceiverImage",friendList[position].image)
            intent.putExtra("status",friendList[position].status)
            intent.putExtra("email",friendList[position].email)
            intent.putExtra("id",friendList[position].uid)
            context.startActivity(intent)
        }
    }
}