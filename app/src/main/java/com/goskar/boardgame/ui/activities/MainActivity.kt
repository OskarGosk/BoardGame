package com.goskar.boardgame.ui.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.goskar.boardgame.ui.ComposeApp
import com.goskar.boardgame.ui.theme.BoardGameTheme

const val CAMERA = "Camera"
const val CAMERA_PERMISSION_GRANTED = "Permission Granted"
const val CAMERA_PERMISSION_DENIED = "Permission Denied"
const val CAMERA_DIALOG = "Show camera permission dialog"

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i(CAMERA, CAMERA_PERMISSION_GRANTED)
        } else {
            Log.i(CAMERA, CAMERA_PERMISSION_DENIED)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoardGameTheme {
                ComposeApp()
            }
        }
        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> Log.i(
                CAMERA,
                CAMERA_PERMISSION_GRANTED
            )

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i(CAMERA, CAMERA_DIALOG )

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}