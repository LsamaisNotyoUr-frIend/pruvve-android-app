package com.fluture.pruvve

class ProfileBody(
    val position: String,
    val height: String,
    val preferredFoot: String,
    val bio: String
)

class CoachProfileBody(
    val schoolOrTeam: String,
    val bio: String
)

class ProfileResponse(
    code: String,
    message: String
)

class GoogleResponse(
    val email:String,
    val password:String
)