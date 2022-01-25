package com.example.kanjitest

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kanjitest.adapters.KanjiListAdapter
import com.example.kanjitest.databinding.ActivityKanjiClassBinding
import com.example.kanjitest.datas.KanjiData
import com.example.kanjitest.viewmodels.MainViewModel

class KanjiClassActivity : BaseActivity() {

    private lateinit var binding: ActivityKanjiClassBinding
    private lateinit var mAdapter: KanjiListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKanjiClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setValues()
        setupEvents()
        supportActionBar?.let {
            setCustomActionBar()
            kanjiClassText.text = titleText
            backButton.setOnClickListener {
                onBackPressed()
            }
        }

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun setupEvents() {

    }

    override fun onResume() {
        super.onResume()
        mAdapter.notifyDataSetChanged()
    }

    override fun setValues() {

        titleText = intent.getStringExtra("classNum") as String
        val getKanjiData = intent.getSerializableExtra("kanjiClassName") as List<KanjiData>
        mAdapter =
            KanjiListAdapter(mContext, getKanjiData, intent.getStringExtra("classNum") as String)
        binding.kanjiRecyclerView.adapter = mAdapter
        val gridLayoutManager = GridLayoutManager(mContext, 5)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.kanjiRecyclerView.layoutManager = gridLayoutManager
    }
}