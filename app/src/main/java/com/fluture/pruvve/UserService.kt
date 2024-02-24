package com.fluture.pruvve


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

internal interface UserService {
    @POST("v1/user")
    fun createUser(@Body user: User): Call<User>
    @POST("v1/auth/login")
    fun getUser(@Body userToLogin: LoginInfo): Call<User>
}