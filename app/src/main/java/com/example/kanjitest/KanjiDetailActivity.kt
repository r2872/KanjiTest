package com.example.kanjitest

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.kanjitest.adapters.KanjiDetailAdapter
import com.example.kanjitest.databinding.ActivityKanjiDetailBinding
import com.example.kanjitest.datas.KanjiClassData

class KanjiDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityKanjiDetailBinding
    private lateinit var mAdapter: KanjiDetailAdapter
    private var position = 0
    private var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKanjiDetailBinding.inflate(layoutInflater)
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
    }

    override fun setupEvents() {

        binding.nextBtn.setOnClickListener {
            if (!binding.detailRecyclerView.canScrollHorizontally(1)) {
                shortToast("마지막 페이지 입니다.")
                return@setOnClickListener
            }
            position++
            Log.d("position", position.toString())
            binding.detailRecyclerView.smoothScrollToPosition(position)
        }
        binding.previousBtn.setOnClickListener {
            if (!binding.detailRecyclerView.canScrollHorizontally(-1)) {
                shortToast("첫 페이지 입니다.")
                return@setOnClickListener
            }
            position--
            Log.d("position", position.toString())
            binding.detailRecyclerView.smoothScrollToPosition(position)
        }
    }

    override fun setValues() {

        val getKanjiClassData = intent.getSerializableExtra("kanjiListData") as KanjiClassData
        titleText = getKanjiClassData.className
        position = intent.getIntExtra("clickedPosition", 0)
        mAdapter =
            KanjiDetailAdapter(mContext, getKanjiClassData.detail, getKanjiClassData.className)
        binding.detailRecyclerView.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
            scrollToPosition(position)
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.detailRecyclerView)

    }

    private fun shortToast(message: String) {

        if (mToast == null) {
            mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT)
        } else {
            mToast!!.setText(message)
        }
        mToast?.show()
    }
}