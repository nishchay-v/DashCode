package com.example.dashcode.network

import com.example.dashcode.database.DatabaseContest
import com.example.dashcode.database.DatabasePlatformUser
import com.example.dashcode.domain.CListContest
import com.example.dashcode.domain.PlatformUser
import com.example.dashcode.domain.UserContest
import com.example.dashcode.domain.UserDetails
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.example.dashcode.util.timeToSeconds


// For upcoming contests list

@JsonClass(generateAdapter = true)
data class NetworkCListContainer (@Json(name="objects")val contestsList: List<NetworkContest>)

@JsonClass(generateAdapter = true)
data class NetworkContest(
    val id: Int,
    @Json(name="event")val name: String,
    val duration: Int,
    val start: String,
    val end: String,
    @Json(name="resource")val platform: String,
    val href: String
)

//fun NetworkCListContainer.asDomainModel(): List<CListContest> =
//    this.contestsList.map {
//        CListContest(
//            duration = it.duration,
//            end = it.end,
//            name = it.name,
//            href = it.href,
//            id = it.id,
//            start = it.start,
//            platform = it.platform
//        )
//    }

fun NetworkCListContainer.asDatabaseModel(): Array<DatabaseContest> =
    this.contestsList.map {
        DatabaseContest(
            duration = it.duration,
            end = it.end,
            name = it.name,
            href = it.href,
            id = it.id,
            start = it.start,
            platform = it.platform
        )
    }.toTypedArray()


// For CodeForces User

@JsonClass(generateAdapter = true)
data class NetworkCodeForcesContainer(
    val status: String,
    @Json(name="result")val ratings: List<NetworkCodeForcesRatings>
)

@JsonClass(generateAdapter = true)
data class NetworkCodeForcesRatings(
    val contestId: Int,
    val contestName: String,
    val handle: String,
    val rank: Int,
    val ratingUpdateTimeSeconds: Int,
    val oldRating: Int,
    val newRating: Int
)

//fun NetworkCodeForcesContainer.asDomainModel(): PlatformUser {
//    //TODO: this will crash for user with no contests
//    return PlatformUser(
//        handle = this.ratings[0].handle,
//        platform = "codeforces.com",
//        contests = this.ratings.map {
//            UserContest(
//                id = it.contestId.toString(),
//                name = it.contestName,
//                updateTime = it.ratingUpdateTimeSeconds,
//                rank = it.rank,
//                newRating = it.newRating,
//                ratingChange = if(it.newRating - it.oldRating >= 0) "+${it.newRating - it.oldRating}"
//                else (it.newRating - it.oldRating).toString()
//            )
//        }
//    )
//}

fun NetworkCodeForcesContainer.asDatabaseModel(): DatabasePlatformUser {
    return DatabasePlatformUser(
        handle = this.ratings[0].handle,
        platform = "codeforces.com",
        contests = this.ratings.map {
            UserContest(
                id = it.contestId.toString(),
                name = it.contestName,
                updateTime = it.ratingUpdateTimeSeconds,
                rank = it.rank,
                newRating = it.newRating,
                ratingChange = if(it.newRating - it.oldRating >= 0) "+${it.newRating - it.oldRating}"
                else (it.newRating - it.oldRating).toString()
            )
        }
    )
}


// For CodeChef User

@JsonClass(generateAdapter = true)
data class NetworkCodeChefContainer(
    val status: String,
    val rating: Int,
    @Json(name = "user_details")val detailsNetwork: NetworkCodeChefUserDetails,
    @Json(name = "contest_ratings")val ratings: List<NetworkCodeChefRatings>
)

@JsonClass(generateAdapter = true)
data class NetworkCodeChefRatings(
    val code: String,
    val rating: Int,
    val rank: Int,
    val name: String,
    val getyear: Int,
    val getmonth : Int,
    val getday: Int,
    @Json(name = "end_date")val updateTime: String
)

@JsonClass(generateAdapter = true)
data class NetworkCodeChefUserDetails(
    val name: String,
    val username: String,
    val country: String, val state: String,
    val city: String,
    @Json(name = "student/professional")val pro: String,
    val institution: String
)

//fun NetworkCodeChefContainer.asDomainModel(): PlatformUser {
//    var oldRating = 0
//    return PlatformUser(
//        handle = this.detailsNetwork.username,
//        platform = "codechef.com",
//        contests = this.ratings.map {
//            val contest = UserContest(
//                id = it.code,
//                name = it.name,
//                updateTime = timeToSeconds(it.updateTime),
//                rank = it.rank,
//                newRating = it.rating,
//                ratingChange = if(it.rating - oldRating >= 0) "+${it.rating - oldRating}"
//                else (it.rating - oldRating).toString()
//            )
//            oldRating = it.rating
//            return@map contest
//        }
//    )
//}

fun NetworkCodeChefContainer.asDatabaseModel(): DatabasePlatformUser {
    var oldRating = 0
    return DatabasePlatformUser(
        handle = this.detailsNetwork.username,
        platform = "codechef.com",
        contests = this.ratings.map {
            val contest = UserContest(
                id = it.code,
                name = it.name,
                updateTime = timeToSeconds(it.updateTime),
                rank = it.rank,
                newRating = it.rating,
                ratingChange = if(it.rating - oldRating >= 0) "+${it.rating - oldRating}"
                else (it.rating - oldRating).toString()
            )
            oldRating = it.rating
            return@map contest
        }
    )
}


// For cList accounts (leetcode and atcoder)

@JsonClass(generateAdapter = true)
data class NetworkAccountDetailContainer(
    @Json(name = "objects")val accountDetail: List<NetworkAccountDetail>
)

@JsonClass(generateAdapter = true)
data class NetworkAccountDetail(
    val handle : String,
    @Json(name = "id")val accountId: Int,
    val name: String,
    val rating: Int,
    @Json(name = "resource")val platform: String,
)

fun NetworkAccountDetailContainer.asDomainModel(): UserDetails =
    UserDetails(
        handle = this.accountDetail[0].handle,
        name = this.accountDetail[0].name,
        accountId = this.accountDetail[0].accountId,
        platform = this.accountDetail[0].platform
    )

fun NetworkAccountDetailContainer.withContestsAsDatabaseModel(contests: List<UserContest>): DatabasePlatformUser =
    DatabasePlatformUser(
        handle = this.accountDetail[0].handle,
        platform = this.accountDetail[0].platform,
        contests = contests
    )

    // For user's contests

@JsonClass(generateAdapter = true)
data class NetworkUserContestsContainer(
    @Json(name="objects")val userContests: List<NetworkUserContests>
)

@JsonClass(generateAdapter = true)
data class NetworkUserContests(
    @Json(name="contest_id")val contestId: Int,
    @Json(name="event")val name: String,
    @Json(name="place")val rank: Int?,
    val date: String,
    @Json(name="old_rating")val oldRating: Int?,
    @Json(name="new_rating")val newRating: Int?,
    @Json(name="rating_change")val ratingChange: Int?
)

fun NetworkUserContestsContainer.asDomainModel(): List<UserContest> {
    val contests = mutableListOf<UserContest>()
    for (item in this.userContests) {

        if (item.newRating == null) {
            continue
        }

        contests.add(
            UserContest(
                id = item.contestId.toString(),
                name = item.name,
                rank = item.rank ?: 0,
                newRating = item.newRating,
                ratingChange = item.ratingChange?.toString() ?: item.newRating.toString(),
                updateTime = timeToSeconds(item.date)
            )
        )

    }
    return contests
}

