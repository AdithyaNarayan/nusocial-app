package com.teamnusocial.nusocial.ui.messages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MessagesViewModel : ViewModel() {
    val numOfMessages: MutableLiveData<Int> = MutableLiveData(0)
}