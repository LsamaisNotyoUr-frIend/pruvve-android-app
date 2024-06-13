package com.fluture.pruvve.adapters

import com.fluture.pruvve.retrofittcalls.DataToken
import com.fluture.pruvve.retrofittcalls.GetData
import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: DataToken,
)

data class GetUserResponse (
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: GetData,
)

data class GetAllUserResponse(
    val code: String,
    val message: String,
    val data: GetAllUsers
)
data class GetAllUsers(
    val list: List<GetData>,
    val page: Int,
    val limit: Int,
    val total: Int
)