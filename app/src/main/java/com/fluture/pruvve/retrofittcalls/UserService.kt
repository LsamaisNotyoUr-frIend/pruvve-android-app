package com.fluture.pruvve.retrofittcalls


import com.fluture.pruvve.CoachProfileBody
import com.fluture.pruvve.GetCategory
import com.fluture.pruvve.ProfileBody
import com.fluture.pruvve.ProfileResponse
import com.fluture.pruvve.adapters.GetAllUserResponse
import com.fluture.pruvve.adapters.GetUserResponse
import com.fluture.pruvve.adapters.LoginResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface UserService {
    @POST("v1/user")
    fun createUser(@Body user: User): Call<User>
    @POST("v1/auth/login")
    fun getUser(@Body userToLogin: LoginInfo): Call<LoginResponse>
    @POST("v1/s3")
    fun uploadPicture(@Body uploadedImage: UploadImage):Call<UploadResponse>
    @PUT("v1/user/media")
    fun uploadData(@Body dataToUpload : UploadData):Call<UploadResponse>
    @PUT("v1/user/athlete/profile")
    fun finishAthleteProfile(@Body profile: ProfileBody):Call<ProfileResponse>
    @PUT("v1/user/coach/profile")
    fun finishCoachProfile(@Body profile: CoachProfileBody):Call<ProfileResponse>

    @GET("v1/user/coach/profile")
    fun getCoachProfile(@Body profile: CoachProfileBody):Call<ProfileResponse>
    @GET("v1/user")
    fun getUserCredentials():Call<GetUserResponse>
    @GET("v1/user/search")
    fun getAllUsers(@Query("request") request: UserRequestObjects):Call<GetAllUserResponse>
    @PUT
    fun uploadFile(@Header("Content-Type") contentType: String, @Url uploadUrl: String, @Body file: RequestBody): Call<ResponseBody>
    @GET("oauth2/authorization/google")
    fun googleLogin(@Query("access_token") accessToken: String):Call<ResponseBody>
    @GET("oauth2/authorization/facebook")
    fun facebookLogin(@Query("access_token") accessToken: String):Call<ResponseBody>
    @POST("v1/post")
    fun makePost(@Body post: PostsMedia):Call<UploadResponse>
    @GET("v1/post")
    fun getUsersPosts(@Query("request") post: GetPostsMedia):Call<GetPost>

    @PUT("v1/post/{postId}/comment")
    fun makeComment(@Path("postId")postId:Int, @Body comments: MakeComments):Call<FollowsReply>
    @GET("v1/post/{postId}/comment")
    fun getComments(@Path("postId")postId:Int, @Query("request") requestObject: RequestObjects):Call<ServerComments>
    @GET("v1/post/{postId}/like")
    fun getLikes(@Path("postId")postId:Int, @Query("request") requestObject: RequestObjects):Call<ServerLikes>
    @POST("v1/post/{postId}/like")
    fun likePost(@Path("postId") postId: Int):Call<FollowsReply>
    @POST("v1/post/{postId}/view")
    fun addViews(@Path("postId") postId: Int):Call<ProfileResponse>
    @DELETE("v1/post/{postId}/unlike")
    fun unLikePost(@Path("postId") postId: Int): Call<FollowsReply>
    @POST("v1/user/{userId}/follow")
    fun followUser(@Path("userId") userId: Int):Call<FollowsReply>
    @DELETE("v1/user/{userId}/unfollow")
    fun unFollowUser(@Path("userId") userId: Int): Call<FollowsReply>

    @GET("v1/user/follow-status")
    fun getFollowStatus(@Query("followerId")followerId: FollowerId, @Query("followedId")followedId: FollowedId):Call<FollowStatusReply>

    @POST("v1/story")
    fun postStory(@Body post: PostsMedia):Call<UploadResponse>

    @GET("v1/story")
    fun getStories(@Query("request") post: GetPostsMedia):Call<GetPost>

    @PUT("v1/team/{teamId}/media")
    fun putTeamsMedia(@Path("teamId")teamId :Int, @Body teamMedia: UploadData):Call<ProfileResponse>

    @GET("v1/team")
    fun getTeams(@Query("request") requestObject: RequestObjects):Call<GetTeams>

    @GET("v1/team/{teamInvitationLink}")
    fun addToTeam(@Path("teamInvitationLink")teamInvitationLink :String):Call<ProfileResponse>

    @GET("v1/team/{teamId}")
    fun getTeamById(@Path("teamId")teamId :Int):Call<GetSpecificTeam>

    @PUT("v1/team/{teamId}/user")
    fun getUserInTeam(@Path("teamId")teamId :Int, @Body userId: UserIdObject):Call<ProfileResponse>

    @POST("v1/team")
    fun makeTeams(@Body name:String):Call<GetSpecificTeam>

    @PUT("v1/user/account-type")
    fun putAccountType(@Body accountType: AccountType):Call<AccountTypeResponse>

    @GET("v1/user/athlete/profile")
    fun getAthleteProfile():Call<GetAthleteProfile>

    @GET("v1/video/feed")
    fun getFeeds(@Query("request") post: GetFeedsMedia):Call<GetAllPosts>

    @GET("v1/post/feed")
    fun getPosts(@Query("request") post: GetFeedsMedia):Call<GetAllPosts>

    @GET("v1/user/{userId}/video")
    fun getMyPosts(@Path("userId")userId:Int, @Query("request") post: GetFeedsMedia):Call<GetAllPosts>

    @GET("v1/post/{postId}")
    fun getSelectedPosts(@Path("postId")postId:Int):Call<GetPost>
    @GET("v1/post/{postId}/summary")
    fun getPostSummary(@Path("postId")postId:Int):Call<GetPostsSummary>
    @GET("v1/video/video-of-the-week")
    fun getVideoOfTheWeek():Call<GetVideoOfWeek>
    @PUT("v1/video/video-of-the-week")
    fun putVideoOfheWeek(@Body postId: PostIdObject):Call<ProfileResponse>
    @GET("v1/video/category")
    fun getVideoCategory(@Query("request") requestObject: RequestObjects):Call<VideoCategory>
    @POST("v1/video/category")
    fun createVideoCategory(@Body videoCategory: VideoCategoryCreator):Call<ProfileResponse>
    @GET("v1/video/category/{categoryId}")
    fun getCategory(@Path("categoryId") categoryId: Int):Call<GetCategory>
    @GET("v1/video/by-category/{categoryId}")
    fun getVideosInCategory(@Path("categoryId") categoryId: Int, @Query("request") requestObject: RequestObjects):Call<GetAllPosts>

    @GET("v1/news")
    fun getNews(@Query("request") requestObject: RequestObjects):Call<GetNews>

    @POST("v1/news")
    fun shareNews(@Body newsItems: News):Call<ProfileResponse>

    @GET("v1/news")
    fun getNewsById(@Path("newsId") newsId: Int):Call<NewsGotten>

    @GET("v1/pitch/{pitchId}")
    fun getPitchById(@Path("pitchId") pitchId: Int):Call<GetPitch>

    @GET("v1/pitch")
    fun getPitches(@Query("request") requestObject: RequestObjects):Call<GetPitches>

    @GET("v1/pitch/{pitchId}/book")
    fun bookPitch(@Path("pitchId") pitchId: Int, @Query("request") requestObject: PitchRequestObjects)

    @GET("v1/pitch/{pitchId}/availability")
    fun checkAvailability(@Path("pitchId") pitchId: Int, @Query("date") data: String):Call<GetPitchAvailability>

    @GET("v1/pitch/book/{bookingReference}/complete")
    fun completeBooking(@Path("bookingReference") bookingReference: String):Call<ProfileResponse>
}