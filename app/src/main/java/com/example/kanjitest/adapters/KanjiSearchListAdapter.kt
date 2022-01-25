package com.example.kanjitest.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kanjitest.R
import com.example.kanjitest.datas.KanjiData

class KanjiSearchListAdapter(
    private val mContext: Context,
    private val mList: List<KanjiData>
) : RecyclerView.Adapter<KanjiSearchListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.search_lsit_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = mList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(mList[position])
    }

    inner class ViewHolder(view: View) : BaseViewHolder(mContext, view) {

        private val backgroundLayout = view.findViewById<ConstraintLayout>(R.id.backgroundLayout)
        private val kanjiTitle = view.findViewById<TextView>(R.id.kanjiTitle_txt)
        private val kanjiMean = view.findViewById<TextView>(R.id.kanjiMean_txt)

        fun bind(item: KanjiData) {

            kanjiTitle.text = item.title
            kanjiMean.text = item.mean

            backgroundLayout.setOnClickListener {

                val customView = LayoutInflater.from(mContext)
                    .inflate(R.layout.my_custom_alert_kanji_search_detail, null)
                val meanTxt = customView.findViewById<TextView>(R.id.mean_txt)
                val radicalText = customView.findViewById<TextView>(R.id.radical_txt)
                val titleText = customView.findViewById<TextView>(R.id.title_txt)
                val writeCountText = customView.findViewById<TextView>(R.id.writeCount_txt)
                val myAlert = AlertDialog.Builder(mContext)
                    .setView(customView)
                    .show()
                meanTxt.text = item.mean
                radicalText.text = item.radical
                titleText.text = item.title
                writeCountText.text = item.writeCount
            }
        }
    }
}