package com.example.dedikodu_mesajlasmauygulamasi.alluser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

import android.widget.Toast
import com.example.dedikodu_mesajlasmauygulamasi.ChatActivity
import com.example.dedikodu_mesajlasmauygulamasi.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_user_profile.*



class userProfileActivity : AppCompatActivity() {

    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth:FirebaseAuth
    private lateinit var mUserRef:DatabaseReference
    private lateinit var requestRef:DatabaseReference
    private lateinit var friendRef:DatabaseReference
    private lateinit var notirequestsRef:DatabaseReference
    var ReceiverName: String?=null
    var ReceriverImage: String? =null
    var myProfileImage :String?=null
    var myName:String?=null
    var CurrentState="nothing_happen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        ReceiverName = intent.getStringExtra("name")
        ReceriverImage=intent.getStringExtra("ReceiverImage")
        var ReceiverEmail: String? = intent.getStringExtra("email")
        var ReceiverStatus: String? = intent.getStringExtra("status")

        var Userid:String?=intent.getStringExtra("id")
        Picasso.get().load(ReceriverImage).into(userProfile)
        receiverName.text = ReceiverName
        receiverEmail.text = ReceiverEmail
        receiverStatus.text = ReceiverStatus



        mUserRef=FirebaseDatabase.getInstance().reference.child("user")
        requestRef=FirebaseDatabase.getInstance().reference.child("Requests")
        friendRef=FirebaseDatabase.getInstance().reference.child("Friends")
        notirequestsRef=FirebaseDatabase.getInstance().reference.child("Notification Requests")
        mAuth=FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        laodMyProfile()
        friendButton.setOnClickListener(View.OnClickListener {
            PerformAction(Userid!!)
        })
        CheckUserExistance(Userid!!)

