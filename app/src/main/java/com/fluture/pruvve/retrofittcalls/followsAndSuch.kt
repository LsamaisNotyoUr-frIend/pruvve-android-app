package com.fluture.pruvve.retrofittcalls

data class FollowsAndUnfollows(
    val userId: Int
)
data class FollowsReply(
    val code: Int,
    val message: String
)
data class FollowerId(
    val followerId: Int
)

data class FollowedId(
    val followedId: Int
)

data class FollowStatusReply(
    val code: Int,
    val message: String,
    val data: Boolean
)