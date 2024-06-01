package com.fluture.pruvve.retrofittcalls

data class RequestObjects(
    val page: Int,
    val size: Int
)

data class PitchRequestObjects(
    val pitchTimes: List<PitchTimes>,
    val bookingReference: String
)

data class PitchTimes(
    val startTime: String,
    val endTime: String
)

data class ServerComments (
    val code: Int,
    val message: String,
    val data: CommentList
)

data class CommentList(
    val list: List<CommentItems>,
    val page: Int,
    val limit: Int,
    val total: Int
)

data class CommentItems(
    val id: Int,
    val comment: String,
    val user: CommentUser,
)
data class MakeComments(
    val comment: String
)

data class ServerLikes (
    val code: Int,
    val message: String,
    val data: LikesList
)

data class LikesList(
    val list: List<LikesItems>,
    val page: Int,
    val limit: Int,
    val total: Int
)

data class LikesItems(
    val id: Int,
    val postId: Int,
    val user: CommentUser
)



