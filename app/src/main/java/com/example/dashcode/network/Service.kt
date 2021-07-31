package com.example.dashcode.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

const val apiUser = "neonv"
const val apiKey = "ce035e53c8d8ccb0a90744a3fb41237b415e8e16"
val platforms = "codechef.com,codeforces.com,leetcode.com,topcoder.com,atcoder.jp,codingcompetitions.withgoogle.com,hackerearth.com,kaggle.com,spoj.com"

interface CodeForcesService {
    @GET("user.rating")
    fun getAccountWithContestsAsync(@Query("handle") username: String): Deferred<NetworkCodeForcesContainer>
}

interface CodeChefService {
    @GET()
    fun getAccountWithContestsAsync(@Url username: String): Deferred<NetworkCodeChefContainer>
}

interface CListContestService {
    @GET(".")
    fun getContestsListAsync(
        @Query("limit") limit: Int,
        @Query("resource") resources: String = platforms,
        @Query("start__gt") startTime: String,
        @Query("order_by") order: String = "start",
        @Query("username") username: String = apiUser,
        @Query("api_key") key: String = apiKey
        ) : Deferred<NetworkCListContainer>
}

interface ClistAccountService {
    @GET(".")
    fun getAccountAsync(
        @Query("resource") resource: String,
        @Query("handle") handle: String,
        @Query("username") username: String = apiUser,
        @Query("api_key") key: String = apiKey
    ): Deferred<NetworkClistAccountContainer>
}

interface ClistAccountContestService {
    @GET(".")
    fun getContestsAsync (
        @Query("account_id") accountId: Int,
        @Query("username") username: String = apiUser,
        @Query("api_key") key: String = apiKey
    ) : Deferred<NetworkUserContestsContainer>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


object Network {
    private val retrofitCodeForcesService = Retrofit.Builder()
        .baseUrl("https://codeforces.com/api/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val retrofitCodeChefService = Retrofit.Builder()
        .baseUrl("https://competitive-coding-api.herokuapp.com/api/codechef/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val retrofitCListContests = Retrofit.Builder()
        .baseUrl("https://clist.by:443/api/v2/contest/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val retrofitCListAccount = Retrofit.Builder()
        .baseUrl("https://clist.by:443/api/v2/account/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    private val retrofitCListAccountContests = Retrofit.Builder()
        .baseUrl("https://clist.by:443/api/v2/statistics/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val codeforces: CodeForcesService = retrofitCodeForcesService.create(CodeForcesService::class.java)

    val codechef: CodeChefService = retrofitCodeChefService.create(CodeChefService::class.java)

    val contestService: CListContestService = retrofitCListContests.create(CListContestService::class.java)

    val accountService: ClistAccountService = retrofitCListAccount.create(ClistAccountService::class.java)

    val accountContestService: ClistAccountContestService = retrofitCListAccountContests.create(ClistAccountContestService::class.java)
}