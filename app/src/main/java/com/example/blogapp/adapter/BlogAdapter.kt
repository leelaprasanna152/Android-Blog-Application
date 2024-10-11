package com.example.blogapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.Model.BlogItemModel
import com.example.blogapp.ReadMoreActivity
import com.example.blogapp.databinding.BlogItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Log
import com.example.blogapp.R

class BlogAdapter(private val items: MutableList<BlogItemModel>) :
    RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance("https://blogapp-6d0f5-default-rtdb.asia-southeast1.firebasedatabase.app").reference
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BlogItemBinding.inflate(inflater, parent, false)
        return BlogViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blogItem = (items[position])
        holder.bind(blogItem)
    }

    inner class BlogViewHolder(private val binding: BlogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(blogItemModel: BlogItemModel) {

            val postId = blogItemModel.postId
            val context = binding.root.context

            binding.heading.text = blogItemModel.heading
//            Glide.with(binding.profile.context)
//                .load(blogItemModel.profileImage)
//                .into(binding.profile)
            binding.userName.text = blogItemModel.userName
            binding.date.text = blogItemModel.date
            binding.post.text = blogItemModel.post
            binding.likeCount.text = blogItemModel.likeCount.toString()

            //set on click listener
            binding.root.setOnClickListener{

                val intent = Intent(context,ReadMoreActivity::class.java)
                intent.putExtra("blogItem",blogItemModel)
                context.startActivity(intent)
            }

            //check id the current user has liked the post
            val postLikeReference:DatabaseReference = databaseReference.child("blogs").child(postId).child("likes")
            val currentUserLiked = currentUser?.uid?.let{uid->
                postLikeReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            binding.likebutton.setImageResource(R.drawable.heart_fill_red)
                        }
                        else{
                            binding.likebutton.setImageResource(R.drawable.heart_black)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }
            //handle like buttons
            binding.likebutton.setOnClickListener{
                if(currentUser!=null){
                    handleLikeButtonClicked(postId,blogItemModel,binding)
                }
                else{
                    Toast.makeText(context,"You have to login first",Toast.LENGTH_SHORT).show()
                }
            }

            val userReference = databaseReference.child("users").child(currentUser?.uid?:"")
            val postSaveReference = userReference.child("saveBlogPosts").child(postId)
            postSaveReference.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        binding.savebutton.setImageResource(R.drawable.save_articles_fill_red)

                    }
                    else{
                        binding.savebutton.setImageResource(R.drawable.unsave_articles_red)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

            //handle save buttons
            binding.savebutton.setOnClickListener {
                // Add your save functionality here
                if(currentUser!=null){
                    handleSaveButtonClicked(postId,blogItemModel,binding)
                }
                else{
                    Toast.makeText(context,"You have to login first",Toast.LENGTH_SHORT).show()
                }

            }

        }
    }



    private fun handleLikeButtonClicked(postId: String, blogItemModel: BlogItemModel,binding: BlogItemBinding) {
        val userReference = databaseReference.child("users").child(currentUser!!.uid)
        val postLikeReference = databaseReference.child("blogs").child(postId).child("likes")
        //user has already like the post

        postLikeReference.child(currentUser.uid).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    userReference.child("likes").child(postId).removeValue()
                        .addOnSuccessListener {
                            postLikeReference.child(currentUser.uid).removeValue()
                            blogItemModel.likedBy?.remove(currentUser.uid)
                            updateLikeButtonImage(binding,false)

                            val newLikeCount = blogItemModel.likeCount-1
                            blogItemModel.likeCount = newLikeCount
                            databaseReference.child("blogs").child(postId).child("likeCount").setValue(newLikeCount)
                            notifyDataSetChanged()
                        }
                        .addOnFailureListener{e->
                            Log.e("LikedClicked","onDataChange: Failed to unlike the blog $e",)
                        }
                }
                else{
                    userReference.child("likes").child(postId).setValue(true)
                        .addOnSuccessListener {
                            postLikeReference.child(currentUser.uid).setValue(true)
                            blogItemModel.likedBy?.add(currentUser.uid)
                            updateLikeButtonImage(binding,true)


                            val newLikeCount = blogItemModel.likeCount+1
                            blogItemModel.likeCount = newLikeCount
                            databaseReference.child("blogs").child(postId).child("likeCount").setValue(newLikeCount)
                            notifyDataSetChanged()
                        }
                        .addOnFailureListener{e->
                            Log.e("LikedClicked","onDataChange: Failed to like the blog $e",)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun updateLikeButtonImage(binding: BlogItemBinding,liked:Boolean){
        if(liked){
            binding.likebutton.setImageResource(R.drawable.heart_fill_black)
        }
        else{
            binding.likebutton.setImageResource(R.drawable.heart_fill_red)
        }

    }

    private fun handleSaveButtonClicked(postId: String, blogItemModel: BlogItemModel, binding: BlogItemBinding) {
        val userReference = databaseReference.child("users").child(currentUser!!.uid)
        userReference.child("saveBlogPosts").child(postId).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    userReference.child("saveBlogPosts").child(postId).removeValue()
                        .addOnSuccessListener {
                            //update the ui
                            val clickedBlogItem = items.find{ it.postId == postId}
                            clickedBlogItem?.isSaved= false
                            notifyDataSetChanged()

                            val context = binding.root.context
                            Toast.makeText(context,"Blog unsaved!",Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener{
                            val context = binding.root.context
                            Toast.makeText(context,"Failed to unsave the Blog",Toast.LENGTH_SHORT).show()
                        }
                    binding.savebutton.setImageResource(R.drawable.unsave_articles_red)
                }
                else{
                    userReference.child("saveBlogPosts").child(postId).setValue(true)
                        .addOnSuccessListener {
                            val clickedBlogItem = items.find{ it.postId == postId}
                            clickedBlogItem?.isSaved= true
                            notifyDataSetChanged()

                            val context = binding.root.context
                            Toast.makeText(context,"Blog Saved!",Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener{
                            val context = binding.root.context
                            Toast.makeText(context,"Failed to save the Blog",Toast.LENGTH_SHORT).show()
                        }
                    //change the save button

                    binding.savebutton.setImageResource(R.drawable.save_articles_fill_red)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    fun updateData(savedBlogArticles: List<BlogItemModel>) {

        items.clear()
        items.addAll(savedBlogArticles)
        notifyDataSetChanged()
    }
}
