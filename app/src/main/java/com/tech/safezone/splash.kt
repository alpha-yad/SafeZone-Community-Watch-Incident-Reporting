package com.tech.safezone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView

class splash: AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        val lottieView: LottieAnimationView = findViewById(R.id.anim)

        // Start the animation
        lottieView.playAnimation()
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            startActivity(Intent(this,RegisterActivity::class.java))
            finish()
        },2000)


    }
}


