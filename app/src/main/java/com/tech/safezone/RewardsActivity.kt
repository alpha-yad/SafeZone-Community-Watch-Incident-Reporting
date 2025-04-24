package com.tech.safezone

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RewardsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("rewards")
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RewardsAdapter
    private val rewards = ArrayList<Reward>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rewards)

        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.rewardsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RewardsAdapter(rewards)
        recyclerView.adapter = adapter

        loadUserPoints()
        loadRewards()
    }

    private fun loadUserPoints() {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).child("points").get()
            .addOnSuccessListener { snapshot ->
                val points = snapshot.getValue(Int::class.java) ?: 0
                findViewById<TextView>(R.id.pointsTextView).text = "Your Points: $points"
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load points", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadRewards() {
        database.child("available_rewards").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rewards.clear()
                for (dataSnapshot in snapshot.children) {
                    val reward = dataSnapshot.getValue(Reward::class.java)
                    if (reward != null) {
                        rewards.add(reward)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RewardsActivity, "Failed to load rewards", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 