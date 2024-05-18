package com.fluture.pruvve.retrofittcalls


import com.fluture.pruvve.CoachProfileBody
import com.fluture.pruvve.adapters.GetUserResponse
import com.fluture.pruvve.adapters.LoginInfo
import com.fluture.pruvve.adapters.LoginResponse
import com.fluture.pruvve.ProfileBody
import com.fluture.pruvve.ProfileResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface UserService {
    @POST("v1/user")
    fun createUser(@Body user: User): Call<User>
    @POST("v1/auth/login")
    fun getUser(@Body userToLogin: LoginInfo): Call<LoginResponse>
    @PUT("v1/user/account-type")
    fun userAccountType(accountType: AccountType): Call<AccountTypeResponse>
    @POST("v1/s3")
    fun uploadPicture(@Body uploadedImage: UploadImage):Call<UploadResponse>
    @PUT("v1/user/media")
    fun uploadData(@Body dataToUpload : UploadData):Call<UploadResponse>
    @PUT("v1/user/athlete/profile")
    fun finishAthleteProfile(@Body profile: ProfileBody):Call<ProfileResponse>
    @PUT("v1/user/coach/profile")
    fun finishCoachProfile(@Body profile: CoachProfileBody):Call<ProfileResponse>
    @GET("v1/user")
    fun getUserCredentials():Call<GetUserResponse>
    @PUT
    fun uploadFile(@Header("Content-Type") contentType: String, @Url uploadUrl: String, @Body file: RequestBody): Call<ResponseBody>
    @GET("oauth2/authorization/google")
    fun googleLogin(@Query("access_token") accessToken: String):Call<ResponseBody>
    @GET("oauth2/authorization/facebook")
    fun facebookLogin(@Query("access_token") accessToken: String):Call<ResponseBody>
    @POST("v1/post")
    fun makePost(@Body post: PostsMedia):Call<UploadResponse>
    @GET("v1/post")
    fun getPosts(@Query("request") post: GetPostsMedia):Call<GetPost>
    @GET("v1/post/{postId}/comment")
    fun getComments(@Path("postId")postId:Int, @Query("request") requestObject: RequestObjects):Call<ServerComments>
    @POST("v1/user/{userId}/follow")
    fun followUser(userId: FollowsAndUnfollows):Call<FollowsReply>
    @POST("v1/user/{userId}/unfollow")
    fun unFollowUser(userId: FollowsAndUnfollows):Call<FollowsReply>

    @GET("v1/user/follow-status")
    fun getFollowStatus(@Query("followerId")followerId: FollowerId, @Query("followedId")followedId: FollowedId):Call<FollowStatusReply>

    @POST("v1/story")
    fun postStory(@Body post: PostsMedia):Call<UploadResponse>
    @GET("v1/story")
    fun getStories(@Query("request") post: GetPostsMedia):Call<GetPost>
}