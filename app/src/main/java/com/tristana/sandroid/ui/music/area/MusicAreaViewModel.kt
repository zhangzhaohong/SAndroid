package com.tristana.sandroid.ui.music.area

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MusicAreaViewModel : ViewModel() {

    var searchMusicName = MutableLiveData<String>(null)
    var searchMusicServiceId = MutableLiveData(0)

}