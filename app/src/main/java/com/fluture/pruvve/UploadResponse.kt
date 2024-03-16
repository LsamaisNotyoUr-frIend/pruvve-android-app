package com.fluture.pruvve

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("code")
    val code:String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val url: String
)