package com.tech.safezone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingAnimation: LottieAnimationView



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        title = "Sign Up"
        auth = FirebaseAuth.getInstance()

        val name = findViewById<EditText>(R.id.name)
        val username = findViewById<EditText>(R.id.username)
        val phone = findViewById<EditText>(R.id.phoneNo)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)
        val submit = findViewById<Button>(R.id.signup)
        loadingAnimation = findViewById(R.id.loading_animation)
        val parentLayout = findViewById<FrameLayout>(R.id.parent_layout)
        val login = findViewById<TextView>(R.id.Login)
        login.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))

        }


        submit.setOnClickListener {
            val name = name.text.toString()
            val email = username.text.toString()
            val phone = phone.text.toString()
            val password = password.text.toString()
            val confirmPassword = confirmPassword.text.toString()
            if(name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()){
                Toast.makeText(this,"All fields must be filled",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confirmPassword != password) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            else {
                showLoading(true)
                parentLayout.isClickable = false
                parentLayout.isFocusable = false

                register(name, email, phone, password)
            }
        }
    }
    fun showLoading(isLoading: Boolean) {
        if(isLoading){
            loadingAnimation.visibility = View.VISIBLE
            loadingAnimation.playAnimation()
        }else{
            loadingAnimation.visibility = View.GONE
            loadingAnimation.pauseAnimation()
        }
    }

    fun register(name: String, email: String, phone: String, password: String) {
        Log.d("RegisterActivity", "Attempting registration with email: $email")
        val parentLayout = findViewById<FrameLayout>(R.id.parent_layout)
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("RegisterActivity", "Auth successful, UID: ${task.result?.user?.uid}")

                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val firestore = FirebaseFirestore.getInstance()
                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "phone" to phone
                    )

                    firestore.collection("users").document(userId).set(userData)
                        .addOnCompleteListener { firestoreTask ->
                            showLoading(false)
                            parentLayout.isClickable = true
                            parentLayout.isFocusable = true

                            if (firestoreTask.isSuccessful) {
                                Toast.makeText(applicationContext, "User registered successfully", Toast.LENGTH_SHORT).show()
                                Log.d("RegisterActivity", "User data added to Firestore")
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val exception = firestoreTask.exception
                                Log.e("RegisterActivity", "Firestore registration failed: ${exception?.message}")
                                Toast.makeText(applicationContext, "Firestore Registration Failed: ${exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    val exception = task.exception
                    Log.e("RegisterActivity", "Registration failed: ${exception?.localizedMessage}")
                    Toast.makeText(applicationContext, "Registration Failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    showLoading(false)
                    parentLayout.isClickable = true
                    parentLayout.isFocusable = true
                }
            } else {
                val exception = task.exception
                Log.e("RegisterActivity", "Registration failed: ${exception?.localizedMessage}")
                Toast.makeText(applicationContext, "Registration Failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                showLoading(false)
                parentLayout.isClickable = true
                parentLayout.isFocusable = true
            }
        }
    }
}
