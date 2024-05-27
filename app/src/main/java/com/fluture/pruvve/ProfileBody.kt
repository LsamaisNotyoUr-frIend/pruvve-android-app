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
    val code: String,
    val message: String
)

data class GoogleResponse(
    val email:String,
    val password:String
)