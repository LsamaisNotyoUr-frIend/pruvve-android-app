package com.fluture.pruvve.retrofittcalls

data class PostsMedia(
    val mediaUrl: String,
    val mediaType: String,
    val caption: String
)
data class GetPostsMedia(
    val page: Int,
    val size: Int,
    val userId: Int
)
data class GetPost(
    val code: Int,
    val message: String,
    val data: PostObject,
)
data class PostObject(
    val list: List<PostItems>,
    val page: Int,
    val limit: Int,
    val total: Int
)

data class PostItems(
    val id: Int,
    val caption: String,
    val mediaUrl: String,
    val mediaType: String,
    val user: CommentUser,
    val creationDate: String,
    val summary: PostsSummary ?= PostsSummary(
        commentCount = 0,
        likeCount = 0,
        viewCount = 0
    )
)
data class PostsSummary(
    val commentCount: Int? = 0,
    val likeCount: Int? = 0,
    val viewCount: Int? = 0
)

data class GetAthletePost(
    val code: Int,
    val message: String,
    val data: AthleteProfile,
)
data class AthleteProfile(
    val position: String,
    val height: String,
    val preferredFoot: String,
    val bio: String
)