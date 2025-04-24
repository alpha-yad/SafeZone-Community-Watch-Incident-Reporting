package com.tech.safezone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity:AppCompatActivity() {
    lateinit var email:EditText
    lateinit var phoneNo:EditText
    lateinit var password:EditText
    lateinit var button: Button
    lateinit var auth:FirebaseAuth
    private lateinit var lottieAnimationView: LottieAnimationView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        email = findViewById<EditText>(R.id.email)

        phoneNo = findViewById<EditText>(R.id.phone)
        password = findViewById<EditText>(R.id.password)
        button = findViewById<Button>(R.id.signin)
        lottieAnimationView = findViewById<LottieAnimationView>(R.id.loading_animation)
        auth = FirebaseAuth.getInstance()
        val parentLayout = findViewById<FrameLayout>(R.id.parent_layout)

        button.setOnClickListener{
           val email = email.text.toString()
            val phoneNo = phoneNo.text.toString()
           val password = password.text.toString()
            if(email.isEmpty() || phoneNo.isEmpty() || password.isEmpty()){
                return@setOnClickListener
            }
//            Clickable means the user can click on the view (like a button, image, etc.).
//            If a view is clickable, the user can interact with it by tapping on it.
//
//            Focusable means the view can receive focus when the user navigates through the app.
//            For example, when you're using the keyboard or other input methods,
//            a view that is focusable will be the one that the input goes to (like a text field where you can type).
            showLoading(true)

            parentLayout.isFocusable = false
            parentLayout.isClickable = false


            login(email,phoneNo,password)
        }

    }
    fun showLoading(isLoading: Boolean) {
        if(isLoading){
            lottieAnimationView.visibility = View.VISIBLE
            lottieAnimationView.playAnimation()
        }else{
            lottieAnimationView.visibility = View.GONE
            lottieAnimationView.pauseAnimation()
        }
    }
    fun login(email:String,phoneNo:String,password:String){
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
            task->
            if(task.isSuccessful){
                val userId = auth.currentUser?.uid
                if(userId != null){
                    checkPhoneNo(userId,phoneNo)
                }
            }
            else{

                task.addOnFailureListener{
                    exception->
                    Toast.makeText(this,exception.localizedMessage,Toast.LENGTH_SHORT).show()
                    // Hide loading animation and re-enable interaction
                    showLoading(false)
                    val parentLayout = findViewById<FrameLayout>(R.id.parent_layout)
                    parentLayout.isFocusable = true
                    parentLayout.isClickable = true

                }
            }
        }
    }

    private fun checkPhoneNo(userId: String, phoneNo: String) {
        val firestore = FirebaseFirestore.getInstance()
        val userRef = firestore.collection("users").document(userId)
        userRef.get().addOnCompleteListener{
            phonechecktask->
            if(phonechecktask.isSuccessful){
                val userData = phonechecktask.result
                val storedPhoneNo = userData?.getString("phone")
                if(storedPhoneNo == phoneNo){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
                else{
                    Toast.makeText(this, "Phone no Does not match", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Failed to retrieve user data",Toast.LENGTH_SHORT).show()
            }
            // Hide loading animation and re-enable interaction
            showLoading(false)
            val parentLayout = findViewById<FrameLayout>(R.id.parent_layout)
            parentLayout.isFocusable = true
            parentLayout.isClickable = true
        }
    }


}