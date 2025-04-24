package com.tech.safezone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth
    private val database = FirebaseDatabase.getInstance().getReference("reports")
    private val storage = FirebaseStorage.getInstance().getReference("report_images")
    private var selectedImageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        auth = FirebaseAuth.getInstance()

        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val previewImageView = findViewById<ImageView>(R.id.previewImageView)

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        submitButton.setOnClickListener {
            if (checkLocationPermission()) {
                submitReport(
                    titleEditText.text.toString(),
                    descriptionEditText.text.toString()
                )
            } else {
                requestLocationPermission()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                findViewById<ImageView>(R.id.previewImageView).setImageURI(uri)
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun submitReport(title: String, description: String) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val report = IncidentReport(
                            userId = auth.currentUser?.uid ?: "",
                            title = title,
                            description = description,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                .format(Date()),
                            imageUrl = ""
                        )

                        val reportRef = database.push()
                        reportRef.setValue(report)
                            .addOnSuccessListener {
                                selectedImageUri?.let { uri ->
                                    val imageRef = storage.child("${reportRef.key}.jpg")
                                    imageRef.putFile(uri)
                                        .addOnSuccessListener {
                                            imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                                reportRef.child("imageUrl").setValue(downloadUri.toString())
                                                    .addOnSuccessListener {
                                                        Toast.makeText(
                                                            this,
                                                            "Report submitted successfully!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        finish()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(
                                                            this,
                                                            "Failed to update image URL",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this,
                                                "Failed to upload image",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } ?: run {
                                    Toast.makeText(
                                        this,
                                        "Report submitted successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    this,
                                    "Failed to submit report",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
        }
    }
}