package com.autopro.kanjitest

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import com.autopro.kanjitest.adapters.MainViewPagerAdapter
import com.autopro.kanjitest.databinding.ActivityMainBinding
import com.autopro.kanjitest.datas.GlobalData
import com.google.android.material.tabs.TabLayout

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var mViewPagerAdapter: MainViewPagerAdapter
    private var waitTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        setAdBanner()

        mViewPagerAdapter = MainViewPagerAdapter(supportFragmentManager)
        binding.mainViewPager.adapter = mViewPagerAdapter
        binding.mainTabLayout.setupWithViewPager(binding.mainViewPager)
        binding.mainTabLayout.apply {
            getTabAt(0)?.setIcon(R.drawable.kanji)?.text = "급수 별 한자"
            getTabAt(1)?.setIcon(R.drawable.ic_baseline_search_24)?.text = "검색"
            getTabAt(2)?.setIcon(R.drawable.ic_baseline_info_24)?.text = "앱 정보"
        }
        binding.mainTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.toString() != "1") hideKeyboard()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        val root: View = binding.mainTabLayout.getChildAt(0)
        if (root is LinearLayout) {
            (root).showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            val drawable = GradientDrawable()
            drawable.setColor(resources.getColor(R.color.gray))
            drawable.setSize(2, 1)
            (root).dividerDrawable = drawable
        }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - waitTime >= 1500) {
            waitTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            finish()
        }

    }

    private fun hideKeyboard() {
        if (this.currentFocus != null) {

            val inputManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                this.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    fun setAdBanner() {

        val bool = GlobalData.checkPremium

        if (bool) {
            binding.adBanner.visibility = View.GONE
        } else {
            binding.adBanner.apply {
                visibility = View.VISIBLE
                loadAd(mAdRequest)
            }
        }
    }

    fun showAdBanner() {
        binding.adBanner.apply {
            visibility = View.VISIBLE
            loadAd(mAdRequest)
        }
    }
}