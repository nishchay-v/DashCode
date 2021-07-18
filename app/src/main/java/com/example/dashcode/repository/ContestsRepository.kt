package com.example.dashcode.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.dashcode.database.AppDatabase
import com.example.dashcode.database.asDomainModel
import com.example.dashcode.domain.CListContest
import com.example.dashcode.network.Network
import com.example.dashcode.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class ContestsRepository(private val database: AppDatabase) {
    val contests: LiveData<List<CListContest>> =
        Transformations.map(database.upcomingContestsDao.getContests()) { it ->
            it?.map {
                it.asDomainModel()
            }
        }

    suspend fun refreshUpcomingContests() {
        val timeNow = LocalDateTime.now().toString()
        withContext(Dispatchers.IO) {
            try {
                val upcomingContests = Network.contestService.getContestsListAsync(20, startTime = timeNow).await()
                database.upcomingContestsDao.insertAll(*upcomingContests.asDatabaseModel())
                Log.i("ContestsRepository", "Contests Added to Database")
            }
            catch (e: Exception) {
                //TODO: display error
                Log.i("ContestsRepository", "$e")
            }
        }
    }
}