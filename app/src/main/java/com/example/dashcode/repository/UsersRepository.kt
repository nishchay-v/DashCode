package com.example.dashcode.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    //LiveData to indicate if a user account is found or not
    private val _foundUser = MutableLiveData<Boolean>()
    val foundUser: LiveData<Boolean>
        get()=_foundUser
    fun onAddUserComplete() {
        _foundUser.value = null
    }

// Functions to invoke fetching of user data and add the data to the Room database

    suspend fun addCodeForcesUser (handle: String)  {
        try {
            withContext(Dispatchers.IO) {
                val userRatings = Network.codeforces.getRatingsAsync(handle).await()
                database.userRatingsDao.insertAll(userRatings.asDatabaseModel())
            }
            _foundUser.value = true
        }
        catch (e: Exception) {
            Log.i("UserRepository", "User not found or $e")
            _foundUser.value = false
        }
    }

    suspend fun addCodeChefUser (handle: String) {
         try{
             withContext(Dispatchers.IO) {
                val userRating = Network.codechef.getRatingsAsync(handle).await()
                database.userRatingsDao.insertAll(userRating.asDatabaseModel())
            }
             _foundUser.value = true
        }
         catch (e: Exception) {
             Log.i("UserRepository", "User not found or $e")
             _foundUser.value = false
         }
    }

    suspend fun addPlatformUser (platform: String, handle: String) {
        try {
            withContext(Dispatchers.IO) {
                val userDetail = Network.accountService.getAccountAsync(platform, handle).await()
                val userDetailObj = userDetail.asDomainModel()
                val userContests = Network.accountContestService.getContestsAsync(userDetailObj.accountId).await()
                val userContestsObj = userContests.asDomainModel()
                database.userRatingsDao.insertAll(userDetail.withContestsAsDatabaseModel(userContestsObj))
            }
            _foundUser.value = true
        }
        catch (e: Exception) {
            Log.i("UserRepository", "User not found or $e")
            _foundUser.value = false
        }
    }

    suspend fun removeAccount (handle: String) {
        withContext(Dispatchers.IO) {
            database.userRatingsDao.deleteUser(handle)
        }
    }
}