        DeclineButton.setOnClickListener(View.OnClickListener {
            UnFriend(Userid!!)
        })





    }
    fun Send(name:String,image:String,id: String){
        val intent= Intent(this,ChatActivity::class.java)
        intent.putExtra("name",name)
        intent.putExtra("image",image)
        intent.putExtra("uid",id)
        this.startActivity(intent)
    }

    fun UnFriend(userId: String){
        if(CurrentState=="friend"){
            friendRef.child(mUser.uid).child(userId).removeValue().addOnCompleteListener{task->
                if(task.isSuccessful){
                    friendRef.child(userId).child(mUser.uid).removeValue().addOnCompleteListener {task->
                        if(task.isSuccessful){
                            Toast.makeText(this,"You are Unfriend",Toast.LENGTH_LONG).show()
                            CurrentState="nothing_happen"
                            friendButton.text="Send Friend Request"
                            DeclineButton.visibility=View.GONE
                        }

                    }
                }
            }
        }
        if(CurrentState=="he_sent_pending"){
            var hashMap : HashMap<String,String>
                    = HashMap<String, String> ()
            hashMap["status"] = "decline"
            requestRef.child(userId).child(mUser.uid).updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {//o
                if(it.isSuccessful){
                    notirequestsRef.child(userId).child(mUser.uid).removeValue()
                    Toast.makeText(this,"You have Decline Friend",Toast.LENGTH_LONG).show()
                    CurrentState="he_sent_decline"
                    friendButton.visibility=View.GONE
                    DeclineButton.visibility=View.GONE
                }
            }
        }
    }
    fun CheckUserExistance(userId:String){

        friendRef.child(mUser.uid).child(userId).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    CurrentState="friend"
                    friendButton.text="Send Message"
                    DeclineButton.text="Unfriend"
                    DeclineButton.visibility=View.VISIBLE

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        friendRef.child(userId).child(mUser.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    CurrentState="friend"
                    friendButton.text="Send Message"
                    DeclineButton.text="Unfriend"
                    DeclineButton.visibility=View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        requestRef.child(mUser.uid).child(userId).addValueEventListener(object :ValueEventListener{ //o
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()){
                    if(snapshot.child("status").value.toString() == "pending"){
                        CurrentState="I_sent_pending"
                        friendButton.text="Cancel Friend Request"
                        DeclineButton.visibility=View.GONE
                    }
                    if(snapshot.child("status").value.toString() == "decline"){
                        CurrentState="I_sent_decline"
                        friendButton.text="Cancel Friend Request"
                        DeclineButton.visibility=View.GONE
                    }
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        requestRef.child(userId).child(mUser.uid).addValueEventListener(object:ValueEventListener{ //o
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if (snapshot.child("status").value.toString()=="pending"){
                        CurrentState="he_sent_pending"
                        friendButton.text="Accept Friend Request"
                        DeclineButton.text="Decline Friend"
                        DeclineButton.visibility=View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        if(CurrentState=="nothing_happen"){
            CurrentState="nothing_happen"
            friendButton.text="Send Friend Request"
            DeclineButton.visibility=View.GONE

        }

    }
    fun PerformAction(userId:String){
        if(CurrentState == "nothing_happen"){
            var hashMap : HashMap<String,String>
                    = HashMap<String, String> ()


            hashMap["status"] = "pending"


            requestRef.child(mUser.uid).child(userId).updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {task-> //o
                if (task.isSuccessful){
                    Toast.makeText(this,"You have sent Friend Request",Toast.LENGTH_LONG).show()
                    notirequestsRef.child(userId).child(mUser.uid).updateChildren(hashMap as Map<String, Any>).addOnCompleteListener {

                    }

                    CurrentState="I_sent_pending"
                    DeclineButton.visibility=View.GONE
                    friendButton.text="Cancel Friend Request"
                }else{
                    Toast.makeText(this,""+task.exception,Toast.LENGTH_LONG).show()
                }
            }
        }
        if(CurrentState=="I_sent_pending"||CurrentState=="I_sent_decline"){
            requestRef.child(mUser.uid).child(userId).removeValue().addOnCompleteListener{//o
                if(it.isSuccessful){
                    Toast.makeText(this,"You have canceled Friend Request",Toast.LENGTH_LONG).show()
                    notirequestsRef.child(userId).child(mUser.uid).removeValue()
                    CurrentState="nothing_happen"
                    friendButton.text="Send Friend Request"
                    DeclineButton.visibility=View.GONE
                }
                else{
                    Toast.makeText(this,""+it.exception,Toast.LENGTH_LONG).show()
                }
            }
        }
        if (CurrentState=="he_sent_pending"){
            requestRef.child((userId)).child(mUser.uid).removeValue().addOnCompleteListener{ //o
                if(it.isSuccessful){
                    notirequestsRef.child(userId).child(mUser.uid).removeValue()
                    val hashMap : HashMap<String,String>
                            = HashMap<String, String> ()
                    hashMap["status"] = "friend"
                    hashMap["username"] = ReceiverName!!
                    hashMap["profileImageUrl"] = ReceriverImage!!


                    val hashMap1 : HashMap<String,String>
                            = HashMap<String, String> ()
                    hashMap1["status"] = "friend"
                    hashMap1["username"] = myName!!
                    hashMap1["profileImageUrl"] = myProfileImage!!

                    friendRef.child(mUser.uid).child(userId).updateChildren(hashMap as Map<String, Any>).addOnCompleteListener{task->

                        if(task.isSuccessful){
                            friendRef.child(userId).child(mUser.uid).updateChildren(hashMap1 as Map<String, Any>).addOnCompleteListener{
                                Toast.makeText(this,"You added friend",Toast.LENGTH_LONG).show()
                                CurrentState="friend"
                                friendButton.text="Send Message"
                                DeclineButton.text="UnFriend"
                                DeclineButton.visibility=View.VISIBLE

                            }

                        }
                    }
                }
            }
        }
        if(CurrentState=="friend"){
            var Userid:String?=intent.getStringExtra("id")
            Send(ReceiverName!!,ReceriverImage!!,Userid!!)


        }

    }
    fun laodMyProfile(){
        mUserRef.child(mUser.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    myProfileImage=snapshot.child("image").value.toString()
                    myName=snapshot.child("name").value.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }




}




