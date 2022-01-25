package com.example.kanjitest.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.kanjitest.datas.KanjiClassData
import com.example.kanjitest.datas.KanjiData

class MainViewModel : ViewModel() {

    private val _knajiClassCurrentList = MutableLiveData<List<KanjiClassData>>()
    private val kanjiClassList = mutableListOf<KanjiClassData>()
    val currentList: LiveData<List<KanjiClassData>> = _knajiClassCurrentList

    private val _searchCurrentList = MutableLiveData<List<KanjiData>>()
    private val searchKanjiList = mutableListOf<KanjiData>()
    val searchCurrentList: LiveData<List<KanjiData>> = _searchCurrentList

    init {
        _knajiClassCurrentList.value = kanjiClassList
        _searchCurrentList.value = searchKanjiList
    }

    fun addClassList(item: KanjiClassData) {
        kanjiClassList.add(item)
        _knajiClassCurrentList.value = kanjiClassList
    }

    fun getClassList(): List<KanjiClassData> {
        return kanjiClassList
    }

    fun clearClassList() {
        kanjiClassList.clear()
    }

    fun addSearchList(item: KanjiData) {
        searchKanjiList.add(item)
        _searchCurrentList.value = searchKanjiList
    }

    fun getSearchLList(): List<KanjiData> {
        return searchKanjiList
    }

    fun clearSearchLList() {
        searchKanjiList.clear()
    }
}