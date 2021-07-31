package com.example.dashcode.database

import androidx.room.*
import com.example.dashcode.domain.CListContest
import com.example.dashcode.domain.PlatformUser
import com.example.dashcode.domain.UserContest

@Entity
data class DatabaseAccount constructor(
    @PrimaryKey(autoGenerate = true)
    val accountId: Int = 0,
    val handle: String,
    val platform: String,
)

@Entity(primaryKeys = ["accountId", "contestId"])
data class DatabaseAccountContests constructor(
    val accountId: Int,
    val contestId: String,
    val name: String,
    val updateTime: Int,
    val rank: Int,
    val oldRating: Int,
    val newRating: Int
)

data class AccountWithContests(
    @Embedded val account: DatabaseAccount,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "accountId"
    )
    val contests: List<DatabaseAccountContests>
)

@Entity
data class DatabaseContest constructor(
    @PrimaryKey
    val id: Int,
    val duration: Int,
    val start: String,
    val end: String,
    val name: String,
    val href: String,
    val platform: String,
)

fun DatabaseAccountContests.asDomainModel(): UserContest {
    val rc = this.newRating - this.oldRating
    return UserContest(
        name = this.name,
        updateTime = this.updateTime,
        rank = this.rank,
        newRating = this.newRating,
        ratingChange = if (rc > 0) "+$rc" else rc.toString()
    )
}

fun AccountWithContests.asDomainModel() : PlatformUser {
    val rc = if (contests.isNotEmpty()) {
        (contests.last().newRating - contests.last().oldRating)
    } else 0
    val ratingChangeText = if (rc > 0) "+$rc" else rc.toString()
    return PlatformUser(
        accountId = this.account.accountId,
        handle = this.account.handle,
        platform = this.account.platform,
        currentRating = if (contests.isNotEmpty()) {contests.last().newRating} else 0,
        lastRatingChange = ratingChangeText,
        contests = this.contests.map{
            it.asDomainModel()
        }.sortedBy { it.updateTime }
    )
}

fun DatabaseContest.asDomainModel() : CListContest =
    CListContest(
        id = this.id,
        duration = this.duration,
        start = this.start,
        end = this.end,
        name = this.name,
        platform = this.platform,
        href = this.href
    )