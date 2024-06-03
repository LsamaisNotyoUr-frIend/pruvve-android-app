package com.fluture.pruvve

data class ProfileBody(
    val position: String,
    val height: String,
    val preferredFoot: String,
    val bio: String
)

data class CoachProfileBody(
    val schoolOrTeam: String,
    val bio: String
)

data class ProfileResponse(
    val code: Int,
    val message: String
)

data class teamResponse(
    val code: Int,
    val message: String,
    val teamId: Int
)

data class GoogleResponse(
    val email:String,
    val password:String
)

data class GetCategory(
    val code: Int,
    val message: String,
    val data: CategoryItems
)
data class CategoryItems(
    val id: Int,
    val category: String,
    val mediaUrl: String
)