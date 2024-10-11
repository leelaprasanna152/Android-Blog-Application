package com.example.blogapp


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.adapter.BlogAdapter
import com.example.blogapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var databaseReference: DatabaseReference
    private val blogItems = mutableListOf<BlogItemModel>()
    private lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //to go save article page
        binding.saveArticleButton.setOnClickListener{

            Toast.makeText(this, "Save Article Button Clicked", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ArticleActivity::class.java)
            startActivity(intent)
        }

        //to go profile activity

        binding.profileImage.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }
        binding.cardView2.setOnClickListener{
            startActivity(Intent(this,ProfileActivity::class.java))
        }

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://blogapp-6d0f5-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("blogs")

        val userId = auth.currentUser?.uid

//        if(userId!=null){
//            loadUserProfileImage(userId)
//        }

        val recyclerView = binding.blogRecyclerView
        val blogAdapter = BlogAdapter(blogItems)
        recyclerView.adapter = blogAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        //fetch data from firebase
        databaseReference.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                blogItems.clear()
               for(snapshot in snapshot.children){
                   val blogItem = snapshot.getValue(BlogItemModel::class.java)
                   if(blogItem!=null){
                       blogItems.add(blogItem)
                   }

               }
                blogItems.reverse()
                blogAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,"Blog loading failed",Toast.LENGTH_SHORT).show()
            }
        })
        binding.floatingAddArticleButton.setOnClickListener{
            startActivity(Intent(this,AddArticleActivity::class.java))
            finish()

        }

    }

//    private fun loadUserProfileImage(userId:String) {
//        val userReference = FirebaseDatabase.getInstance("https://blogapp-6d0f5-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users").child(userId)
//        userReference.child("profileImage").addValueEventListener(object:ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val profileImageUrl = snapshot.getValue(String::class.java)
//                if(profileImageUrl!=null){
//                    Glide.with(this@MainActivity)
//                        .load(profileImageUrl)
//                        .into(binding.profileImage)
//                }else {
//                    // Handle case where image URL is null
//                    Toast.makeText(this@MainActivity, "Profile image not found", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//               Toast.makeText(this@MainActivity,"Error loading profile image",Toast.LENGTH_SHORT).show()
//            }
//
//        })
//    }

}