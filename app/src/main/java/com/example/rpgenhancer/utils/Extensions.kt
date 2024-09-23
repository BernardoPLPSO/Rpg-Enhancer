package com.example.rpgenhancer.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Environment
import androidx.compose.ui.graphics.ImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import io.ktor.utils.io.errors.IOException
import java.io.File
import java.io.FileOutputStream


@Throws(IOException::class)
fun Uri.uriToByteArray(context: Context) =
    context.contentResolver.openInputStream(this)?.use { it.buffered().readBytes() }


fun playAudioFromUri(context: Context, url: String) {
    val mediaPlayer = MediaPlayer.create(context, Uri.parse(url))
    mediaPlayer?.apply {
        setOnCompletionListener {
            release() // Release resources once playback is complete
        }
        start() // Start playback
    }
}

fun playAudioFromUrl(mediaPlayer: MediaPlayer, url: String, onCompletion: () -> Unit) {
    mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
    try {
        mediaPlayer.setDataSource(url) // Set the URL as the data source
        mediaPlayer.prepare() // Prepare the media player asynchronously
        mediaPlayer.start()
        // Start playback once the media player is prepared
        mediaPlayer.setOnPreparedListener { player ->
            player.start()
        }

        // Release the media player resources once playback is complete
        mediaPlayer.setOnCompletionListener {
            it.release()
            onCompletion() // Optionally notify the app when playback is complete
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun generateQRCode(spellId: Long): Bitmap? {
    // Concatenate the URL and additional info
//    val qrData = "$url|$additionalInfo"

    return try {
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(
            spellId.toString(),
            BarcodeFormat.QR_CODE,
            600,
            600
        )
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        bmp
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String): Boolean {
    val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "QR Codes")
    if (!directory.exists()) {
        directory.mkdirs() // Create the directory if it doesn't exist
    }
    val file = File(directory, "$fileName.png")
    return try {
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        true // Successfully saved
    } catch (e: IOException) {
        e.printStackTrace()
        false // Failed to save
    }
}