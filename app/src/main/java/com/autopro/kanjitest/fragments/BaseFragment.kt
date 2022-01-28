package com.autopro.kanjitest.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.autopro.kanjitest.viewmodels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject

abstract class BaseFragment : Fragment() {

    lateinit var mContext: Context
    lateinit var viewModel: MainViewModel
    lateinit var mAuth: FirebaseAuth

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mContext = requireContext()
    }

    abstract fun setupEvents()
    abstract fun setValues()

    fun getJson(): JSONObject {
        val assetManager = resources.assets
        val inputStream = assetManager.open("json/kanjiClassData.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        return JSONObject(jsonString)
    }

}