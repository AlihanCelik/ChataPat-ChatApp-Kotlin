package com.example.dedikodu_mesajlasmauygulamasi.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.dedikodu_mesajlasmauygulamasi.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class profileActivity : AppCompatActivity() {

    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var database:FirebaseDatabase
    var imageUrl:String=""




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        mAuth= FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()
        mUser=mAuth.currentUser!!
        database.reference.child("user").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children){
                    var id=ds.key

                    if(id==mUser.uid){
                        my_profile_name.text=ds.child("name").value.toString()
                        my_profile_email.text=ds.child("email").value.toString()
                        Picasso.get().load(ds.child("image").value.toString()).into(my_profile_image)
                        Mystatus.text=ds.child("status").value.toString()
                        imageUrl=ds.child("image").value.toString()
                    }




                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })







    }

    fun LogOut(view : View){
        mAuth.signOut()
        val intent= Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun BackClick(view:View){
        val intent=Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }
    fun EditProfile(view :View){
        val intent= Intent(this, EditProfile::class.java)
        intent.putExtra("name",my_profile_name.text)
        intent.putExtra("image",imageUrl)
        intent.putExtra("status",Mystatus.text)
        intent.putExtra("email",my_profile_email.text)


        startActivity(intent)
    }
    fun ChangePassword(view :View){
        val intent=Intent(this,ChangePassword::class.java)
        startActivity(intent)
        finish()
    }
}