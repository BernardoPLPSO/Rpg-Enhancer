package com.example.rpgenhancer

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    var soundUrl = mutableStateOf("")
    var spellDescription = mutableStateOf("")

    // Function to update the soundUrl
    fun updateSoundUrl(url: String) {
        soundUrl.value = url
    }

    fun updateSpellDescription(description: String) {
        spellDescription.value = description
    }
}