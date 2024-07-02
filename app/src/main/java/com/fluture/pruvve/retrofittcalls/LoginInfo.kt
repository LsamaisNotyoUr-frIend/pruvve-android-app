package com.fluture.pruvve.retrofittcalls

data class LoginInfo(
    val username: String,
    val password: String
)

data class Follow(
    val code: Int,
    val message: String,
    val data: FollowSummary
)

data class FollowSummary(
    val following: Int,
    val followers: Int
)