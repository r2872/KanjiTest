package com.example.kanjitest.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kanjitest.adapters.KanjiClassListAdapter
import com.example.kanjitest.databinding.FragmentMainBinding
import com.example.kanjitest.datas.KanjiClassData
import com.example.kanjitest.datas.KanjiData
import com.example.kanjitest.viewmodels.MainViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class MainFragment : BaseFragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var mAdapter: KanjiClassListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.currentList.observe(this, Observer {
            mAdapter.notifyDataSetChanged()
        })

        setupEvents()
        setValues()

    }

    override fun onResume() {
        super.onResume()
        mAdapter.notifyDataSetChanged()
    }

    override fun setupEvents() {

    }

    override fun setValues() {
        mAdapter = KanjiClassListAdapter(mContext, viewModel.getClassList())
        binding.kanjiCLassRecyclerView.adapter = mAdapter
        binding.kanjiCLassRecyclerView.layoutManager = LinearLayoutManager(mContext)

        readJsonInStorage()
    }

    private fun readJsonInStorage() {
        viewModel.clearClassList()

        val jsonObj = getJson()

        getJsonArr(jsonObj, "1급")
        getJsonArr(jsonObj, "2급")
        getJsonArr(jsonObj, "3급")
        getJsonArr(jsonObj, "4급")
        getJsonArr(jsonObj, "5급")
        getJsonArr(jsonObj, "6급")
        getJsonArr(jsonObj, "7급")
        getJsonArr(jsonObj, "8급")
        getJsonArr(jsonObj, "준3급")
        getJsonArr(jsonObj, "준4급")
        getJsonArr(jsonObj, "준5급")
        getJsonArr(jsonObj, "준6급")
        getJsonArr(jsonObj, "준7급")
        getJsonArr(jsonObj, "준특급")
        getJsonArr(jsonObj, "특급")

        mAdapter.notifyDataSetChanged()
    }

    private fun getJsonArr(item: JSONObject, classText: String): JSONArray {
        val result = item.getJSONArray(classText)
        var title: String
        var radical: String
        var mean: String
        var writeCount: String
        var img: String
        val list = ArrayList<KanjiData>()
        var isChecked: String
        for (i in 0 until result.length()) {
            val jsonObj = result.getJSONObject(i)
            title = jsonObj.getString("title")
            radical = jsonObj.getString("radical")
            mean = jsonObj.getString("mean")
            writeCount = jsonObj.getString("writeCount")
            img = jsonObj.getString("img")
            isChecked = jsonObj.getString("checked")
            list.add(KanjiData(title, radical, mean, writeCount, img, isChecked))

        }
        viewModel.addClassList(
            KanjiClassData(
                classText,
                list
            )
        )
        return result
    }
}