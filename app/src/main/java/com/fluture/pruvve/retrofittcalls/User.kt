package com.fluture.pruvve.retrofittcalls


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
    val id:Int,
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

data class CommentUser(
    val firstName:String,
    val lastName:String,
    val username:String,
    val id: Int,
    val profilePictureUrl: String
)

data class teamMembers(
    val firstName:String,
    val lastName:String,
    val username:String,
    val id: Int,
    val profilePictureUrl: String,
    val preferredFoot: String,
    val position: String
)