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
    val data: PostObject,
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
    val user: CommentUser,
    val creationDate: String,
    val summary: PostsSummary ?= PostsSummary(
        commentCount = 0,
        likeCount = 0,
        viewCount = 0
    )
)
data class PostsSummary(
    val commentCount: Int? = 0,
    val likeCount: Int? = 0,
    val viewCount: Int? = 0
)

data class GetPostsSummary(
    val code: Int,
    val message: String,
    val data: PostsSummary ?= PostsSummary(
        commentCount = 0,
        likeCount = 0,
        viewCount = 0
    )
)

data class GetAthletePost(
    val code: Int,
    val message: String,
    val data: AthleteProfile,
)
data class AthleteProfile(
    val position: String,
    val height: String,
    val preferredFoot: String,
    val bio: String
)

data class GetAllPosts(
    val code: Int,
    val message: String,
    val data: AllPostObject
)
data class AllPostObject(
    val list: List<AllPostItems>,
    val page: Int,
    val limit: Int,
    val total: Int
)
data class AllPostItems(
    val id: Int,
    val caption: String,
    val mediaUrl: String,
    val user: CommentUser,
    val creationDate: String
)

data class GetVideoOfWeek(
    val code: Int,
    val message: String,
    val data: VideoOfTheWeekObject
)
data class VideoOfTheWeekObject(
    val id: Int,
    val caption: String,
    val mediaUrl: String,
    val user: CommentUser,
    val creationDate: String
)

data class VideoCategory(
    val code: Int,
    val message: String,
    val data: VideoCategoryItem
)
data class VideoCategoryItem(
    val list: List<VideoCategoryObject>,
    val page: Int,
    val limit: Int,
    val total: Int
)
data class VideoCategoryObject(
    val id: Int,
    val category: String,
    val mediaUrl: String
)

data class VideoCategoryCreator(
    val caption: String,
    val mediaUrl: String
)

data class News(
    val mediaUrl: String,
    val mediaType: String,
    val caption: String
)
data class NewsGotten(
    val code: Int,
    val message: String,
    val data: NewsListItems
)

data class GetNews(
    val code: Int,
    val message: String,
    val data: GetNewsList
)

data class GetNewsList(
    val list: List<NewsListItems>,
    val page: Int,
    val limit: Int,
    val total: Int
)

data class NewsListItems(
    val id: Int,
    val title: String,
    val body: String,
    val time: String
)

data class GetPitches(
    val code: Int,
    val message: String,
    val data: GetPitchList
)
data class GetPitchList(
    val list: List<GetPitchItems>,
    val page: Int,
    val limit: Int,
    val total: Int
)

data class GetPitch(
    val code: Int,
    val message: String,
    val data: GetPitchItems?= GetPitchItems(
        1,
        "empty",
        "empty",
        "empty",
        "empty",
        "empty",
        1,
        1,
        true,
        true,
        true,
        "empty",
        true,
        1,
        1
    )
)

data class GetPitchItems(
    val id:Int,
    val imageUrl:String,
    val title: String,
    val address: String,
    val format: String,
    val surface: String,
    val rating: Int,
    val price: Int,
    val hasChangingRoom : Boolean,
    val hasFreeParking: Boolean,
    val hasFloodLights: Boolean,
    val description: String,
    val hasPruvveCamera: Boolean,
    val longitude: Int,
    val latitude: Int
)

data class GetPitchAvailability(
    val code: Int,
    val message: String,
    val data: PitchTimeItems
)
data class PitchTimeItems(
    val id: Int,
    val slots: List<TimeSlothsItems>
)

data class TimeSlothsItems(
    val slotsLeft: Int,
    val startTime: String,
    val endTime: String
)