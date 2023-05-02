package com.example.dedikodu_mesajlasmauygulamasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.dedikodu_mesajlasmauygulamasi.profile.profileActivity
import com.google.firebase.auth.EmailAuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.Save

class ChangePassword : AppCompatActivity() {
    private lateinit var mUser: FirebaseUser
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        mAuth= FirebaseAuth.getInstance()

        Save.setOnClickListener {
            changePassword()
        }

    }
    private fun changePassword(){
        if(password.text.isNotEmpty()&&newPassword.text.isNotEmpty()&&newPassword2.text.isNotEmpty()){
            if(password.text.toString()!=newPassword.toString()){
                if(newPassword.text.toString()==newPassword2.text.toString()){
                    mUser=mAuth.currentUser!!
                    val credential=EmailAuthProvider.getCredential(mUser.email!!,password.text.toString())
                    mUser?.reauthenticate(credential)?.addOnCompleteListener() {
                        if(it.isSuccessful){
                            Toast.makeText(this,"Re-Authentication success.",Toast.LENGTH_SHORT).show()
                            mUser?.updatePassword(newPassword.text.toString())?.addOnCompleteListener { task->
                                if(task.isSuccessful){
                                    Toast.makeText(this,"Password changed successfully.",Toast.LENGTH_SHORT).show()
                                    mAuth.signOut()
                                    val intent=Intent(this,LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Toast.makeText(this,"The password was not successfully changed.",Toast.LENGTH_SHORT).show()
                                }
                            }

                        }else{
                            Toast.makeText(this,"Re-Authentication failed.",Toast.LENGTH_SHORT).show()

                        }
                    }
                }else{
                    newPassword.error="Not the same as the new password"
                }
            }else{
                newPassword.error="The new password cannot be the same as the previous one"
            }


        }else{
            Toast.makeText(this,"Make sure you fill them all.",Toast.LENGTH_SHORT).show()
        }
    }
    fun BackClick(view: View){
        val intent= Intent(this, profileActivity::class.java)
        startActivity(intent)
        finish()
    }

}