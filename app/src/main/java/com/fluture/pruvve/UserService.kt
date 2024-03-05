package com.fluture.pruvve


import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

internal interface UserService {
    @POST("v1/user")
    fun createUser(@Body user: User): Call<User>
    @POST("v1/auth/login")
    fun getUser(@Body userToLogin: LoginInfo): Call<LoginResponse>
    @PUT("v1/user/account-type")
    fun userAccountType(@Body accountType: AccountType): Call<AccountType>
    @POST("v1/s3")
    fun uploadPicture(@Body uploadedImage: UploadImage):Call<UploadResponse>
    @PUT("v1/user/media")
    fun uploadData(@Body dataToUpload : UploadData):Call<UploadResponse>
    @PUT("v1/user/athlete/profile")
    fun finishAthleteProfile(@Body profile: ProfileBody):Call<ProfileResponse>
    @PUT("v1/user/coach/profile")
    fun finishCoachProfile(@Body profile: CoachProfileBody):Call<ProfileResponse>
    @GET("oauth2/authorization/google")
    fun googleLogin(@Query("access_token") accessToken: String):Call<LoginResponse>
    @GET("oauth2/authorization/facebook")
    fun facebookLogin(@Query("access_token") accessToken: String):Call<LoginResponse>
}