package com.tech.safezone

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class ForumActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("forum_posts")
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ForumAdapter
    private val posts = ArrayList<ForumPost>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ForumAdapter(posts)
        recyclerView.adapter = adapter

        val messageEditText = findViewById<EditText>(R.id.messageEditText)
        val postButton = findViewById<Button>(R.id.postButton)

        postButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                postMessage(message)
                messageEditText.text.clear()
            }
        }

        loadPosts()
    }

    private fun postMessage(message: String) {
        val post = ForumPost(
            userId = auth.currentUser?.uid ?: "",
            userName = auth.currentUser?.displayName ?: "Anonymous",
            message = message,
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        database.push().setValue(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add post", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadPosts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                posts.clear()
                for (dataSnapshot in snapshot.children) {
                    val post = dataSnapshot.getValue(ForumPost::class.java)
                    if (post != null) {
                        posts.add(post)
                    }
                }
                posts.reverse() // Show newest posts first
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ForumActivity, "Failed to load posts", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 