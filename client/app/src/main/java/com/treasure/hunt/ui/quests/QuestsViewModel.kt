package com.treasure.hunt.ui.quests

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class QuestsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the quests Fragment"
    }
    val text: LiveData<String> = _text
}