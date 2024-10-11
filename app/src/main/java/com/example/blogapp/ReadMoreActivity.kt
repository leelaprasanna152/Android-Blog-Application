
package com.example.blogapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.databinding.ActivityReadMoreBinding

class ReadMoreActivity : AppCompatActivity() {
    // Use ActivityReadMoreBinding for binding the layout
    private lateinit var binding: ActivityReadMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the correct binding class
        binding = ActivityReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set back button click listener
        binding.backButton.setOnClickListener {
            finish()
        }

        // Retrieve the BlogItemModel passed via Intent
        val blogs = intent.getParcelableExtra<BlogItemModel>("blogItem")

        // Check if the blog is not null and populate views with data
        if (blogs != null) {
            binding.titleText.text = blogs.heading
            binding.userName.text = blogs.userName
            binding.date.text = blogs.date
            binding.blogDescriptionTextView.text = blogs.post

            val userImageUrl = blogs.profileImage
            Glide.with(this)
                .load(userImageUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileImage)
        } else {
            // Show a toast if the blog could not be loaded
            Toast.makeText(this, "Failed to load blog", Toast.LENGTH_SHORT).show()
        }
    }
}
