package com.autopro.kanjitest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.autopro.kanjitest.databinding.ActivitySplashBinding
import com.autopro.kanjitest.datas.GlobalData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

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

        checkPremium()
    }

    private fun checkPremium() {
        if (mAuth.currentUser != null) {
            GlobalData.isSignIn = true
            val userEmail = mAuth.currentUser?.email.toString().split("@")[0]
            mDatabase = Firebase.database.getReference("Users").child(userEmail)
            mDatabase.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val getIsPremium = data.value
                        GlobalData.checkPremium = getIsPremium == true
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}