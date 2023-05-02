package com.example.dedikodu_mesajlasmauygulamasi.alluser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dedikodu_mesajlasmauygulamasi.Adapter.NotificationAdapter
import com.example.dedikodu_mesajlasmauygulamasi.HomeActivity
import com.example.dedikodu_mesajlasmauygulamasi.R
import com.example.dedikodu_mesajlasmauygulamasi.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {
    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var NotiAdapter: NotificationAdapter
    var requestlist=ArrayList<User>()
    var idList=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        mAuth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        mUser=mAuth.currentUser!!

        val freference=database.reference.child("Notification Requests").child(mUser.uid)
        val ureference=database.reference.child("user")

        freference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                idList.clear()
                for(ds in snapshot.children){
                    var id=ds.key
                    idList.add(id!!)
                }
                ureference.addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        requestlist.clear()
                        for (fds in snapshot.children){
                            val userId=fds.key

                            val userName=fds.child("name").value.toString()

                            val userEmail=fds.child("email").value.toString()
                            val userImage=fds.child("image").value.toString()
                            val userStatus=fds.child("status").value.toString()
                            if(userId in idList ){
                                val downloadUser= User(userId,userName,userEmail,userImage,userStatus)
                                requestlist.add(downloadUser)

                            }
                        }
                        NotiAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })


        var layoutManager= LinearLayoutManager(this)
        NotiRcyclerView.layoutManager= layoutManager
        NotiAdapter= NotificationAdapter(this@NotificationActivity,requestlist)
        NotiRcyclerView.adapter=NotiAdapter
    }
    fun BackClick(view: View){
        val intent= Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }


}