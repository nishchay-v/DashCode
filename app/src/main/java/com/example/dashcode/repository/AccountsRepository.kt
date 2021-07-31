package com.example.dashcode.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.dashcode.database.AppDatabase
import com.example.dashcode.database.asDomainModel
import com.example.dashcode.domain.PlatformUser
import com.example.dashcode.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountsRepository(private val database: AppDatabase) {

    // List of added accounts
    val accounts: LiveData<List<PlatformUser>?> =
        Transformations.map(database.userAccountsDao.getAccountsWithContests()) { accounts ->
            accounts.map { account -> account.asDomainModel() }
        }

    //LiveData to indicate if a account account is found or not
    private val _foundAccount = MutableLiveData<Boolean>()
    val foundAccount: LiveData<Boolean>
        get()=_foundAccount
    fun onAddUserComplete() {
        _foundAccount.value = null
    }

// Functions to invoke fetching of user data and add the data to the Room database

    suspend fun addCodeForcesAccount (handle: String)  {
        try {
            withContext(Dispatchers.IO) {
                val networkAccount = Network.codeforces.getAccountWithContestsAsync(handle).await()
                val id = database.userAccountsDao.insertAccount(networkAccount.asDatabaseAccountModel())
                Log.i("AccountRepository", "Account no. $id")
                database.userContestsDao.insertAll(*networkAccount.asDatabaseAccountContestsModel(id.toInt()))
            }
            _foundAccount.value = true
        }
        catch (e: Exception) {
            Log.i("AccountRepository", "User not found or $e")
            _foundAccount.value = false
        }
    }

    suspend fun addCodeChefAccount (handle: String) {
         try{
             withContext(Dispatchers.IO) {
                val networkAccount = Network.codechef.getAccountWithContestsAsync(handle).await()
                val id = database.userAccountsDao.insertAccount(networkAccount.asDatabaseAccountModel())
                Log.i("AccountRepository", "Account no. $id")
                database.userContestsDao.insertAll(*networkAccount.asDatabaseAccountContestsModel(id.toInt()))
            }
             _foundAccount.value = true
        }
         catch (e: Exception) {
             Log.i("AccountRepository", "User not found or $e")
             _foundAccount.value = false
         }
    }

    suspend fun addClistAccount (platform: String, handle: String) {
        try {
            withContext(Dispatchers.IO) {
                val networkAccount = Network.accountService.getAccountAsync(platform, handle).await()
                val networkAccountObj = networkAccount.asDomainModel()
                val networkContests = Network.accountContestService.getContestsAsync(networkAccountObj.clistId).await()
                val id = database.userAccountsDao.insertAccount(networkAccount.asDatabaseAccountModel())
                Log.i("AccountRepository", "Account no. $id")
                database.userContestsDao.insertAll(*networkContests.asDatabaseAccountContestsModel(id.toInt()))
            }
            _foundAccount.value = true
        }
        catch (e: Exception) {
            Log.i("AccountRepository", "User not found or $e")
            _foundAccount.value = false
        }
    }

    suspend fun removeAccount (accountId: Int) {
        withContext(Dispatchers.IO) {
            database.userAccountsDao.deleteAccountAndData(accountId)
        }
    }
}

