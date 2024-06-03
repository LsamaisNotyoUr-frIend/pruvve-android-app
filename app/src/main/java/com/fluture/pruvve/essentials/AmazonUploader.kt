package com.fluture.pruvve.essentials

import android.util.Log
import com.amazonaws.auth.AnonymousAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream


class AmazonUploader{
    private val s3Client: AmazonS3Client = AmazonS3Client(AnonymousAWSCredentials())

    init {
        // Initialize Amazon S3 client
        s3Client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.AF_SOUTH_1))
    }

    fun uploadImage(byteImage: ByteArray, signedUrl: String, callback: (Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                // Extract bucket name and object key from signed URL
                val urlParts = signedUrl.split('/')
                val bucketName = urlParts[3]
                val objectKey = urlParts[4]

                // Upload byte array to S3 using pre-signed URL
                val inputStream = ByteArrayInputStream(byteImage)
                val putObjectRequest = PutObjectRequest(bucketName, objectKey, inputStream, null)
                s3Client.putObject(putObjectRequest)

                Log.d("AmazonSuccess", "The image was uploaded successfully")

                callback(true)
            } catch (e: Exception) {
                Log.e("AmazonFailure", "Failed to upload the image: ${e.message}", e)
                callback(false)
            }
        }
    }
}