package com.fluture.pruvve

import com.google.gson.annotations.SerializedName

class UploadImage(
    @SerializedName("filename")
    val filenameWithFileData: String,
    val purpose: String
)