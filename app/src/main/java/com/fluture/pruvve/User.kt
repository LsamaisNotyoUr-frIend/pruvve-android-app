package com.fluture.pruvve
data class User(
    val firstName:String,
    val lastName:String,
    val email:String,
    val zipCode:String,
    val gender:String,
    val dateOfBirth:String,
    val username:String,
    val password:String,
)

data class UserToGet(
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