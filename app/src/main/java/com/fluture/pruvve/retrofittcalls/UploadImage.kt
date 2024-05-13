package com.fluture.pruvve.retrofittcalls

import com.google.gson.annotations.SerializedName

class UploadImage(
    @SerializedName("filename")
    val fileName: String,
    val purpose: String
)