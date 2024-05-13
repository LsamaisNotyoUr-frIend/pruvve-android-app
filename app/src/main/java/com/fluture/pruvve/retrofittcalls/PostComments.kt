package com.fluture.pruvve.retrofittcalls

data class RequestObjects(
    val page: Int,
    val size: Int
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
    val user: CommentUser
)