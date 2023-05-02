package com.example.dedikodu_mesajlasmauygulamasi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    var email:String=""
    var password:String=""
    private val  emailPattern:String = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth= FirebaseAuth.getInstance()
        val guncelKullanici=auth.currentUser
        if(guncelKullanici!=null){
            val intent=Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


    }
    fun signUp(view: View){
        val intent=Intent(this,RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun signinButton(view:View){
        email=loginEmail.text.toString()
        password=loginPassword.text.toString()

        fun isEmailValid(email: String): Boolean {
            return Pattern.compile(
                emailPattern
            ).matcher(email).matches()
        }


        if(email.isEmpty()||password.isEmpty()){

            Toast.makeText(this,"Enter your username and password",Toast.LENGTH_LONG).show()

        }else if(!isEmailValid(email)){

            loginEmail.error = "Invalid Email"
            Toast.makeText(this,"Invalid email",Toast.LENGTH_LONG).show()
        }else if(password.length<6){
            loginPassword.error = "Invalid Password"
            Toast.makeText(this,"Password must be a maximum of 6 characters",Toast.LENGTH_LONG).show()
        }else{
            auth= FirebaseAuth.getInstance()
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener {task->
                if (task.isSuccessful){
                    val intent=Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this,"Eror in login",Toast.LENGTH_LONG).show()
                }
        }







        }

    }
}