package com.autopro.kanjitest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.autopro.kanjitest.adapters.KanjiDetailAdapter
import com.autopro.kanjitest.databinding.ActivityKanjiDetailBinding
import com.autopro.kanjitest.datas.GlobalData
import com.autopro.kanjitest.datas.KanjiClassData

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

    }

    override fun setValues() {

        setAdBanner()

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

    private fun setAdBanner() {

        if (GlobalData.checkPremium) {
            binding.adBanner.visibility = View.GONE
        } else {
            binding.adBanner.apply {
                visibility = View.VISIBLE
                loadAd(mAdRequest)
            }
        }
    }
}