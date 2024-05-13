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
    val data: PostObject
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
    val user: CommentUser
)