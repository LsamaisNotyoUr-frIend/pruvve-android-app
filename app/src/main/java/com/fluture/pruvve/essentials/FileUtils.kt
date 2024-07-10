package com.fluture.pruvve.essentials

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object FileUtils {

    fun downloadFile(context: Context, fileUrl: String, fileName: String): String? {
        val file = File(context.getExternalFilesDir(null), fileName)
        if (file.exists()) {
            return file.absolutePath
        }
        return try {
            val url = URL(fileUrl)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.connect()

            val inputStream: InputStream = connection.inputStream
            val outputStream = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var length: Int

            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.close()
            inputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun deleteAllFiles(context: Context) {
        val directory = context.getExternalFilesDir(null)
        directory?.listFiles()?.forEach { it.delete() }
    }
}
