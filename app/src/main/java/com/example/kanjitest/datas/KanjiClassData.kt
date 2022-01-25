package com.example.kanjitest.datas

import java.io.Serializable

data class KanjiClassData(
    val className: String,
    val detail: List<KanjiData>
): Serializable