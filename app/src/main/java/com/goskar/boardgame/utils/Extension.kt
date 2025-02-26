package com.goskar.boardgame.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.core.content.ContextCompat


fun checkAndRequestPermissionWithClick(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>,
    onClick: () -> Unit = {}
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        // Do on click
        onClick()
    }  else {
        // Request a permission
        launcher.launch(permission)
    }
}


