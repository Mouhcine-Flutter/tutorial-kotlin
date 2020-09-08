package com.example.kotlin_audioplayer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AudioFilesViewModel: ViewModel(){
    val audioFiles = MutableLiveData<List<AudioFile>>(listOf())
}