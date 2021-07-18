package com.example.dashcode.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserRatingsDao {
    @Query("select * from DatabasePlatformUser")
    fun getUsers() : LiveData<List<DatabasePlatformUser>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg databaseUsers: DatabasePlatformUser)

    @Query("delete from databaseplatformuser where handle = :handle")
    fun deleteUser(handle: String)

    @Query("delete from DatabasePlatformUser")
    fun nukeTable()

    @Query ("select * from DatabasePlatformUser where handle = :handle")
    fun getUser(handle: String): DatabasePlatformUser
}

@Dao
interface UpcomingContestsDao {
    @Query("select * from DatabaseContest order by start")
    fun getContests() : LiveData<List<DatabaseContest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg upcomingContests: DatabaseContest)
}

@Database(entities = [DatabasePlatformUser::class, DatabaseContest::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract val userRatingsDao: UserRatingsDao
    abstract val upcomingContestsDao: UpcomingContestsDao
}

private lateinit var INSTANCE: AppDatabase

fun getDatabase(context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            AppDatabase::class.java,
            "userRatings").fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}