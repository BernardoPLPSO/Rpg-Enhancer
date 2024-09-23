package com.example.rpgenhancer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.rpgenhancer.data.model.SpellDto

class SharedViewModel : ViewModel() {
    var soundUrl = mutableStateOf("")
    var spellDescription = mutableStateOf("")
    var imageUrl = mutableStateOf("")
    var id = mutableLongStateOf(0L)
    var isSpellLoaded = mutableStateOf(false)

    // Function to update the soundUrl
    fun updateSoundUrl(url: String) {
        soundUrl.value = url
    }

    fun updateImageUrl(url: String) {
        imageUrl.value = url
    }

    fun updateSpellDescription(description: String) {
        spellDescription.value = description
    }

    fun updateId(id : Long){
        this.id.value = id;
    }

    fun loadSpellOnce(spell: SpellDto?) {
        if (!isSpellLoaded.value && spell != null) {
            updateSoundUrl(spell.audioUrl ?: "")
            updateImageUrl(spell.imageUrl ?: "")
            isSpellLoaded.value = true
        }
    }

    fun updateSpellLoaded(state: Boolean){
        isSpellLoaded.value = state
    }
}