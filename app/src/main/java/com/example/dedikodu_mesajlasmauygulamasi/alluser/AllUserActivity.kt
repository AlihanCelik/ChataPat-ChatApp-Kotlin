package com.example.dedikodu_mesajlasmauygulamasi.alluser


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dedikodu_mesajlasmauygulamasi.Adapter.allUserAdapter
import com.example.dedikodu_mesajlasmauygulamasi.HomeActivity
import com.example.dedikodu_mesajlasmauygulamasi.R
import com.example.dedikodu_mesajlasmauygulamasi.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_all_user.*


class AllUserActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var alluserAdapter: allUserAdapter
    var alluserlist=ArrayList<User>()
    var user: User?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_user)
        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()


        val reference=database.reference.child("user")
        reference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                alluserlist.clear()
                for(ds in snapshot.children){
                    val userId=ds.key
                    val userName=ds.child("name").value.toString()
                    val userEmail=ds.child("email").value.toString()
                    val userImage=ds.child("image").value.toString()
                    val userStatus=ds.child("status").value.toString()
                    val downloadUser= User(userId,userName,userEmail,userImage,userStatus)
                    alluserlist.add(downloadUser)


                }
                alluserAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
        var layoutManager= LinearLayoutManager(this)
        allUserRcyclerView.layoutManager=GridLayoutManager(this,2,GridLayoutManager.VERTICAL,false)
        alluserAdapter= allUserAdapter(this@AllUserActivity,alluserlist)
        allUserRcyclerView.adapter=alluserAdapter


    }
    fun findUser(view: View){
        val intent= Intent(this, findUserActivity::class.java)
        startActivity(intent)
    }

    fun allUserButton(view:View){
        val intent= Intent(this, AllUserActivity::class.java)
        startActivity(intent)
    }
    fun BackClick(view: View){
        val intent=Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}