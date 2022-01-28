package com.autopro.kanjitest.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.autopro.kanjitest.KanjiDetailActivity
import com.autopro.kanjitest.R
import com.autopro.kanjitest.datas.KanjiData
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

        private val mPref = mContext.getSharedPreferences("viewSetting", Context.MODE_PRIVATE)
        private val mViewAll = mPref.getBoolean("viewAll", false)

        private val meanText = view.findViewById<TextView>(R.id.mean_txt)
        private val titleText = view.findViewById<TextView>(R.id.title_txt)
        private val radicalText = view.findViewById<TextView>(R.id.radical_txt)
        private val writeCountText = view.findViewById<TextView>(R.id.writeCount_txt)
        private val checkedImg = view.findViewById<ImageView>(R.id.checked_img)
        private val settingButton = view.findViewById<TextView>(R.id.setting_btn)

        @SuppressLint("CommitPrefEdits", "SetTextI18n")
        fun bind(item: KanjiData, position: Int) {
            Log.d("mViewAll", mViewAll.toString())

            if (mViewAll) {
                meanText.text = item.mean
                titleText.text = item.title
                radicalText.text = "부수 \n ${item.radical}"
                writeCountText.text = "획수 \n ${item.writeCount}"
            } else {
                titleText.text = item.title
            }

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
                } else {
                    kanjiData.put("checked", "true")
                    Log.d("false", kanjiData.toString())
                }
            }

            settingButton.setOnClickListener {

                val customView =
                    LayoutInflater.from(mContext).inflate(R.layout.my_custom_alert_setting, null)
                val radioGroup = customView.findViewById<RadioGroup>(R.id.radioGroup)
                val viewAllRadio = customView.findViewById<RadioButton>(R.id.viewAll_radio)
                val viewKanjiOnlyRadio =
                    customView.findViewById<RadioButton>(R.id.viewKanjiOnly_radio)
                if (mViewAll) {
                    viewAllRadio.isChecked = true
                } else {
                    viewKanjiOnlyRadio.isChecked = true
                }
                var viewAll: Boolean? = null
                radioGroup.setOnCheckedChangeListener { _, _ ->
                    if (viewAllRadio.isChecked) {
                        viewAll = true
                    } else if (viewKanjiOnlyRadio.isChecked) {
                        viewAll = false
                    }
                }
                val myAlert = AlertDialog.Builder(mContext)
                    .setView(customView)
                    .setTitle("보기 설정")
                    .setPositiveButton("확인") { _, _ ->
                        mPref.edit().putBoolean("viewAll", viewAll ?: true).apply()
                        Log.d("viewAll", (viewAll ?: true).toString())
                    }
                    .show()
            }
        }
    }
}