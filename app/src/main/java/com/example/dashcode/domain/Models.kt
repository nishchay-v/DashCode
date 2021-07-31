package com.example.dashcode.domain

data class UserContest(
    val name: String,
    val updateTime: Int,
    val rank: Int,
    val newRating: Int,
    val ratingChange: String
)

data class MarkerData(
    val contestName: String,
    val rank: Int,
    val rating: Int,
    val ratingChange: String
)

data class CListContest(
    val id: Int,
    val name: String,
    val duration: Int,
    val start: String,
    val end: String,
    val href: String,
    val platform: String
)

data class UserDetails(
    val handle: String,
    val name: String,
    val clistId: Int,
    val platform: String
)

data class PlatformUser(
    val accountId: Int,
    val handle: String,
    val platform: String,
    val contests: List<UserContest>,
    val currentRating: Int,
    val lastRatingChange: String
)