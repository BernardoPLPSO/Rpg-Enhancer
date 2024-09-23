package com.example.rpgenhancer.data.model

import android.os.Build
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime

@Serializable
data class SpellDto(
    @SerialName("id") val id: Long,                       // bigint (int8)
//    @SerialName("created_at") val createdAt: String,      // Use String to store timestamp
    @SerialName("description") val description: String?,   // text (nullable)
    @SerialName("audio_url") val audioUrl: String?,       // text (nullable)
    @SerialName("image_url") val imageUrl: String?        // text (nullable)
) {
//    fun getCreatedAtAsOffsetDateTime(): OffsetDateTime {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            OffsetDateTime.parse(createdAt)
//        } else {
//            TODO("VERSION.SDK_INT < O")
//        }
//    }
}