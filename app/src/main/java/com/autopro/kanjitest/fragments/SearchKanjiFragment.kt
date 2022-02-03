package com.autopro.kanjitest.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autopro.kanjitest.R
import com.autopro.kanjitest.adapters.KanjiSearchListAdapter
import com.autopro.kanjitest.databinding.FragmentSearchKanjiBinding
import com.autopro.kanjitest.datas.KanjiData
import com.autopro.kanjitest.viewmodels.MainViewModel
import org.json.JSONArray
import org.json.JSONObject


class SearchKanjiFragment : BaseFragment() {

    private lateinit var binding: FragmentSearchKanjiBinding
    private lateinit var mAdapter: KanjiSearchListAdapter
    private var mCheckSearchType = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchKanjiBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.searchCurrentList.observe(this, Observer {
            mAdapter.notifyDataSetChanged()
        })

        setValues()
        setupEvents()
    }

    override fun setupEvents() {

        binding.searchImg.setOnClickListener {
            searchKanji()
        }
        binding.searchEdt.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchKanji()
                true
            } else false
        }

        binding.searchRecyclerView.addOnItemTouchListener(object :
            RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                hideKeyboard()
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    override fun setValues() {

        setSpinner()

        mAdapter = KanjiSearchListAdapter(mContext, viewModel.getSearchLList())
        binding.searchRecyclerView.adapter = mAdapter
        binding.searchRecyclerView.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
    }

    private fun readJsonInStorage(inputText: String) {
        viewModel.clearSearchLList()

        val jsonObj = getJson()

        getJsonArr(jsonObj, "1급", inputText)
        getJsonArr(jsonObj, "2급", inputText)
        getJsonArr(jsonObj, "3급", inputText)
        getJsonArr(jsonObj, "4급", inputText)
        getJsonArr(jsonObj, "5급", inputText)
        getJsonArr(jsonObj, "6급", inputText)
        getJsonArr(jsonObj, "7급", inputText)
        getJsonArr(jsonObj, "8급", inputText)
        getJsonArr(jsonObj, "준3급", inputText)
        getJsonArr(jsonObj, "준4급", inputText)
        getJsonArr(jsonObj, "준5급", inputText)
        getJsonArr(jsonObj, "준6급", inputText)
        getJsonArr(jsonObj, "준7급", inputText)
        getJsonArr(jsonObj, "준특급", inputText)
        getJsonArr(jsonObj, "특급", inputText)

        mAdapter.notifyDataSetChanged()
    }

    private fun getJsonArr(item: JSONObject, classText: String, inputText: String): JSONArray {

        val result = item.getJSONArray(classText)
        var title: String
        var radical: String
        var mean: String
        var writeCount: String
        var img: String
        var isChecked: String
        for (i in 0 until result.length()) {
            val jsonObj = result.getJSONObject(i)
            title = jsonObj.getString("title")
            radical = jsonObj.getString("radical")
            mean = jsonObj.getString("mean")
            writeCount = jsonObj.getString("writeCount")
            img = jsonObj.getString("img")
            isChecked = jsonObj.getString("checked")
            val meanPix = mean.replace(" ", "")
            val splitSlash = meanPix.split("/")
            val splitComma = meanPix.split(",")
            if (mCheckSearchType) {

                for (e in splitSlash.indices) {

                    if (splitSlash[e].endsWith(inputText)) {

                        viewModel.addSearchList(
                            KanjiData(title, radical, mean, writeCount, img, isChecked)
                        )
                        break

                    } else {
                        for (p in splitComma.indices) {
                            if (splitComma[p].endsWith(inputText)) {

                                viewModel.addSearchList(
                                    KanjiData(title, radical, mean, writeCount, img, isChecked)
                                )

                                break
                            }
                        }
                    }

                }
            } else {
                for (e in splitSlash.indices) {

                    val subtract = splitSlash[e].substring(0, splitSlash[e].length - 1)

                    if (subtract.contains(inputText)) {

                        viewModel.addSearchList(
                            KanjiData(title, radical, mean, writeCount, img, isChecked)
                        )
                        break

                    } else {
                        for (p in splitComma.indices) {

                            val subtract = splitComma[p].substring(0, splitComma[p].length - 1)

                            if (subtract.contains(inputText)) {

                                viewModel.addSearchList(
                                    KanjiData(title, radical, mean, writeCount, img, isChecked)
                                )

                                break
                            }
                        }
                    }

                }
            }
            if (title == inputText) {
                viewModel.addSearchList(
                    KanjiData(title, radical, mean, writeCount, img, isChecked)
                )
            }
        }

        return result
    }

    private fun hideKeyboard() {
        if (activity != null && activity!!.currentFocus != null) {

            val inputManager =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                activity!!.currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun searchKanji() {
        if (binding.searchEdt.length() == 0) {
            Toast.makeText(mContext, "검색 할 단어를 입력 해 주세요", Toast.LENGTH_SHORT).show()
            return
        } else {
            val inputText = binding.searchEdt.text.toString().replace(" ", "")
            readJsonInStorage(inputText)
            val result = viewModel.getSearchLList()
            if (result.isEmpty()) {
                Toast.makeText(mContext, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
            }
            hideKeyboard()
        }
    }

    private fun setSpinner() {
        val mySpinner = binding.spinner
        val adapter = ArrayAdapter.createFromResource(
            mContext,
            R.array.spinner_item,
            android.R.layout.simple_spinner_dropdown_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mySpinner.adapter = adapter

        mySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        binding.searchEdt.hint = "검색 할 한자의 음 을 입력 해 주세요"
                        mCheckSearchType = true
                    }
                    else -> {
                        binding.searchEdt.hint = "검색 할 한자의 훈 을 입력 해 주세요"
                        mCheckSearchType = false
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
}