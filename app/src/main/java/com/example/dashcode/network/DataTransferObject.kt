package com.example.dashcode.network

import com.example.dashcode.database.DatabaseAccountContests
import com.example.dashcode.database.DatabaseContest
import com.example.dashcode.database.DatabaseAccount
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

fun NetworkCListContainer.asDatabaseAccountModel(): Array<DatabaseContest> =
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

fun NetworkCodeForcesContainer.asDatabaseAccountModel(): DatabaseAccount {
    return DatabaseAccount(
        handle = this.ratings[0].handle,
        platform = "codeforces.com"
    )
}
fun NetworkCodeForcesContainer.asDatabaseAccountContestsModel(accountId: Int): Array<DatabaseAccountContests> =
    ratings.map { networkContest ->
        return@map DatabaseAccountContests(
            accountId = accountId,
            contestId = networkContest.contestId.toString(),
            name = networkContest.contestName,
            updateTime = networkContest.ratingUpdateTimeSeconds,
            rank = networkContest.rank,
            newRating = networkContest.newRating,
            oldRating = networkContest.oldRating
        )
    }.toTypedArray()

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

fun NetworkCodeChefContainer.asDatabaseAccountModel(): DatabaseAccount = DatabaseAccount(
        handle = this.detailsNetwork.username,
        platform = "codechef.com"
    )

fun NetworkCodeChefContainer.asDatabaseAccountContestsModel(accountId: Int): Array<DatabaseAccountContests> {
    var prevRating = 0
    return ratings.map { networkContest ->
        val contest = DatabaseAccountContests(
            accountId = accountId,
            contestId = networkContest.code,
            name = networkContest.name,
            updateTime = timeToSeconds(networkContest.updateTime),
            rank = networkContest.rank,
            newRating = networkContest.rating,
            oldRating = prevRating
        )
        prevRating = networkContest.rating
        return@map contest
    }.toTypedArray()
}



// For cList accounts (leetcode and atcoder)

@JsonClass(generateAdapter = true)
data class NetworkClistAccountContainer(
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

fun NetworkClistAccountContainer.asDomainModel(): UserDetails =
    UserDetails(
        handle = this.accountDetail[0].handle,
        name = this.accountDetail[0].name,
        clistId = this.accountDetail[0].accountId,
        platform = this.accountDetail[0].platform
    )

fun NetworkClistAccountContainer.asDatabaseAccountModel(): DatabaseAccount =
    DatabaseAccount(
        handle = this.accountDetail[0].handle,
        platform = this.accountDetail[0].platform
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
    @Json(name="new_rating")val newRating: Int?,
    @Json(name="rating_change")val ratingChange: Int?
)

fun NetworkUserContestsContainer.asDatabaseAccountContestsModel(accountId: Int): Array<DatabaseAccountContests> {
    val contests = mutableListOf<DatabaseAccountContests>()

    var oldRating = 0
    for (networkContest in this.userContests) {

        if (networkContest.newRating == null) {
            continue
        }

        val contest = DatabaseAccountContests(
            accountId = accountId,
            contestId = networkContest.contestId.toString(),
            name = networkContest.name,
            rank = networkContest.rank ?: 0,
            updateTime = timeToSeconds(networkContest.date),
            newRating = networkContest.newRating,
            oldRating = oldRating
        )
        oldRating = networkContest.newRating
        contests.add(contest)
    }

    return contests.toTypedArray()
}