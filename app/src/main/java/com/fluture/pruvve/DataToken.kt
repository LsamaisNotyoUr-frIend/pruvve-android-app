package com.fluture.pruvve

import com.google.gson.annotations.SerializedName

data class DataToken (
    @SerializedName("token")
    val token: String,
    val user:UserToGet
)

data class GetData(
    val id:String,
    val firstName:String,
    val lastName:String,
    val email:String,
    val zipCode:String,
    val gender:String,
    val username:String,
    val profilePicUrl:String,
    val accountType: String,
    val dateOfBirth:String,
)