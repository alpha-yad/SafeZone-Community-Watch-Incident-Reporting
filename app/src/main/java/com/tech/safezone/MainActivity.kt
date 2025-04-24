package com.tech.safezone

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("reports")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        // Initialize card views
        val emergencyCard = findViewById<CardView>(R.id.card_emergency)
        val servicesCard = findViewById<CardView>(R.id.card_services)
        val reportingCard = findViewById<CardView>(R.id.card_reporting)
        val forumCard = findViewById<CardView>(R.id.card_forum)
        val profileCard = findViewById<CardView>(R.id.card_profile)
        val rewardsCard = findViewById<CardView>(R.id.card_rewards)

        // Set click listeners for each card
        emergencyCard.setOnClickListener {
            startActivity(Intent(this, EmergencyActivity::class.java))
        }

        servicesCard.setOnClickListener {
            startActivity(Intent(this, ServicesActivity::class.java))
        }

        reportingCard.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }

        forumCard.setOnClickListener {
            startActivity(Intent(this, ForumActivity::class.java))
        }

        profileCard.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        rewardsCard.setOnClickListener {
            startActivity(Intent(this, RewardsActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}