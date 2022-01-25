package com.example.kanjitest.datas

import java.io.Serializable

data class KanjiData(
    val title: String,
    val radical: String,
    val mean: String,
    val writeCount: String,
    val img: String,
    val isChecked: String
) : Serializable