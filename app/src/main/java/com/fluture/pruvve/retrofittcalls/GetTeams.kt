package com.fluture.pruvve.retrofittcalls

data class GetTeams(
    val code: Int,
    val message: String,
    val data: GetTeamsItem
)
data class GetTeamsItem(
    val list: List<TeamsItems>,
    val page: Int,
    val limit: Int,
    val total: Int
)
data class TeamsItems(
    val id: Int,
    val teamMembers: List<CommentUser>,
    val name: String,
    val invitationLink: String,
    val profilePictureUrl: String,
    val coach: CoachUser
)
data class CoachUser(
    val firstName:String,
    val lastName:String,
    val username:String,
    val id: Int,
    val profilePictureUrl: String
)
data class GetSpecificTeam(
    val code: Int,
    val message: String,
    val data: GetTeamItem
)
data class GetTeamItem(
    val id: Int,
    val teamMembers: List<teamMembers>,
    val name: String,
    val invitationLink: String,
    val profilePictureUrl: String,
    val coach: CoachUser
)