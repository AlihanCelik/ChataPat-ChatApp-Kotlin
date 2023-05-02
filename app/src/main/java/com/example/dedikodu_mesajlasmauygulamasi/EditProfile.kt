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
import android.provider.ContactsContract.Profile
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dedikodu_mesajlasmauygulamasi.profile.profileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfile : AppCompatActivity() {
    var secilenGorsel: Uri?=null
    var secilenBitmap: Bitmap?=null
    var imageURI:String=""
    var name:String=""
    var status:String=""
    var email:String?=""
    var newname:String?=null
    var newstatus:String?=null
    var imageUrl:String?=null

    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        newname=intent.getStringExtra("name")
        newstatus=intent.getStringExtra("status")
        imageUrl=intent.getStringExtra("image")
        email= intent.getStringExtra("email")
        mAuth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()
        mUser=mAuth.currentUser!!
        storage= FirebaseStorage.getInstance()



        newName.text= Editable.Factory.getInstance().newEditable(newname)
        newStatus.text=Editable.Factory.getInstance().newEditable(newstatus)
        Picasso.get().load(imageUrl).into(new_profile_image)

    }
    fun SaveButton(view: View){
        val progressDialog = ProgressDialog(this@EditProfile)
        progressDialog.setMessage("Please wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        name=newName.text.toString()
        status=newStatus.text.toString()

        val storageReference=storage.reference.child("upload").child(mAuth.uid!!)

        if(name.isEmpty()||status.isEmpty()){
            progressDialog.dismiss()
            Toast.makeText(this,"Please Enter Valid Data",Toast.LENGTH_LONG).show()
        }
        else{
            if(secilenGorsel!=null) {
                storageReference.putFile(secilenGorsel!!).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageReference.downloadUrl.addOnSuccessListener {
                            imageURI = it.toString()


                            database.reference.child("user").child(mUser.uid)
                                .setValue(User(mUser.uid, name, email, imageURI, status))
                            progressDialog.dismiss()
                        }
                    }
                }
            }else{
                    database.reference.child("user").child(mUser.uid)
                        .setValue(User(mUser.uid, name, email,imageUrl, status))
                progressDialog.dismiss()
                }

        }





    }

    fun returnButton(view:View){
        newName.text= Editable.Factory.getInstance().newEditable(newname)
        newStatus.text=Editable.Factory.getInstance().newEditable(newstatus)
        Picasso.get().load(imageUrl).into(new_profile_image)
    }





    fun gorselSec(view: View){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            //izin alınmadı
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)

        }else{
            //izin varsa yaoılacaklar
            val galeriIntent= Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntent,2) // bir sonuç için başlatmayı ifade eder

        }
    }
    fun BackClick(view:View){
        val intent= Intent(this, profileActivity::class.java)
        startActivity(intent)
        finish()
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
                    secilenBitmap= ImageDecoder.decodeBitmap(source)
                    new_profile_image.setImageBitmap(secilenBitmap)


                }else{
                    secilenBitmap=
                        MediaStore.Images.Media.getBitmap(this.contentResolver,secilenGorsel)
                    new_profile_image.setImageBitmap(secilenBitmap)
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}