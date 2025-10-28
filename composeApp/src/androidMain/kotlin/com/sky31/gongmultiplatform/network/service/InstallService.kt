package com.sky31.gongmultiplatform.network.service

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import com.sky31.gongmultiplatform.network.HttpClientProvider
import com.sky31.gongmultiplatform.network.api.ResourceApiImpl
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

actual object InstallService: KoinComponent {
    private val context: Context by inject()
    private val clientProvider: HttpClientProvider by inject()

    private val resourceApi = ResourceApiImpl(clientProvider.client)

    actual suspend fun downloadApk(url: String): Flow<Int> {
        try {
            return resourceApi.getApkZip(url) {response ->
                val outputFile = File(context.cacheDir, "release.zip")
                outputFile.outputStream().use { fileOutputStream ->
                    response.bodyAsChannel().copyTo(fileOutputStream)
                }

                unzip()
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun unzip() {
        val zipFile = File(context.cacheDir, "release.zip")
        val outputDir = File(context.cacheDir, "release")

        ZipInputStream(FileInputStream(zipFile)).use { zipIn ->
            var entry = zipIn.nextEntry
            while(entry != null) {
                val filePath = File(outputDir, entry.name)
                if(!entry.isDirectory) {
                    filePath.parentFile?.mkdirs()
                    FileOutputStream(filePath).use { out ->
                        zipIn.copyTo(out)
                    }
                } else {
                    filePath.mkdirs()
                }

                zipIn.closeEntry()
                entry = zipIn.nextEntry
            }
        }
    }

    actual suspend fun installApk() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !context.packageManager.canRequestPackageInstalls()) {
            throw Error("No install permission!")
        }

        val apkDir = File(context.cacheDir, "release")

        val fileList = apkDir.list()

        if(fileList !== null && fileList.isNotEmpty()) {
            if(fileList[0] !is String) {
                throw Error("apk file not found")
            }

            val apkFile = File(apkDir, fileList[0])

            val apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
                )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            context.startActivity(intent)
        } else {
            throw Error("apk file not found")
        }
    }
}