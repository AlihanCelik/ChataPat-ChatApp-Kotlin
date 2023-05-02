package com.example.dedikodu_mesajlasmauygulamasi

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dedikodu_mesajlasmauygulamasi.Adapter.chatUserAdapter


import com.example.dedikodu_mesajlasmauygulamasi.alluser.AllUserActivity
import com.example.dedikodu_mesajlasmauygulamasi.alluser.NotificationActivity

import com.example.dedikodu_mesajlasmauygulamasi.profile.profileActivity


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database:FirebaseDatabase
    private lateinit var Adapter: chatUserAdapter
    var messages=ArrayList<Message>()
    var chatuserlist=ArrayList<User>()
    var idList=ArrayList<String>()

    var user:User?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mAuth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        mUser=mAuth.currentUser!!



        if (mAuth.currentUser == null) {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }




        val freference=database.reference.child("chats")
        val ureference=database.reference.child("user")

        var userid = mUser.uid


        freference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                idList.clear()
                for(ds in snapshot.children){
                    var chatid=ds.key
                    var listValues:List<String> = chatid!!.split("+").map { it->it.trim() }
                    var id=listValues[0]
                    if (id==mUser.uid){
                        var id2=listValues[1]
                        idList.add(id2)
                    }
                    if(listValues[1]==mUser.uid){
                        idList.add(listValues[0])
                    }


                }

                ureference.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        chatuserlist.clear()
                        for (fds in snapshot.children){
                            val userId=fds.key

                            val userName=fds.child("name").value.toString()

                            val userEmail=fds.child("email").value.toString()
                            val userImage=fds.child("image").value.toString()
                            val userStatus=fds.child("status").value.toString()
                            if(userId in idList ){
                                if (mUser.uid!=userId){

                                                val downloadUser= User(userId,userName,userEmail,userImage,userStatus)
                                                chatuserlist.add(downloadUser)

                                }


                            }
                        }
                        Adapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        var layoutManager= LinearLayoutManager(this)
        mainUserRcyclerView.layoutManager= layoutManager
        Adapter= chatUserAdapter(this@HomeActivity,chatuserlist)
        mainUserRcyclerView.adapter=Adapter



    }




    fun allUserButton(view: View){
        val intent=Intent(this@HomeActivity,AllUserActivity::class.java)
        startActivity(intent)

    }
    fun ProfileClick(view:View){
        val intent=Intent(this@HomeActivity, profileActivity::class.java)
        startActivity(intent)

    }
    fun FriendsClick(view:View){
        val intent=Intent(this,FriendsActivity::class.java)
        startActivity(intent)

    }
    fun NotificationClick(view:View){
        val intent=Intent(this, NotificationActivity::class.java)
        startActivity(intent)

    }
    fun SettingClick(view:View){
        mAuth.signOut()
        val intent=Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }




}