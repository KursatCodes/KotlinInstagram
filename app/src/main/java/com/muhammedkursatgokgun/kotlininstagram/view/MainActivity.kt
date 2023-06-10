package com.muhammedkursatgokgun.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muhammedkursatgokgun.kotlininstagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)

        auth= Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent= Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun signInClicked(view:View){
        val email= binding.editTextEmail.text.toString()
        val password= binding.editTextTextPassword.text.toString()

        if(email.equals("") || password.equals("")){
            Toast.makeText(this,"Enter email and password.",Toast.LENGTH_LONG).show()
        }else{
            auth.signInWithEmailAndPassword(email, password)
             .addOnSuccessListener {
             val intent= Intent(this, FeedActivity::class.java)
             startActivity(intent)
             finish()
            }.addOnFailureListener{
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signUpClicked(view:View){
        val email= binding.editTextEmail.text.toString()
        val password= binding.editTextTextPassword.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty()){
            auth.createUserWithEmailAndPassword(email, password)
             .addOnSuccessListener {
                val intent= Intent(this, FeedActivity::class.java)
                 startActivity(intent)
                 finish()
            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this,"Enter email and password.",Toast.LENGTH_LONG).show()
        }
    }


}