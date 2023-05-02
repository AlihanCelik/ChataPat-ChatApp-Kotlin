package com.example.dedikodu_mesajlasmauygulamasi

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*

import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {
    var secilenGorsel: Uri?=null
    var secilenBitmap: Bitmap?=null
    private lateinit var auth:FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var database:FirebaseDatabase

    var imageURI:String=""
    var name:String=""
    var email:String=""
    var password:String=""
    var confirm:String=""
    private val  emailPattern:String = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)


    }
    fun SignUp(view:View){
        val progressDialog = ProgressDialog(this@RegistrationActivity)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        var status="Hey There I'm Using This Application"

        name=registrationName.text.toString()
        email=registrationEmail.text.toString()
        password=registrationPassword.text.toString()
        confirm=registrationConfirm.text.toString()
        auth= FirebaseAuth.getInstance()
        database= FirebaseDatabase.getInstance()
        storage= FirebaseStorage.getInstance()

        fun isEmailValid(email: String): Boolean {
            return Pattern.compile(
                emailPattern
            ).matcher(email).matches()
        }


        if(email.isEmpty()||password.isEmpty()||name.isEmpty()||confirm.isEmpty()){
            progressDialog.dismiss()
            Toast.makeText(this,"Please Enter Valid Data",Toast.LENGTH_LONG).show()

        }else if(!isEmailValid(email)){
            progressDialog.dismiss()
            registrationEmail.error="Invalid Email"
            Toast.makeText(this,"Invalid Email",Toast.LENGTH_LONG).show()
        }else if(password != confirm){
            progressDialog.dismiss()
            Toast.makeText(this,"Password does not Match",Toast.LENGTH_LONG).show()

        }
        else if(password.length<6){
            progressDialog.dismiss()
            registrationPassword.error="Invalid Password"
            Toast.makeText(this,"Password must be a minimum of 6 characters",Toast.LENGTH_LONG).show()
        }
        else{

            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task->
                if(task.isSuccessful){


                    val reference= database.reference.child("user").child(auth.uid!!)
                    val storageReference=storage.reference.child("upload").child(auth.uid!!)
                    if(secilenGorsel!=null){
                        storageReference.putFile(secilenGorsel!!).addOnCompleteListener { task->
                            if(task.isSuccessful){
                                storageReference.downloadUrl.addOnSuccessListener {
                                    imageURI=it.toString()
                                    var status="Hey There I'm Using This Application"
                                    reference.setValue(User(auth.uid,name,email,imageURI,status)).addOnCompleteListener {task->
                                        if(task.isSuccessful){
                                            progressDialog.dismiss()
                                            Toast.makeText(this,"User Successfully created.",Toast.LENGTH_LONG).show()
                                            val intent=Intent(this@RegistrationActivity,HomeActivity::class.java)
                                            startActivity(intent)
                                            finish()


                                        }else{
                                            Toast.makeText(this,"Could not create user",Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        imageURI="https://firebasestorage.googleapis.com/v0/b/dedikodu-mesajlasmauygulamasi.appspot.com/o/blank-profile-picture-g2d25015e1_1280.png?alt=media&token=2148d672-1010-4765-86f2-598b54433b88"
                        reference.setValue(User(auth.uid,name,email,imageURI,status)).addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(this,"User Successfully created.",Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@RegistrationActivity,HomeActivity::class.java))

                            }else{
                                Toast.makeText(this,"Could not create user",Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }else{
                    progressDialog.dismiss()
                    Toast.makeText(this,"Something Went Wrong",Toast.LENGTH_LONG).show()
                }

            }.addOnFailureListener{exception->
                Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }



    }
    fun SignIn(view: View){
        val intent=Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()

    }

    fun gorselSec(view:View){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            //izin alınmadı
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)

        }else{
            //izin varsa yaoılacaklar
            val galeriIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2) // bir sonuç için başlatmayı ifade eder

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode==1){
            if(grantResults.size > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //izin verirlirse yapılacaklar
                val galeriIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==2 && resultCode== Activity.RESULT_OK&&data!=null){ // istekte 2 kodunu kullandık

            secilenGorsel = data.data
            if(secilenGorsel!=null){
                if(Build.VERSION.SDK_INT>=28){
                    val source= ImageDecoder.createSource(this.contentResolver,secilenGorsel!!)
                    secilenBitmap=ImageDecoder.decodeBitmap(source)
                    registration_profile_image.setImageBitmap(secilenBitmap)


                }else{
                    secilenBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,secilenGorsel)
                    registration_profile_image.setImageBitmap(secilenBitmap)
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}