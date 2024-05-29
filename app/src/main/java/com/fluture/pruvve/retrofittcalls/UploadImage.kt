package com.fluture.pruvve.retrofittcalls

data  class UploadImage(
    val fileName: String,
    val purpose: String
)

data class PostIdObject(
    val postId: Int
)

data class UserIdObject(
    val userId: Int
)