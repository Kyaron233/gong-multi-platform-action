package com.sky31.gongmultiplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sky31.gongmultiplatform.di.networkModule
import com.sky31.gongmultiplatform.di.repositoryModule
import com.sky31.gongmultiplatform.di.securityModule
import com.sky31.gongmultiplatform.module.androidDatabaseModule
import com.sky31.gongmultiplatform.module.androidSecurityModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        startKoin {
            androidContext(this@MainActivity.applicationContext)
            modules(listOf(
                repositoryModule,
                androidDatabaseModule,
                androidSecurityModule,
                securityModule,
                networkModule
            ))
        }

        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}