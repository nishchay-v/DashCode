package com.example.dashcode.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.dashcode.domain.CListContest
import com.example.dashcode.domain.PlatformUser
import com.example.dashcode.domain.UserContest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity
@TypeConverters(GsonTypeConverters::class)
data class DatabasePlatformUser constructor(
    @PrimaryKey
    val handle: String,
    val platform: String,
    val contests: List<UserContest>
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

// Since we can't directly store list data type in Database, we need to convert them into list
class GsonTypeConverters {
    private val gson = Gson()

    @TypeConverter
    fun stringToContestsList(data: String?): List<UserContest>? {
        if (data == null)
            return listOf<UserContest>()

        val type = object :TypeToken<List<UserContest>>(){}.type

        return gson.fromJson<List<UserContest>>(data, type)
    }

    @TypeConverter
    fun contestsListToString(list: List<UserContest>) : String {
        return gson.toJson(list)
    }
}

fun DatabasePlatformUser.asDomainModel() : PlatformUser =
    PlatformUser(
        handle = this.handle,
        platform = this.platform,
        rating = this.contests.last().newRating,
        ratingChange = this.contests.last().ratingChange,
        contests = this.contests
    )


fun DatabaseContest.asDomainModel() : CListContest =
    CListContest (
        id = this.id,
        duration = this.duration,
        start = this.start,
        end = this.end,
        name = this.name,
        platform = this.platform,
        href = this.href
        )