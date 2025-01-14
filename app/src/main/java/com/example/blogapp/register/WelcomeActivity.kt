package com.example.blogapp.register

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapp.MainActivity
import com.example.blogapp.SignInandRegistrationActivity
import com.example.blogapp.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    private val binding: ActivityWelcomeBinding by lazy{
        ActivityWelcomeBinding.inflate(layoutInflater)
    }
    //private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        //auth = FirebaseAuth.getInstance()
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, SignInandRegistrationActivity::class.java)
            intent.putExtra("action", "login")
            startActivity(intent)
            finish()
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, SignInandRegistrationActivity::class.java)
            intent.putExtra("action", "register")
            startActivity(intent)
            finish()
        }
    }
//    override fun onStart(){
//        super.onStart()
//        val currentUser = auth.currentUser
//        if(currentUser!=null){
//            startActivity(Intent(this, WelcomeActivity::class.java))
//            finish()
//
//        }
//    }
}
