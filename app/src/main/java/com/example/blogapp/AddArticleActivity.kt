package com.example.blogapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.Model.UserData
import com.example.blogapp.databinding.ActivityAddArticleBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date

class AddArticleActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAddArticleBinding.inflate(layoutInflater)
    }
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://blogapp-6d0f5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("blogs")
    private val userReference: DatabaseReference = FirebaseDatabase.getInstance("https://blogapp-6d0f5-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.imageButton2.setOnClickListener{
            finish()
        }

        binding.addBlogButton.setOnClickListener { // Fixed method name
            val title = binding.blogTitle.editText?.text.toString().trim()
            val description = binding.blogDescription.editText?.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }

            val user: FirebaseUser? = auth.currentUser
            if (user != null) {
                val userId = user.uid
                val userName = user.displayName ?: "Anonymous"
                val userImageUrl = user.photoUrl?:"" // Ensure this is a String

                // fetch user name and user profile from database
                userReference.child(userId).addListenerForSingleValueEvent(object:ValueEventListener{
                    @SuppressLint("SimpleDateFormat")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData = snapshot.getValue(UserData::class.java)
                        if(userData!=null){
                            val userNameFromDB = userData.name
                            val userImageUrlFromDB = userData.profileImage

                            val currentDate = SimpleDateFormat("yyyy-MM-dd").format(Date())

                            //create a blogItemModel
                            val blogItem = BlogItemModel(
                                heading=title,
                                userName=userNameFromDB,
                                date=currentDate,
                                post=description,
                                userId = userId,
                                likeCount = 0,
                                profileImage = userImageUrlFromDB
                            )
                            //generate a unique key for the blog post
                            val key = databaseReference.push().key
                            if(key!=null){
                                blogItem.postId = key
                                val blogReference = databaseReference.child(key)
                                blogReference.setValue(blogItem).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@AddArticleActivity,
                                            "Failed to add blog",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                    override fun onCancelled(error:  DatabaseError) {
                    }
                })
            }
        }
    }
}