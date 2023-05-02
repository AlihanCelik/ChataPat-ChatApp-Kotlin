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
import kotlinx.android.synthetic.main.item_alluser_row.view.*


class allUserAdapter(val context:Context, val alluserList:ArrayList<User>) :RecyclerView.Adapter<allUserAdapter.allUserViewHolder>(){
    class allUserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): allUserViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.item_alluser_row,parent,false)
        return allUserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alluserList.size
    }

    override fun onBindViewHolder(holder: allUserViewHolder, position: Int) {
        holder.itemView.alluser_name.text=alluserList[position].name
        holder.itemView.alluser_status.text=alluserList[position].status
        Picasso.get().load(alluserList[position].image).into(holder.itemView.alluser_image)
        holder.itemView.setOnClickListener {
            val intent= Intent(context, userProfileActivity::class.java)
            intent.putExtra("name",alluserList[position].name)
            intent.putExtra("ReceiverImage",alluserList[position].image)
            intent.putExtra("status",alluserList[position].status)
            intent.putExtra("email",alluserList[position].email)
            intent.putExtra("id",alluserList[position].uid)
            context.startActivity(intent)

        }
    }


}