package com.example.kanjitest.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kanjitest.KanjiDetailActivity
import com.example.kanjitest.R
import com.example.kanjitest.datas.KanjiData
import org.json.JSONObject

class KanjiDetailAdapter(
    private val mContext: Context,
    private val mList: List<KanjiData>,
    private val classNum: String
) : RecyclerView.Adapter<KanjiDetailAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.kanji_detail_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(mList[position], position)
    }

    inner class ViewHolder(view: View) : BaseViewHolder(mContext, view) {

        private val meanText = view.findViewById<TextView>(R.id.mean_txt)
        private val titleText = view.findViewById<TextView>(R.id.title_txt)
        private val radicalText = view.findViewById<TextView>(R.id.radical_txt)
        private val writeCountText = view.findViewById<TextView>(R.id.writeCount_txt)
        private val checkedImg = view.findViewById<ImageView>(R.id.checked_img)

        fun bind(item: KanjiData, position: Int) {

            meanText.text = item.mean
            titleText.text = item.title
            radicalText.text = item.radical
            writeCountText.text = item.writeCount
            if (item.isChecked == "true") {
                checkedImg.setImageResource(R.drawable.ic_baseline_star_rate_24)
            }

            checkedImg.setOnClickListener {
                val jsonObj = (mContext as KanjiDetailActivity).getJson()
                val jsonArr = jsonObj.getJSONArray(classNum)
                val kanjiData = jsonArr[position] as JSONObject

                if (kanjiData.getString("checked") == "true") {
                    kanjiData.put("checked", "false")
                    Log.d("true", kanjiData.toString())
                }
                else {
                    kanjiData.put("checked", "true")
                    Log.d("false", kanjiData.toString())
                }
            }
        }
    }
}