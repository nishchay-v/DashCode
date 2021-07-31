package com.example.dashcode.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserAccountsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAccount(databaseAccount: DatabaseAccount): Long
    
    @Query("DELETE FROM DatabaseAccountContests WHERE accountId = :accountId")
    fun deleteAccountData(accountId: Int)

    @Query("DELETE FROM DatabaseAccount WHERE accountId = :accountId")
    fun deleteUser(accountId: Int)

    @Query("DELETE FROM DatabaseAccount")
    fun nukeTable()

    @Transaction
    @Query("SELECT * FROM DatabaseAccount")
    fun getAccountsWithContests(): LiveData<List<AccountWithContests>>

    @Transaction
    fun deleteAccountAndData(accountId: Int) {
        deleteUser(accountId)
        deleteAccountData(accountId)
    }
}

@Dao
interface UserContestsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg databaseAccountContests: DatabaseAccountContests)
}

@Dao
interface UpcomingContestsDao {
    @Query("select * FROM DatabaseContest ORDER BY start")
    fun getContests() : LiveData<List<DatabaseContest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg upcomingContests: DatabaseContest)
}

@Database(entities = [DatabaseAccount::class, DatabaseContest::class, DatabaseAccountContests::class], version = 3)
abstract class AppDatabase: RoomDatabase() {
    abstract val userAccountsDao: UserAccountsDao
    abstract val upcomingContestsDao: UpcomingContestsDao
    abstract val userContestsDao: UserContestsDao
}

private lateinit var INSTANCE: AppDatabase

fun getDatabase(context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            AppDatabase::class.java,
            "accounts").fallbackToDestructiveMigration().build()
        }
    }
    return INSTANCE
}