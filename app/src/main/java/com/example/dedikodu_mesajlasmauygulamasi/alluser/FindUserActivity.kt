package com.example.dedikodu_mesajlasmauygulamasi.alluser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dedikodu_mesajlasmauygulamasi.Adapter.allUserAdapter
import com.example.dedikodu_mesajlasmauygulamasi.R
import com.example.dedikodu_mesajlasmauygulamasi.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_find_user.*
import kotlinx.android.synthetic.main.activity_find_user.allUserRcyclerView

class findUserActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var alluserAdapter: allUserAdapter
    var finduserlist=ArrayList<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_user)
        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()

    }
    fun findFrends(view:View){
        val reference=database.reference.child("user")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                finduserlist.clear()
                for(ds in snapshot.children){
                    val userId=ds.key

                    val userName=ds.child("name").value.toString()

                    val userEmail=ds.child("email").value.toString()
                    val userImage=ds.child("image").value.toString()
                    val userStatus=ds.child("status").value.toString()
                    if(findFriends.text.toString() in userName){
                        val downloadUser= User(userId,userName,userEmail,userImage,userStatus)
                        finduserlist.add(downloadUser)
                    }else{
                        Toast.makeText(applicationContext,"User not found",Toast.LENGTH_LONG).show()
                    }



                }
                alluserAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        allUserRcyclerView.layoutManager= GridLayoutManager(this,2, GridLayoutManager.VERTICAL,false)
        alluserAdapter= allUserAdapter(this@findUserActivity,finduserlist)
        allUserRcyclerView.adapter=alluserAdapter
    }
}