package com.example.kanjitest.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.kanjitest.KanjiDetailActivity
import com.example.kanjitest.R
import com.example.kanjitest.datas.KanjiClassData
import com.example.kanjitest.datas.KanjiData

class KanjiListAdapter(
    private val mContext: Context,
    private val mList: List<KanjiData>,
    private val classNum: String
) : RecyclerView.Adapter<KanjiListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.kanji_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(mList[position], position)
    }

    inner class ViewHolder(view: View) : BaseViewHolder(mContext, view) {

        private val backgroundLayout = view.findViewById<ConstraintLayout>(R.id.backgroundLayout)
        private val kanjiMean = view.findViewById<TextView>(R.id.kanjiMean_txt)
        private val kanji = view.findViewById<TextView>(R.id.kanji_txt)
        private val isCheckedImg = view.findViewById<ImageView>(R.id.isChecked_img)

        fun bind(item: KanjiData, position: Int) {

            kanji.text = item.title
            val firstMean = item.mean.split(",")
            val reSplit = firstMean[0].split("/")
            val isChecked = item.isChecked
            if (firstMean[0] == reSplit[0]) {
                kanjiMean.text = firstMean[0]
            } else {
                kanjiMean.text = reSplit[0]
            }
            if (isChecked == "true") {
                isCheckedImg.isVisible = true
            }

            backgroundLayout.setOnClickListener {

                val intent = Intent(mContext, KanjiDetailActivity::class.java)
                intent.putExtra("kanjiListData", KanjiClassData(classNum, mList))
                intent.putExtra("clickedPosition", position)
                mContext.startActivity(intent)
            }
        }
    }
}