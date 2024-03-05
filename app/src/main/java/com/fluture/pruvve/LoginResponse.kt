package com.fluture.pruvve

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: DataToken,
    @SerializedName("user")
    val user: User
)