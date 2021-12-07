package com.treasure.hunt.ui.booty

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BootyViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the booty Fragment"
    }
    val text: LiveData<String> = _text
}