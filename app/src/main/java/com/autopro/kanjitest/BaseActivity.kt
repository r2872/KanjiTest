package com.autopro.kanjitest

import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.autopro.kanjitest.viewmodels.MainViewModel
import com.google.android.gms.ads.AdRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import org.json.JSONObject

abstract class BaseActivity : AppCompatActivity() {

    lateinit var mContext: Context
    lateinit var kanjiClassText: TextView
    lateinit var viewModel: MainViewModel
    lateinit var titleText: String
    lateinit var backButton: ImageView
    lateinit var mAdRequest: AdRequest
    lateinit var mAuth: FirebaseAuth
    lateinit var mDatabase: DatabaseReference
    lateinit var randomTest: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this
        mAuth = FirebaseAuth.getInstance()
        mAdRequest = AdRequest.Builder().build()
    }

    abstract fun setupEvents()

    abstract fun setValues()

    fun setCustomActionBar() {

        val defActionBar = supportActionBar!!

        defActionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        defActionBar.setCustomView(R.layout.my_custom_action_bar)

        val toolBar = defActionBar.customView.parent as Toolbar
        toolBar.setContentInsetsAbsolute(0, 0)

        kanjiClassText =
            defActionBar.customView.findViewById(R.id.kanjiClass_txt)
        backButton = defActionBar.customView.findViewById(R.id.back_img)
        randomTest = defActionBar.customView.findViewById(R.id.randomTest)
    }

    fun getJson(): JSONObject {
        val inputStream = resources.openRawResource(R.raw.kanjiclassdata)
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        return JSONObject(jsonString)
    }
}