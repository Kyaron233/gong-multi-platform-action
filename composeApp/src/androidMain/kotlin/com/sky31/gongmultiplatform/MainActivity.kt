package com.sky31.gongmultiplatform

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import com.sky31.gongmultiplatform.network.service.InstallService
import java.lang.ref.WeakReference

class MainActivity: ComponentActivity() {

    companion object {
        private var activityRef: WeakReference<MainActivity>? = null

        fun getInstance(): MainActivity? = activityRef?.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        activityRef = WeakReference(this)

        // TODO 之后移动到功能处
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                100
            )
        }

        setContent {
            App()
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (packageManager.canRequestPackageInstalls()) {
                InstallService.onPermissionGranted()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}