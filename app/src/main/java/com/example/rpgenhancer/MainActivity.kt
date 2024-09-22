package com.example.rpgenhancer

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.rpgenhancer.data.model.UserState
import com.example.rpgenhancer.ui.theme.RpgEnhancerTheme
import com.example.rpgenhancer.utils.generateQRCode
import com.example.rpgenhancer.utils.playAudioFromUrl
import com.example.rpgenhancer.utils.saveBitmapToFile
import com.example.rpgenhancer.utils.uriToByteArray
import com.google.zxing.integration.android.IntentIntegrator
import io.github.jan.supabase.annotations.SupabaseExperimental

class MainActivity : ComponentActivity() {
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RpgEnhancerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(sharedViewModel)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents != null) {
                    val parts = result.contents.split("|")
                    if (parts.size == 2){
                        val scannedUrl = parts[0]
                        val scannedInfo = parts[1]
                        Toast.makeText(this, "Scanned description: $scannedInfo", Toast.LENGTH_LONG).show()
                        sharedViewModel.updateSoundUrl(scannedUrl)
                        sharedViewModel.updateSpellDescription(scannedInfo)
                    }


                    // Update the soundUrl in the ViewModel

                }
            }
        }
    }
}


@OptIn(SupabaseExperimental::class)
@Composable
fun MainScreen(
    sharedViewModel: SharedViewModel,
    viewModel: SupabaseViewModel = viewModel(),
) {
    val context = LocalContext.current
    val userState by viewModel.userState
    val mediaPlayer = MediaPlayer()
    val soundUrl by sharedViewModel.soundUrl// Collect soundUrl from ViewModel
    val spellDescription by sharedViewModel.spellDescription


    var currentUserState by remember { mutableStateOf("") }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var imageUrl by remember {
        mutableStateOf("")
    }

    var isPlaying by remember { mutableStateOf(false) }


    var publicUrls by remember { mutableStateOf<List<String>>(emptyList()) } // State for holding public URLs
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
//        Button(onClick = { viewModel.createBucket("photos") }) {
//            Text(text = "Create bucket")
//        }
//
//        Button(onClick = { launcher.launch("*/*") }) {
//                Text(text = "Select Image")
//            }
//            if(imageUri != null) {
//                Button(onClick = {
//                    val imageByteArray = imageUri?.uriToByteArray(context)
//                    imageByteArray?.let {
//                        viewModel.uploadFile("sounds","soundtest",it, "mp3")
//                    }
//                }) {
//                    Text(text = "Upload Sound")
//                }
//        }

        Button(onClick = { viewModel.readPublicFile("sounds","soundtest", "mp3"){
            sharedViewModel.updateSoundUrl(it)
        } }) {
            Text(text = "Get Public Image")
        }

        Button(onClick = {
            IntentIntegrator(context as Activity).initiateScan()
        }) {
            Text(text = "Scan QR Code")
        }

        if(soundUrl.isNotEmpty()) {
            playAudioFromUrl(mediaPlayer,soundUrl) {
                isPlaying = false // Update state when playback completes
            }
            isPlaying = true // Indicate that the audio is playing
            sharedViewModel.updateSoundUrl("")
        }

        Text(text= if (spellDescription.isNotEmpty()) "Spell Description: $spellDescription" else "")

        Button(
            onClick = {
                playAudioFromUrl(mediaPlayer,soundUrl) {
                    isPlaying = false // Update state when playback completes
                }
                isPlaying = true // Indicate that the audio is playing
            },
            enabled = soundUrl != "" && !isPlaying // Disable button while audio is playing
        ) {
            Text(text = if (isPlaying) "Playing Audio..." else "Play Audio from URL")
        }

        Button(onClick = {
            qrCodeBitmap = generateQRCode(soundUrl, "description123")
        }) {
            Text(text = "Generate QR Code")
        }

        qrCodeBitmap?.let {
            saveBitmapToFile(context, qrCodeBitmap!!, "MyQRCode")
            Image(bitmap = it.asImageBitmap(), contentDescription = "QR Code")
        }

        // Button to list all public URLs in the bucket
//        Button(onClick = {
//            viewModel.readAllFiles("photos") { urls ->
//                publicUrls = urls
//            }
//        }) {
//            Text(text = "List All Public URLs")
//        }
//
//        // Display the list of public URLs
//        if (publicUrls.isNotEmpty()) {
//            LazyColumn {
//                items(publicUrls) { url ->
//                    Text(text = url)
//                }
//            }
//        }


        when (userState) {
            is UserState.Loading -> {
                LoadingComponent()
            }

            is UserState.Success -> {
                val message = (userState as UserState.Success).message
                currentUserState = message
            }

            is UserState.Error -> {
                val message = (userState as UserState.Error).message
                currentUserState = message
            }
        }

//        if(soundUrl.isNotEmpty()) {
//            Text("Sound URL: $soundUrl")
//        }

        Text(text = currentUserState)
        Text(text = if (imageUri != null) "Image is selected" else "")

        if(imageUrl.isNotEmpty()) {
            AsyncImage(model = imageUrl, contentDescription = "random image")
        }
    }
}
