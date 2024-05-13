package com.fluture.pruvve.essentials
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ImageUploader {

    fun uploadImage(imageBytes: ByteArray, url: String) {
        val client = OkHttpClient()

        val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("RetrofitUploadFailure", e.printStackTrace().toString()) }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    Log.d("RetrofitUpload", "image has been uploaded successfully")
                } else {
                     Log.e("RetrofitUpload", "Upload failed: ${response.code}")
                }
            }
        })
    }
}

class VideoUploader {
    fun uploadVideo(imageBytes: ByteArray, url: String) {
        val client = OkHttpClient()

        val requestBody = imageBytes.toRequestBody("video/mp4".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .put(requestBody)
            .build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e("RetrofitUploadFailure", e.printStackTrace().toString()) }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    Log.d("RetrofitUpload", "Video has been uploaded successfully")
                } else {
                    Log.e("RetrofitUpload", "Upload failed: ${response.code}")
                }
            }
        })
    }
}