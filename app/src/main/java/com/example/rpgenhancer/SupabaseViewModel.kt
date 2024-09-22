package com.example.rpgenhancer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rpgenhancer.data.model.UserState
import com.example.rpgenhancer.data.network.SupabaseClient.client
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch


class SupabaseViewModel : ViewModel() {
    private val _userState = mutableStateOf<UserState>(UserState.Loading)
    val userState: State<UserState> = _userState

    fun createBucket(name: String) {
        viewModelScope.launch {
            try {
                _userState.value = UserState.Loading
                client.storage.createBucket(id = name) {
                    public = true
                    fileSizeLimit = 10.megabytes
                }
                _userState.value = UserState.Success("Created bucket successfully")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun uploadFile(bucketName: String,fileName: String, byteArray: ByteArray, fileType: String) {
        viewModelScope.launch {
            try {
                _userState.value = UserState.Loading
                val bucket = client.storage[bucketName]
                bucket.upload("$fileName.$fileType",byteArray,true)
                _userState.value = UserState.Success("File uploaded successfully!")
            } catch(e: Exception) {
                e.printStackTrace()
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun readPublicFile(
        bucketName: String,
        fileName: String,
        fileType: String,
        onImageUrlRetrieved: (url: String) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                _userState.value = UserState.Loading
                val bucket = client.storage[bucketName]
                val url = bucket.publicUrl("$fileName.$fileType")
                onImageUrlRetrieved(url)
                _userState.value = UserState.Success("File read successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }

    fun readAllFiles(
        bucketName: String,
        onResult: (List<String>) -> Unit,
    ) {
        viewModelScope.launch {
            try {
                _userState.value = UserState.Loading
                val fileList = client.storage.from(bucketName).list()
                // Map the file paths to their public URLs
                val publicUrls = fileList.map { file ->
                    client.storage.from(bucketName).publicUrl(file.name)
                }
                onResult(publicUrls)
                _userState.value = UserState.Success("File read successfully!")
            } catch (e: Exception) {
                _userState.value = UserState.Error("Error: ${e.message}")
            }
        }
    }
}