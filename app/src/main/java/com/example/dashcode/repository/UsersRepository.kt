package com.example.dashcode.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.dashcode.database.AppDatabase
import com.example.dashcode.database.asDomainModel
import com.example.dashcode.domain.PlatformUser
import com.example.dashcode.network.Network
import com.example.dashcode.network.asDatabaseModel
import com.example.dashcode.network.asDomainModel
import com.example.dashcode.network.withContestsAsDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsersRepository(private val database: AppDatabase) {

    // List of added users
    val users: LiveData<List<PlatformUser>?> =
        Transformations.map(database.userRatingsDao.getUsers()) {
            it?.map {
                it.asDomainModel()
            }
        }

// Functions to invoke fetching of user data and add the data to the Room database

    suspend fun addCodeForcesUser (handle: String)  {
        withContext(Dispatchers.IO) {
            try {
                val userRatings = Network.codeforces.getRatingsAsync(handle).await()
                database.userRatingsDao.insertAll(userRatings.asDatabaseModel())
                Log.i("UserRepository", "User added")
            }
            catch (e: Exception) {
                //TODO: display error when a user is not found
                Log.i("UserRepository", "User not found or $e")
            }
        }
    }

    suspend fun addCodeChefUser (handle: String) {
        withContext(Dispatchers.IO) {
            try {
                val userRating = Network.codechef.getRatingsAsync(handle).await()
                database.userRatingsDao.insertAll(userRating.asDatabaseModel())
                Log.i("UserRepository", "User added")
            }
            catch (e: Exception) {
                Log.i("UserRepository", "User not found or $e")
            }
        }
    }

    suspend fun addPlatformUser (platform: String, handle: String) {
        withContext(Dispatchers.IO) {
            try {
                val userDetail = Network.accountService.getAccountAsync(platform, handle).await()
                val userDetailObj = userDetail.asDomainModel()
                val userContests = Network.accountContestService.getContestsAsync(userDetailObj.accountId).await()
                val userContestsObj = userContests.asDomainModel()
                database.userRatingsDao.insertAll(userDetail.withContestsAsDatabaseModel(userContestsObj))
                Log.i("UserRepository", "User added")
            }
            catch (e: Exception) {
                Log.i("UserRepository", "User not found or $e")
            }
        }
    }
}

