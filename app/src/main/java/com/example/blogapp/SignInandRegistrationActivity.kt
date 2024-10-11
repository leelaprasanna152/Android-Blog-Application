package com.example.blogapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.blogapp.Model.UserData
import com.example.blogapp.databinding.ActivitySignInandRegistrationBinding
import com.example.blogapp.register.WelcomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class SignInandRegistrationActivity : AppCompatActivity() {

    private val binding: ActivitySignInandRegistrationBinding by lazy {
        ActivitySignInandRegistrationBinding.inflate(LayoutInflater.from(this))
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://blogapp-6d0f5-default-rtdb.asia-southeast1.firebasedatabase.app")
        storage = FirebaseStorage.getInstance()

        val action: String? = intent.getStringExtra("action")

        // Adjust visibility for login
        if (action == "login") {
            binding.loginEmailAddress.visibility = View.VISIBLE
            binding.loginPassword.visibility = View.VISIBLE
            binding.loginButton.visibility = View.VISIBLE

            binding.registerName.visibility = View.GONE
            binding.registerEmail.visibility = View.GONE
            binding.registerPassword.visibility = View.GONE
            binding.cardView.visibility = View.GONE
            binding.registerButton.visibility = View.INVISIBLE
//            binding.registerNewHere.visibility = View.INVISIBLE

            binding.loginButton.setOnClickListener{
                val loginEmail = binding.loginEmailAddress.text.toString()
                val loginPassword = binding.loginPassword.text.toString()
                if(loginEmail.isEmpty()||loginPassword.isEmpty()){
                    Toast.makeText(this,"Please fill all details",Toast.LENGTH_SHORT).show()
                }
                else{
                    auth.signInWithEmailAndPassword(loginEmail,loginPassword)
                        .addOnCompleteListener{task->
                            if(task.isSuccessful){
                                Toast.makeText(this,"Login Successful",Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this,MainActivity::class.java))
                                finish()
                            }
                            else{
                                Toast.makeText(this,"login Failed.please enter correct details",Toast.LENGTH_SHORT).show()
                            }

                        }
                }

            }
        } else if (action == "register") {
            // Adjust visibility for registration
            binding.registerName.visibility = View.VISIBLE
            binding.registerEmail.visibility = View.VISIBLE
            binding.registerPassword.visibility = View.VISIBLE
            binding.cardView.visibility = View.VISIBLE
            binding.registerButton.visibility = View.VISIBLE
//            binding.registerNewHere.visibility = View.VISIBLE

            binding.loginButton.visibility = View.INVISIBLE

            binding.registerButton.setOnClickListener {
                val registerName = binding.registerName.text.toString()
                val registerEmail = binding.registerEmail.text.toString()
                val registerPassword = binding.registerPassword.text.toString()
                if (registerName.isEmpty() || registerEmail.isEmpty() || registerPassword.isEmpty()) {
                    Toast.makeText(this, "Please Fill All The Details", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(registerEmail, registerPassword)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user: FirebaseUser? = auth.currentUser
                                auth.signOut()
                                user?.let {
                                    val userReference = database.getReference("users")
                                    val userId = user.uid
                                    val userData = UserData(
                                        registerName, registerEmail
                                    )
                                    userReference.child(userId).setValue(userData)
                                        .addOnSuccessListener{
                                            Log.d("TAG","onCreate:data saved")
                                        }
                                        .addOnFailureListener{e->
                                            Log.e("TAG","onCreate:Error saving data ${e.message}")
                                        }
                                    val storageReference = storage.reference.child("profile_image/$userId.jpg")

//                                    storageReference.putFile(imageUri!!)
//                                        .addOnCompleteListener{ task->
//                                            if(task.isSuccessful){
//                                                storageReference.downloadUrl.addOnCompleteListener{ imageUri->
//                                                    if(imageUri.isSuccessful){
//                                                        val imageUrl = imageUri.result.toString()
//                                                        //save the image url to the realtime database
//                                                        userReference.child(userId).child("profileImage").setValue(imageUrl)
////                                                        Glide.with(this)
////                                                            .load(imageUri)
////                                                            .apply(RequestOptions.circleCropTransform())
////                                                            .into(binding.registerUserImage)
//
//                                                    }
//                                                }
//                                            }
//                                        }
                                    if (imageUri != null) {
                                        storageReference.putFile(imageUri!!)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    storageReference.downloadUrl.addOnCompleteListener { imageUriTask ->
                                                        if (imageUriTask.isSuccessful) {
                                                            val imageUrl = imageUriTask.result.toString()
                                                            // Save the image URL to the Realtime Database
                                                            userReference.child(userId).child("profileImage").setValue(imageUrl)
                                                                .addOnSuccessListener {
                                                                    Log.d("SignInandRegistration", "Profile image uploaded successfully")
                                                                }
                                                                .addOnFailureListener { e ->
                                                                    Log.e("SignInandRegistration", "Error saving image URL to database: ${e.message}")
                                                                }
                                                        } else {
                                                            Log.e("SignInandRegistration", "Error getting download URL")
                                                        }
                                                    }
                                                } else {
                                                    Log.e("SignInandRegistration", "Image upload failed: ${task.exception?.message}")
                                                }
                                            }
                                    } else {
                                        Log.e("SignInandRegistration", "Image URI is null, cannot upload")
                                    }
                                    Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this,WelcomeActivity::class.java))
                                    finish()

                                }
                            } else {

                                Toast.makeText(this, "User Registration Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
        }
        binding.cardView.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            if(imageUri!=null){
                Glide.with(this)
                    .load(imageUri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.registerUserImage)
            }
            else{
                Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
