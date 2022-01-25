package com.example.kanjitest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.kanjitest.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        val myHandler = Handler(Looper.getMainLooper())
        myHandler.postDelayed({
            val myIntent = Intent(mContext, MainActivity::class.java)
            startActivity(myIntent)
            finish()
        }, 2000)
    }
}