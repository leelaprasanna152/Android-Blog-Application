package com.example.blogapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.blogapp.databinding.ActivityProfileBinding
import com.example.blogapp.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {

    private val binding:ActivityProfileBinding by lazy{
        ActivityProfileBinding.inflate(layoutInflater)
    }

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.addNewBlogButton.setOnClickListener{
            startActivity(Intent(this,AddArticleActivity::class.java))
        }

        binding.articlesButton.setOnClickListener{

            //startActivity(Intent(this,ArticleActivity::class.java))
            val userId = auth.currentUser?.uid
            val intent = Intent(this,ArticleActivity::class.java)
            intent.putExtra("userId",userId)
            startActivity(intent)


        }

        binding.logOutButton.setOnClickListener{
            auth.signOut()
            //navigate
            startActivity(Intent(this,WelcomeActivity::class.java))
            finish()
        }


        //Initialize firebase
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://blogapp-6d0f5-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users")

        val userId = auth.currentUser?.uid
        if(userId != null){
            loadUserProfileData(userId)
        }
    }
    private fun loadUserProfileData(userId: String) {
        val userReference = databaseReference.child(userId)
        //load user profile image
        userReference.child("profileImage").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val profileImageUrl = snapshot.getValue(String::class.java)
                if(profileImageUrl!=null){
                    Glide.with(this@ProfileActivity)
                        .load(profileImageUrl)
                        .into(binding.userProfile)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity,"Failed to load user image",Toast.LENGTH_SHORT).show()
            }
        })

        userReference.child("name").addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.getValue(String::class.java)
                if(userName!=null){
                    binding.userName.text = userName
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}