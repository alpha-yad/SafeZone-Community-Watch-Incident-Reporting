package com.tech.safezone

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class RequestServiceActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("services")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_service)

        auth = FirebaseAuth.getInstance()

        // Check if user is logged in
        if (auth.currentUser == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val serviceTypeSpinner = findViewById<Spinner>(R.id.serviceTypeSpinner)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        // Define service types
        val serviceTypes = arrayOf(
            "Emergency Response",
            "Police Assistance",
            "Fire Department",
            "Ambulance",
            "Road Assistance",
            "Security Patrol"
        )

        // Create adapter for spinner
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            serviceTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        serviceTypeSpinner.adapter = adapter

        submitButton.setOnClickListener {
            val serviceType = serviceTypeSpinner.selectedItem.toString()
            val description = descriptionEditText.text.toString()

            if (description.isNotEmpty()) {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val serviceRequest = ServiceRequest(
                    userId = userId,
                    serviceType = serviceType,
                    description = description,
                    status = "Pending",
                    timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(Date())
                )

                Log.d("RequestService", "Submitting service request: $serviceRequest")
                
                val serviceRef = database.push()
                serviceRef.setValue(serviceRequest)
                    .addOnSuccessListener {
                        Log.d("RequestService", "Service request submitted successfully")
                        Toast.makeText(this, "Service request submitted successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e("RequestService", "Failed to submit service request", e)
                        Toast.makeText(this, "Failed to submit service request: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 