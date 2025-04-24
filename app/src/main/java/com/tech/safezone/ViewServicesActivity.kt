package com.tech.safezone

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewServicesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var serviceList: ArrayList<ServiceRequest>
    private lateinit var adapter: ServiceAdapter
    private val database = FirebaseDatabase.getInstance().getReference("services")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_services)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        serviceList = ArrayList()
        adapter = ServiceAdapter(serviceList)
        recyclerView.adapter = adapter

        // Fetch services from Firebase
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                serviceList.clear()
                for (dataSnapshot in snapshot.children) {
                    val service = dataSnapshot.getValue(ServiceRequest::class.java)
                    if (service != null) {
                        serviceList.add(service)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewServicesActivity, "Failed to load services", Toast.LENGTH_SHORT).show()
            }
        })
    }
} 