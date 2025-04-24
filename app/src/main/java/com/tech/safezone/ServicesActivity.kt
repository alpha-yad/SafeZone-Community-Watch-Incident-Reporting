package com.tech.safezone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ServicesActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("services")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        auth = FirebaseAuth.getInstance()

        val requestServiceButton = findViewById<Button>(R.id.requestServiceButton)
        val viewServicesButton = findViewById<Button>(R.id.viewServicesButton)
        val myServicesButton = findViewById<Button>(R.id.myServicesButton)

        requestServiceButton.setOnClickListener {
            startActivity(Intent(this, RequestServiceActivity::class.java))
        }

        viewServicesButton.setOnClickListener {
            startActivity(Intent(this, ViewServicesActivity::class.java))
        }

        myServicesButton.setOnClickListener {
            startActivity(Intent(this, MyServicesActivity::class.java))
        }
    }
} 