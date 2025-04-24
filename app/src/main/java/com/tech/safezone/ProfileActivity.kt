package com.tech.safezone

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("users")
    private val storage = FirebaseStorage.getInstance().getReference("profile_images")
    private var selectedImageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        val profileImageView = findViewById<ImageView>(R.id.profileImageView)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val phoneEditText = findViewById<EditText>(R.id.phoneEditText)
        val addressEditText = findViewById<EditText>(R.id.addressEditText)
        val saveButton = findViewById<Button>(R.id.saveButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val address = addressEditText.text.toString().trim()

            if (name.isNotEmpty()) {
                saveProfile(name, phone, address)
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        loadProfile()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                findViewById<ImageView>(R.id.profileImageView).setImageURI(uri)
            }
        }
    }

    private fun saveProfile(name: String, phone: String, address: String) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.child(userId)

        val userData = hashMapOf(
            "name" to name,
            "phone" to phone,
            "address" to address,
            "email" to auth.currentUser?.email
        )

        userRef.setValue(userData)
            .addOnSuccessListener {
                selectedImageUri?.let { uri ->
                    val imageRef = storage.child("$userId.jpg")
                    imageRef.putFile(uri)
                        .addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                userRef.child("profileImageUrl").setValue(downloadUri.toString())
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Failed to update profile image URL", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to upload profile image", Toast.LENGTH_SHORT).show()
                        }
                } ?: run {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfile() {
        val userId = auth.currentUser?.uid ?: return
        database.child(userId).get()
            .addOnSuccessListener { snapshot ->
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val phone = snapshot.child("phone").getValue(String::class.java) ?: ""
                val address = snapshot.child("address").getValue(String::class.java) ?: ""
                val profileImageUrl = snapshot.child("profileImageUrl").getValue(String::class.java)

                findViewById<EditText>(R.id.nameEditText).setText(name)
                findViewById<EditText>(R.id.phoneEditText).setText(phone)
                findViewById<EditText>(R.id.addressEditText).setText(address)

                // TODO: Load profile image using Glide or Picasso
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
    }
} 